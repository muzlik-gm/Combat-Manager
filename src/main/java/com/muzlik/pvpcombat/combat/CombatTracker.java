package com.muzlik.pvpcombat.combat;

import com.muzlik.pvpcombat.data.CombatEvent;
import com.muzlik.pvpcombat.data.PlayerCombatData;
import com.muzlik.pvpcombat.performance.LagManager;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles combat detection and tracking of combat events.
 * Integrates with LagManager for performance-aware combat decisions.
 */
public class CombatTracker {

    private final Map<UUID, PlayerCombatData> playerData;
    private LagManager lagManager;

    public CombatTracker() {
        this.playerData = new ConcurrentHashMap<>();
    }

    /**
     * Sets the lag manager for performance monitoring integration.
     */
    public void setLagManager(LagManager lagManager) {
        this.lagManager = lagManager;
    }

    /**
     * Records a combat event.
     */
    public void recordEvent(CombatEvent event) {
        PlayerCombatData data = getPlayerData(event.getPlayerId());
        data.getEvents().add(event);
        data.updateLastActivity(System.currentTimeMillis());
        data.getStats().increment(event.getEventType());
    }

    /**
     * Gets or creates player combat data.
     */
    public PlayerCombatData getPlayerData(UUID playerId) {
        return playerData.computeIfAbsent(playerId, PlayerCombatData::new);
    }

    /**
     * Records damage dealt in combat.
     */
    public void recordDamageDealt(Player attacker, double damage) {
        PlayerCombatData data = getPlayerData(attacker.getUniqueId());
        data.addDamageDealt(damage);
        data.updateLastActivity(System.currentTimeMillis());

        // Update performance data for lag detection
        if (lagManager != null) {
            lagManager.updatePlayerPing(attacker);
        }
    }

    /**
     * Records damage received in combat.
     */
    public void recordDamageReceived(Player defender, double damage) {
        PlayerCombatData data = getPlayerData(defender.getUniqueId());
        data.addDamageReceived(damage);
        data.updateLastActivity(System.currentTimeMillis());

        // Update performance data for lag detection
        if (lagManager != null) {
            lagManager.updatePlayerPing(defender);
        }
    }

    /**
     * Records a combat win.
     */
    public void recordWin(Player winner) {
        PlayerCombatData data = getPlayerData(winner.getUniqueId());
        data.incrementWins();
        data.incrementCombats();
        data.updateLastActivity(System.currentTimeMillis());
        
        // Log for debugging
        System.out.println("[COMBAT] " + winner.getName() + " won! Total wins: " + data.getWins());
    }

    /**
     * Records a combat loss.
     */
    public void recordLoss(Player loser) {
        PlayerCombatData data = getPlayerData(loser.getUniqueId());
        data.incrementLosses();
        data.incrementCombats();
        data.updateLastActivity(System.currentTimeMillis());
        
        // Log for debugging
        System.out.println("[COMBAT] " + loser.getName() + " lost! Total losses: " + data.getLosses());
    }

    /**
     * Gets all player data (for persistence).
     */
    public Map<UUID, PlayerCombatData> getAllPlayerData() {
        return new ConcurrentHashMap<>(playerData);
    }

    /**
     * Clears old/inactive player data.
     */
    public void cleanupInactiveData() {
        long cutoff = System.currentTimeMillis() - 24L * 60 * 60 * 1000; // 24 hours
        playerData.entrySet().removeIf(entry -> entry.getValue().getLastActivity() < cutoff);
    }
}