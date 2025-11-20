package com.muzlik.pvpcombat.data;

import java.util.UUID;

/**
 * Abstract base class for combat-related events.
 */
public abstract class CombatEvent {
    protected final UUID sessionId;
    protected final UUID playerId;
    protected final long timestamp;
    protected final String eventType;

    public CombatEvent(UUID sessionId, UUID playerId, String eventType) {
        this.sessionId = sessionId;
        this.playerId = playerId;
        this.timestamp = System.currentTimeMillis();
        this.eventType = eventType;
    }

    // Getters
    public UUID getSessionId() { return sessionId; }

    public UUID getPlayerId() { return playerId; }

    public long getTimestamp() { return timestamp; }

    public String getEventType() { return eventType; }
}