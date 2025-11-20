package com.muzlik.pvpcombat.integration.crossserver;

import com.muzlik.pvpcombat.data.CombatSession;
import java.util.UUID;

/**
 * Data model for cross-server combat synchronization.
 * Contains essential combat session information for network transmission.
 */
public class CombatSyncData {
    private final UUID sessionId;
    private final UUID attackerId;
    private final UUID defenderId;
    private final String attackerName;
    private final String defenderName;
    private final String serverName;
    private final long startTime;
    private final int remainingSeconds;
    private final boolean active;

    public CombatSyncData(UUID sessionId, UUID attackerId, UUID defenderId,
                         String attackerName, String defenderName, String serverName,
                         long startTime, int remainingSeconds, boolean active) {
        this.sessionId = sessionId;
        this.attackerId = attackerId;
        this.defenderId = defenderId;
        this.attackerName = attackerName;
        this.defenderName = defenderName;
        this.serverName = serverName;
        this.startTime = startTime;
        this.remainingSeconds = remainingSeconds;
        this.active = active;
    }

    // Getters
    public UUID getSessionId() { return sessionId; }
    public UUID getAttackerId() { return attackerId; }
    public UUID getDefenderId() { return defenderId; }
    public String getAttackerName() { return attackerName; }
    public String getDefenderName() { return defenderName; }
    public String getServerName() { return serverName; }
    public long getStartTime() { return startTime; }
    public int getRemainingSeconds() { return remainingSeconds; }
    public boolean isActive() { return active; }

    /**
     * Creates CombatSyncData from a CombatSession.
     */
    public static CombatSyncData fromSession(CombatSession session, String serverName) {
        return new CombatSyncData(
            session.getSessionId(),
            session.getAttacker().getUniqueId(),
            session.getDefender().getUniqueId(),
            session.getAttacker().getName(),
            session.getDefender().getName(),
            serverName,
            session.getStartTime(),
            session.getRemainingTime(),
            session.isActive()
        );
    }

    @Override
    public String toString() {
        return String.format("CombatSyncData{sessionId=%s, attacker=%s, defender=%s, server=%s, remaining=%ds, active=%s}",
                           sessionId, attackerName, defenderName, serverName, remainingSeconds, active);
    }
}