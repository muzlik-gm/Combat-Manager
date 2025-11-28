package com.muzlik.pvpcombat.restrictions;

import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import com.muzlik.pvpcombat.data.RestrictionData;
import org.bukkit.entity.Player;

/**
 * Handles golden apple usage restrictions during combat.
 */
public class GoldenAppleRestriction {
    private final RestrictionManager restrictionManager;

    public GoldenAppleRestriction(RestrictionManager restrictionManager) {
        this.restrictionManager = restrictionManager;
    }

    /**
     * Checks if player can use golden apple.
     */
    public boolean canUseGoldenApple(Player player) {
        if (!PvPCombatPlugin.getInstance().getConfig().getBoolean("restrictions.golden-apple.enabled", true)) {
            return true;
        }

        if (!restrictionManager.getCombatManager().isInCombat(player)) {
            return true;
        }

        // Check if usage is completely blocked during combat
        if (PvPCombatPlugin.getInstance().getConfig().getBoolean("restrictions.golden-apple.block-usage", false)) {
            return false;
        }

        // Check cooldown
        RestrictionData restrictionData = restrictionManager.getOrCreateRestrictionData(player);
        return !restrictionData.isOnCooldown("golden_apple");
    }

    /**
     * Checks if player can use enchanted golden apple.
     */
    public boolean canUseEnchantedGoldenApple(Player player) {
        if (!PvPCombatPlugin.getInstance().getConfig().getBoolean("restrictions.enchanted-golden-apple.enabled", true)) {
            return true;
        }

        if (!restrictionManager.getCombatManager().isInCombat(player)) {
            return true;
        }

        // Check if usage is completely blocked during combat
        if (PvPCombatPlugin.getInstance().getConfig().getBoolean("restrictions.enchanted-golden-apple.block-usage", false)) {
            return false;
        }

        // Check cooldown
        RestrictionData restrictionData = restrictionManager.getOrCreateRestrictionData(player);
        return !restrictionData.isOnCooldown("enchanted_golden_apple");
    }

    /**
     * Calculates golden apple cooldown based on combat status.
     */
    private int calculateGoldenAppleCooldown(Player player) {
        int baseCooldown = PvPCombatPlugin.getInstance().getConfig().getInt("restrictions.golden-apple.cooldown", 30);
        
        if (restrictionManager.getCombatManager().isInCombat(player)) {
            double multiplier = PvPCombatPlugin.getInstance().getConfig().getDouble("restrictions.golden-apple.combat-cooldown-multiplier", 1.5);
            return (int) (baseCooldown * multiplier);
        }
        
        return baseCooldown;
    }

    /**
     * Calculates enchanted golden apple cooldown based on combat status.
     */
    private int calculateEnchantedGoldenAppleCooldown(Player player) {
        int baseCooldown = PvPCombatPlugin.getInstance().getConfig().getInt("restrictions.enchanted-golden-apple.cooldown", 60);
        
        if (restrictionManager.getCombatManager().isInCombat(player)) {
            double multiplier = PvPCombatPlugin.getInstance().getConfig().getDouble("restrictions.enchanted-golden-apple.combat-cooldown-multiplier", 2.0);
            return (int) (baseCooldown * multiplier);
        }
        
        return baseCooldown;
    }

    /**
     * Applies golden apple usage restrictions.
     */
    public void onGoldenAppleUsed(Player player, RestrictionData restrictionData) {
        int cooldownSeconds = calculateGoldenAppleCooldown(player);

        if (cooldownSeconds > 0) {
            restrictionData.setCooldown("golden_apple", cooldownSeconds);
            restrictionManager.applyCooldown(player, "golden_apple", cooldownSeconds);
            
            // Apply actual Minecraft cooldown to the player
            int cooldownTicks = cooldownSeconds * 20;
            player.setCooldown(org.bukkit.Material.GOLDEN_APPLE, cooldownTicks);
            
            // Send message to player
            player.sendMessage(org.bukkit.ChatColor.YELLOW + "Golden Apple cooldown: " + cooldownSeconds + " seconds");
        }

        restrictionData.setLastGoldenAppleUse(java.time.LocalDateTime.now());
    }

    /**
     * Applies enchanted golden apple usage restrictions.
     */
    public void onEnchantedGoldenAppleUsed(Player player, RestrictionData restrictionData) {
        int cooldownSeconds = calculateEnchantedGoldenAppleCooldown(player);

        if (cooldownSeconds > 0) {
            restrictionData.setCooldown("enchanted_golden_apple", cooldownSeconds);
            restrictionManager.applyCooldown(player, "enchanted_golden_apple", cooldownSeconds);
            
            // Apply actual Minecraft cooldown to the player
            int cooldownTicks = cooldownSeconds * 20;
            player.setCooldown(org.bukkit.Material.ENCHANTED_GOLDEN_APPLE, cooldownTicks);
            
            // Send message to player
            player.sendMessage(org.bukkit.ChatColor.GOLD + "Enchanted Golden Apple cooldown: " + cooldownSeconds + " seconds");
        }

        restrictionData.setLastEnchantedGoldenAppleUse(java.time.LocalDateTime.now());
    }
}
