package com.muzlik.pvpcombat.restrictions;

import com.muzlik.pvpcombat.data.RestrictionData;
import org.bukkit.entity.Player;
import com.muzlik.pvpcombat.core.PvPCombatPlugin;

/**
 * Handles ender pearl restrictions during combat.
 */
public class EnderPearlRestriction {

    private final RestrictionManager restrictionManager;

    public EnderPearlRestriction(RestrictionManager restrictionManager) {
        this.restrictionManager = restrictionManager;
    }

    /**
     * Checks if a player can use an ender pearl.
     */
    public boolean canUse(Player player, RestrictionData restrictionData) {
        // Check for active ender pearl cooldown
        if (restrictionData.isOnCooldown("ender_pearl")) {
            return false;
        }

        // Check if pearl usage is blocked entirely during combat
        if (isPearlUsageBlocked(player)) {
            return false;
        }

        return true;
    }

    /**
     * Applies ender pearl usage restrictions.
     */
    public void onEnderPearlUsed(Player player, RestrictionData restrictionData) {
        // Calculate cooldown based on combat status and configuration
        int cooldown = calculateCooldown(player);

        if (cooldown > 0) {
            restrictionData.setCooldown("ender_pearl", cooldown);
            restrictionManager.applyCooldown(player, "ender_pearl", cooldown);
        }

        restrictionData.setLastEnderPearlUse(java.time.LocalDateTime.now());
    }

    /**
     * Checks if ender pearl usage is blocked entirely during combat.
     */
    private boolean isPearlUsageBlocked(Player player) {
        // Config-based block-usage check.
        return PvPCombatPlugin.getInstance().getConfig().getBoolean("restrictions.enderpearl.block-usage", false);
    }

    /**
     * Calculates the cooldown for ender pearl usage.
     */
    private int calculateCooldown(Player player) {
        if (!restrictionManager.getCombatManager().isInCombat(player)) {
            return 0;
        }
        // Config-based cooldown multiplier.
        int baseCooldown = PvPCombatPlugin.getInstance().getConfig().getInt("restrictions.enderpearl.cooldown", 10);
        double multiplier = PvPCombatPlugin.getInstance().getConfig().getDouble("restrictions.enderpearl.combat-cooldown-multiplier", 2.0);
        return (int) (baseCooldown * multiplier);
    }

    /**
     * Gets the remaining cooldown time for ender pearls.
     */
    public int getRemainingCooldown(Player player, RestrictionData restrictionData) {
        if (!restrictionData.isOnCooldown("ender_pearl")) {
            return 0;
        }

        // Calculate remaining seconds
        java.time.LocalDateTime expiry = restrictionData.getActiveCooldowns().get("ender_pearl");
        if (expiry != null) {
            long secondsLeft = java.time.Duration.between(java.time.LocalDateTime.now(), expiry).getSeconds();
            return Math.max(0, (int) secondsLeft);
        }

        return 0;
    }
}