package com.muzlik.pvpcombat.restrictions;

import com.muzlik.pvpcombat.data.RestrictionData;
import org.bukkit.entity.Player;

/**
 * Handles trident usage restrictions during combat.
 */
public class TridentRestriction {

    private final RestrictionManager restrictionManager;

    public TridentRestriction(RestrictionManager restrictionManager) {
        this.restrictionManager = restrictionManager;
    }

    /**
     * Checks if a player can throw a trident.
     */
    public boolean canThrowTrident(Player player, RestrictionData restrictionData) {
        // Check if player is in combat
        if (!restrictionManager.getCombatManager().isInCombat(player)) {
            return true;
        }

        // Check if trident restrictions are enabled
        boolean enabled = com.muzlik.pvpcombat.core.PvPCombatPlugin.getInstance()
            .getConfig().getBoolean("restrictions.trident.enabled", true);
        if (!enabled) {
            return true;
        }

        // Check if throwing is blocked
        boolean blockThrowing = com.muzlik.pvpcombat.core.PvPCombatPlugin.getInstance()
            .getConfig().getBoolean("restrictions.trident.block-throwing", true);
        if (blockThrowing) {
            return false;
        }

        // Check cooldown
        return !restrictionData.isOnCooldown("trident");
    }

    /**
     * Checks if a player can use riptide enchantment.
     */
    public boolean canUseRiptide(Player player) {
        // Check if player is in combat
        if (!restrictionManager.getCombatManager().isInCombat(player)) {
            return true;
        }

        // Check if trident restrictions are enabled
        boolean enabled = com.muzlik.pvpcombat.core.PvPCombatPlugin.getInstance()
            .getConfig().getBoolean("restrictions.trident.enabled", true);
        if (!enabled) {
            return true;
        }

        // Check if riptide is blocked
        return !com.muzlik.pvpcombat.core.PvPCombatPlugin.getInstance()
            .getConfig().getBoolean("restrictions.trident.block-riptide", true);
    }

    /**
     * Applies trident cooldown after throwing.
     */
    public void onTridentThrown(Player player, RestrictionData restrictionData) {
        int baseCooldown = com.muzlik.pvpcombat.core.PvPCombatPlugin.getInstance()
            .getConfig().getInt("restrictions.trident.cooldown", 5);
        
        double multiplier = com.muzlik.pvpcombat.core.PvPCombatPlugin.getInstance()
            .getConfig().getDouble("restrictions.trident.combat-cooldown-multiplier", 2.0);

        int cooldown = restrictionManager.getCombatManager().isInCombat(player) 
            ? (int) (baseCooldown * multiplier) 
            : baseCooldown;

        restrictionData.setCooldown("trident", cooldown);
    }
}
