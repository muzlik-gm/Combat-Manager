package com.muzlik.pvpcombat.restrictions;

import com.muzlik.pvpcombat.data.RestrictionData;
import org.bukkit.entity.Player;
import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Handles elytra restrictions during combat.
 */
public class ElytraRestriction {

    private final RestrictionManager restrictionManager;

    public ElytraRestriction(RestrictionManager restrictionManager) {
        this.restrictionManager = restrictionManager;
    }

    /**
     * Checks if a player can use elytra (glide or boost).
     */
    public boolean canUse(Player player, RestrictionData restrictionData) {
        // Check if glide is blocked
        if (restrictionData.isElytraGlideBlocked()) {
            return false;
        }

        // Check for active elytra cooldown
        if (restrictionData.isOnCooldown("elytra")) {
            return false;
        }

        // Check altitude-based restrictions
        if (isAltitudeRestricted(player)) {
            return false;
        }

        // Check time-based restrictions
        if (isTimeRestricted(player)) {
            return false;
        }

        return true;
    }

    /**
     * Handles elytra glide activation.
     */
    public void onGlideStart(Player player, RestrictionData restrictionData) {
        double currentAltitude = player.getLocation().getY();

        // Store glide start altitude
        restrictionData.setLastGlideStart(java.time.LocalDateTime.now());
        restrictionData.setAltitudeAtGlideStart(currentAltitude);

        // Check if glide should be blocked
        if (shouldBlockGlide(player, currentAltitude)) {
            restrictionData.setElytraGlideBlocked(true);
            player.setGliding(false);
        }
    }

    /**
     * Handles elytra boost/firework usage.
     */
    public void onElytraBoost(Player player, RestrictionData restrictionData) {
        // Apply cooldown for boosting
        int boostCooldown = calculateBoostCooldown(player);
        if (boostCooldown > 0) {
            restrictionData.setCooldown("elytra_boost", boostCooldown);
        }

        restrictionData.setLastElytraBoost(java.time.LocalDateTime.now());
    }

    /**
     * Handles elytra takeoff attempt.
     */
    public void onTakeoffAttempt(Player player, RestrictionData restrictionData) {
        // Check if takeoff is allowed
        if (!canTakeoff(player)) {
            player.setGliding(false);
            return;
        }

        // Store takeoff data
        restrictionData.setLastGlideStart(java.time.LocalDateTime.now());
        restrictionData.setAltitudeAtGlideStart(player.getLocation().getY());
    }

    /**
     * Checks if glide should be blocked based on altitude.
     */
    private boolean shouldBlockGlide(Player player, double altitude) {
        // Config min safe height (config.getDouble("elytra.min-height", 100)).
        double minHeight = PvPCombatPlugin.getInstance().getConfig().getDouble("restrictions.elytra.min-safe-height", 10.0);
        return altitude < minHeight;
    }

    /**
     * Checks if player can takeoff with elytra.
     */
    private boolean canTakeoff(Player player) {
        // Takeoff restrictions (e.g., fireworks boost check, cancel if config).
        boolean blockTakeoff = PvPCombatPlugin.getInstance().getConfig().getBoolean("restrictions.elytra.block-takeoff", true);
        if (blockTakeoff) {
            return false;
        }
        // Fireworks boost check
        if (player.getInventory().getItemInMainHand().getType() == Material.FIREWORK_ROCKET) {
            return false;
        }
        return true;
    }

    /**
     * Checks if altitude-based restrictions apply.
     */
    private boolean isAltitudeRestricted(Player player) {
        String worldName = player.getWorld().getName();
        // World-specific altitude (config.getConfigurationSection("elytra.worlds." + world)).
        ConfigurationSection worldSection = PvPCombatPlugin.getInstance().getConfig().getConfigurationSection("restrictions.elytra.worlds." + worldName);
        if (worldSection != null) {
            double minHeight = worldSection.getDouble("min-safe-height", -1);
            if (minHeight > 0 && player.getLocation().getY() < minHeight) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if time-based restrictions apply.
     */
    private boolean isTimeRestricted(Player player) {
        long time = player.getWorld().getTime();
        // Day/night restrictions (config.getBoolean("elytra.restrict-night") && time > night).
        boolean blockNight = PvPCombatPlugin.getInstance().getConfig().getBoolean("restrictions.elytra.time-restrictions.block-at-night", false);
        boolean isNight = time >= 13000 || time <= 1000;
        if (blockNight && isNight) {
            return true;
        }
        return false;
    }

    /**
     * Calculates boost cooldown.
     */
    private int calculateBoostCooldown(Player player) {
        if (!restrictionManager.getCombatManager().isInCombat(player)) {
            return 0;
        }
        // Config boost cooldown.
        return PvPCombatPlugin.getInstance().getConfig().getInt("restrictions.elytra.boost-cooldown", 30);
    }

    /**
     * Gets remaining cooldown for elytra actions.
     */
    public int getRemainingCooldown(Player player, RestrictionData restrictionData, String actionType) {
        if (!restrictionData.isOnCooldown(actionType)) {
            return 0;
        }

        java.time.LocalDateTime expiry = restrictionData.getActiveCooldowns().get(actionType);
        if (expiry != null) {
            long secondsLeft = java.time.Duration.between(java.time.LocalDateTime.now(), expiry).getSeconds();
            return Math.max(0, (int) secondsLeft);
        }

        return 0;
    }
}