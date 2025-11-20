package com.muzlik.pvpcombat.logging;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data model for individual combat events with detailed statistical tracking.
 */
public class CombatLogEntry {
    public enum EventType {
        COMBAT_START,
        DAMAGE_DEALT,
        DAMAGE_RECEIVED,
        HIT_LANDED,
        HIT_MISSED,
        KNOCKBACK_GIVEN,
        KNOCKBACK_RECEIVED,
        MOVEMENT,
        COMBAT_END,
        TIMER_RESET,
        INTERFERENCE_DETECTED
    }

    private final UUID sessionId;
    private final UUID playerId;
    private final UUID targetId;
    private final EventType eventType;
    private final LocalDateTime timestamp;
    private final double damage;
    private final boolean hitLanded;
    private final double distance;
    private final double knockbackForce;
    private final String weaponType;
    private final String location;
    private final String additionalData;

    private CombatLogEntry(Builder builder) {
        this.sessionId = builder.sessionId;
        this.playerId = builder.playerId;
        this.targetId = builder.targetId;
        this.eventType = builder.eventType;
        this.timestamp = builder.timestamp;
        this.damage = builder.damage;
        this.hitLanded = builder.hitLanded;
        this.distance = builder.distance;
        this.knockbackForce = builder.knockbackForce;
        this.weaponType = builder.weaponType;
        this.location = builder.location;
        this.additionalData = builder.additionalData;
    }

    // Getters
    public UUID getSessionId() { return sessionId; }
    public UUID getPlayerId() { return playerId; }
    public UUID getTargetId() { return targetId; }
    public EventType getEventType() { return eventType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getDamage() { return damage; }
    public boolean isHitLanded() { return hitLanded; }
    public double getDistance() { return distance; }
    public double getKnockbackForce() { return knockbackForce; }
    public String getWeaponType() { return weaponType; }
    public String getLocation() { return location; }
    public String getAdditionalData() { return additionalData; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s: %s (Session: %s)",
            timestamp, playerId, eventType, additionalData, sessionId);
    }

    public static class Builder {
        private UUID sessionId;
        private UUID playerId;
        private UUID targetId;
        private EventType eventType;
        private LocalDateTime timestamp = LocalDateTime.now();
        private double damage = 0.0;
        private boolean hitLanded = false;
        private double distance = 0.0;
        private double knockbackForce = 0.0;
        private String weaponType = "";
        private String location = "";
        private String additionalData = "";

        public Builder sessionId(UUID sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder playerId(UUID playerId) {
            this.playerId = playerId;
            return this;
        }

        public Builder targetId(UUID targetId) {
            this.targetId = targetId;
            return this;
        }

        public Builder eventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder damage(double damage) {
            this.damage = damage;
            return this;
        }

        public Builder hitLanded(boolean hitLanded) {
            this.hitLanded = hitLanded;
            return this;
        }

        public Builder distance(double distance) {
            this.distance = distance;
            return this;
        }

        public Builder knockbackForce(double knockbackForce) {
            this.knockbackForce = knockbackForce;
            return this;
        }

        public Builder weaponType(String weaponType) {
            this.weaponType = weaponType;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder additionalData(String additionalData) {
            this.additionalData = additionalData;
            return this;
        }

        public CombatLogEntry build() {
            if (sessionId == null || playerId == null || eventType == null) {
                throw new IllegalStateException("sessionId, playerId, and eventType are required");
            }
            return new CombatLogEntry(this);
        }
    }
}