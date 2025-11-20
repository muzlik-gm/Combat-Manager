package com.muzlik.pvpcombat.restrictions;

import com.muzlik.pvpcombat.combat.CombatManager;
import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import com.muzlik.pvpcombat.data.RestrictionData;
import com.muzlik.pvpcombat.interfaces.IRestrictionManager;
import com.muzlik.pvpcombat.utils.CacheManager;
import org.bukkit.entity.Player;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Main restriction manager implementing IRestrictionManager interface.
 * Handles all movement and ability restrictions during combat.
 */
public class RestrictionManager implements IRestrictionManager {

    private final CombatManager combatManager;
    private final EnderPearlRestriction enderPearlRestriction;
    private final ElytraRestriction elytraRestriction;
    private final Map<Player, RestrictionData> playerRestrictions;
    private final CacheManager cacheManager;

    public RestrictionManager(CombatManager combatManager, CacheManager cacheManager) {
        this.combatManager = combatManager;
        this.cacheManager = cacheManager;
        this.enderPearlRestriction = new EnderPearlRestriction(this);
        this.elytraRestriction = new ElytraRestriction(this);
        this.playerRestrictions = new ConcurrentHashMap<>();
    }

    @Override
    public boolean canUseEnderPearl(Player player) {
        if (!combatManager.isInCombat(player)) {
            return true;
        }

        // Check cache first
        String cacheKey = player.getUniqueId() + ":enderpearl";
        Boolean cached = (Boolean) cacheManager.get("restriction-data", cacheKey);
        if (cached != null) {
            return cached;
        }

        RestrictionData restrictionData = getOrCreateRestrictionData(player);
        boolean canUse = enderPearlRestriction.canUse(player, restrictionData);

        // Cache the result for short time
        cacheManager.put("restriction-data", cacheKey, canUse);
        return canUse;
    }

    @Override
    public boolean canUseElytra(Player player) {
        if (!combatManager.isInCombat(player)) {
            return true;
        }

        // Check cache first
        String cacheKey = player.getUniqueId() + ":elytra";
        Boolean cached = (Boolean) cacheManager.get("restriction-data", cacheKey);
        if (cached != null) {
            return cached;
        }

        RestrictionData restrictionData = getOrCreateRestrictionData(player);
        boolean canUse = elytraRestriction.canUse(player, restrictionData);

        // Cache the result for short time
        cacheManager.put("restriction-data", cacheKey, canUse);
        return canUse;
    }

    @Override
    public boolean canBreakBlocks(Player player) {
        if (!combatManager.isInCombat(player)) {
            return true;
        }
        // Block breaking restrictions (config.getBoolean("restrictions.block-break")).
        return !PvPCombatPlugin.getInstance().getConfig().getBoolean("restrictions.block-break", true);
    }

    @Override
    public boolean canPlaceBlocks(Player player) {
        if (!combatManager.isInCombat(player)) {
            return true;
        }
        // Block placing restrictions.
        return !PvPCombatPlugin.getInstance().getConfig().getBoolean("restrictions.block-place", true);
    }
    public boolean canTeleport(Player player) {
        if (!combatManager.isInCombat(player)) {
            return true;
        }
        // Teleport restrictions.
        return !PvPCombatPlugin.getInstance().getConfig().getBoolean("restrictions.teleport", true);
    }

    @Override
    public void applyCooldown(Player player, String actionType, int cooldownSeconds) {
        RestrictionData restrictionData = getOrCreateRestrictionData(player);
        restrictionData.setCooldown(actionType, cooldownSeconds);
    }

    /**
     * Gets or creates restriction data for a player.
     */
    public RestrictionData getOrCreateRestrictionData(Player player) {
        return playerRestrictions.computeIfAbsent(player, p -> new RestrictionData(p.getUniqueId()));
    }

    /**
     * Removes restriction data for a player (when they leave combat).
     */
    public void removeRestrictionData(Player player) {
        RestrictionData removed = playerRestrictions.remove(player);
        if (removed != null) {
            removed.clearAllRestrictions();
        }
    }

    /**
     * Clears all restrictions for a player.
     */
    public void clearRestrictions(Player player) {
        RestrictionData restrictionData = playerRestrictions.get(player);
        if (restrictionData != null) {
            restrictionData.clearAllRestrictions();
        }
    }

    // Getters for specific restrictions
    public EnderPearlRestriction getEnderPearlRestriction() { return enderPearlRestriction; }
    public ElytraRestriction getElytraRestriction() { return elytraRestriction; }
    public CombatManager getCombatManager() { return combatManager; }
}