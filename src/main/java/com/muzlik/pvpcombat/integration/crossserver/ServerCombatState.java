package com.muzlik.pvpcombat.integration.crossserver;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks cross-server combat states for all players across the network.
 * Maintains a global view of which players are in combat on which servers.
 */
public class ServerCombatState {

    private final Map<UUID, CombatSyncData> globalCombatStates;
    private final String localServerName;

    public ServerCombatState(String localServerName) {
        this.globalCombatStates = new ConcurrentHashMap<>();
        this.localServerName = localServerName;
    }

    /**
     * Updates the combat state for a player across the network.
     */
    public void updateCombatState(UUID playerId, CombatSyncData syncData) {
        if (syncData.isActive()) {
            globalCombatStates.put(playerId, syncData);
        } else {
            globalCombatStates.remove(playerId);
        }
    }

    /**
     * Checks if a player is in combat anywhere on the network.
     */
    public boolean isPlayerInCombat(UUID playerId) {
        CombatSyncData state = globalCombatStates.get(playerId);
        return state != null && state.isActive();
    }

    /**
     * Gets the combat state for a specific player.
     */
    public CombatSyncData getPlayerCombatState(UUID playerId) {
        return globalCombatStates.get(playerId);
    }

    /**
     * Gets all active combat sessions on the network.
     */
    public Map<UUID, CombatSyncData> getAllActiveCombats() {
        return new ConcurrentHashMap<>(globalCombatStates);
    }

    /**
     * Gets combat sessions active on a specific server.
     */
    public Map<UUID, CombatSyncData> getCombatSessionsForServer(String serverName) {
        Map<UUID, CombatSyncData> serverSessions = new ConcurrentHashMap<>();
        for (Map.Entry<UUID, CombatSyncData> entry : globalCombatStates.entrySet()) {
            if (entry.getValue().getServerName().equals(serverName)) {
                serverSessions.put(entry.getKey(), entry.getValue());
            }
        }
        return serverSessions;
    }

    /**
     * Gets the local server name.
     */
    public String getLocalServerName() {
        return localServerName;
    }

    /**
     * Cleans up expired combat states.
     * Should be called periodically to remove stale data.
     */
    public void cleanupExpiredStates() {
        long currentTime = System.currentTimeMillis();
        globalCombatStates.entrySet().removeIf(entry -> {
            CombatSyncData data = entry.getValue();
            // Remove if inactive or if session should have expired (with buffer)
            return !data.isActive() ||
                   (currentTime - data.getStartTime()) > (data.getRemainingSeconds() + 300) * 1000L; // 5 min buffer
        });
    }

    /**
     * Gets the total number of active combat sessions across the network.
     */
    public int getTotalActiveCombats() {
        return globalCombatStates.size();
    }
}