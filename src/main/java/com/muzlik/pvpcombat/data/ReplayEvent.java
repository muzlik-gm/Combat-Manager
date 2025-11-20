package com.muzlik.pvpcombat.data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lightweight event data class for combat replay system.
 * Optimized for memory efficiency and fast serialization.
 */
public class ReplayEvent {

    public enum ReplayEventType {
        HIT_LANDED,
        HIT_MISSED,
        MOVEMENT,
        ABILITY_USED,
        DAMAGE_DEALT,
        KNOCKBACK,
        COMBAT_END
    }

    private final UUID playerId;
    private final UUID targetId;
    private final ReplayEventType eventType;
    private final LocalDateTime timestamp;
    private final double damage;
    private final boolean critical;
    private final String location; // Compressed location as "x,y,z"
    private final String weaponType;
    private final String additionalData;

    private ReplayEvent(Builder builder) {
        this.playerId = builder.playerId;
        this.targetId = builder.targetId;
        this.eventType = builder.eventType;
        this.timestamp = builder.timestamp;
        this.damage = builder.damage;
        this.critical = builder.critical;
        this.location = builder.location;
        this.weaponType = builder.weaponType;
        this.additionalData = builder.additionalData;
    }

    // Getters
    public UUID getPlayerId() { return playerId; }
    public UUID getTargetId() { return targetId; }
    public ReplayEventType getEventType() { return eventType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getDamage() { return damage; }
    public boolean isCritical() { return critical; }
    public String getLocation() { return location; }
    public String getWeaponType() { return weaponType; }
    public String getAdditionalData() { return additionalData; }

    /**
     * Calculates memory footprint of this event in bytes.
     */
    public int getMemoryFootprint() {
        return 16 + 16 + 4 + 24 + 8 + 1 + // UUIDs, enum, timestamp, double, boolean
               (location != null ? location.length() * 2 : 0) +
               (weaponType != null ? weaponType.length() * 2 : 0) +
               (additionalData != null ? additionalData.length() * 2 : 0) + 32; // overhead
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s: %.1f dmg %s",
            timestamp, playerId, eventType, damage, critical ? "(crit)" : "");
    }

    public static class Builder {
        private UUID playerId;
        private UUID targetId;
        private ReplayEventType eventType;
        private LocalDateTime timestamp = LocalDateTime.now();
        private double damage = 0.0;
        private boolean critical = false;
        private String location = "";
        private String weaponType = "";
        private String additionalData = "";

        public Builder playerId(UUID playerId) {
            this.playerId = playerId;
            return this;
        }

        public Builder targetId(UUID targetId) {
            this.targetId = targetId;
            return this;
        }

        public Builder eventType(ReplayEventType eventType) {
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

        public Builder critical(boolean critical) {
            this.critical = critical;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder weaponType(String weaponType) {
            this.weaponType = weaponType;
            return this;
        }

        public Builder additionalData(String additionalData) {
            this.additionalData = additionalData;
            return this;
        }

        public ReplayEvent build() {
            if (playerId == null || eventType == null) {
                throw new IllegalStateException("playerId and eventType are required");
            }
            return new ReplayEvent(this);
        }
    }
}