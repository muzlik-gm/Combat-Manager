package com.muzlik.pvpcombat.restrictions;

import org.bukkit.entity.Player;

/**
 * Handles end crystal usage restrictions during combat.
 */
public class CrystalRestriction {

    private final RestrictionManager restrictionManager;

    public CrystalRestriction(RestrictionManager restrictionManager) {
        this.restrictionManager = restrictionManager;
    }

    /**
     * Checks if a player can place an end crystal.
     */
    public boolean canPlaceCrystal(Player player) {
        // Check if player is in combat
        if (!restrictionManager.getCombatManager().isInCombat(player)) {
            return true;
        }

        // Check if crystal restrictions are enabled
        boolean enabled = com.muzlik.pvpcombat.core.PvPCombatPlugin.getInstance()
            .getConfig().getBoolean("restrictions.crystal.enabled", true);
        if (!enabled) {
            return true;
        }

        // Check if placement is blocked
        return !com.muzlik.pvpcombat.core.PvPCombatPlugin.getInstance()
            .getConfig().getBoolean("restrictions.crystal.block-placement", true);
    }

    /**
     * Checks if a player can break an end crystal.
     */
    public boolean canBreakCrystal(Player player) {
        // Check if player is in combat
        if (!restrictionManager.getCombatManager().isInCombat(player)) {
            return true;
        }

        // Check if crystal restrictions are enabled
        boolean enabled = com.muzlik.pvpcombat.core.PvPCombatPlugin.getInstance()
            .getConfig().getBoolean("restrictions.crystal.enabled", true);
        if (!enabled) {
            return true;
        }

        // Check if breaking is blocked
        return !com.muzlik.pvpcombat.core.PvPCombatPlugin.getInstance()
            .getConfig().getBoolean("restrictions.crystal.block-breaking", false);
    }
}
