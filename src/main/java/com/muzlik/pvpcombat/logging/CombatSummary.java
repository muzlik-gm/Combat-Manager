package com.muzlik.pvpcombat.logging;

import com.muzlik.pvpcombat.logging.CombatLogEntry.EventType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Generates statistical summaries for combat sessions.
 */
public class CombatSummary {

    private final List<CombatLogEntry> entries;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    // Calculated statistics
    private int totalAttacks;
    private int hitsLanded;
    private double totalDamageDealt;
    private double totalDamageReceived;
    private int knockbackExchanges;
    private double accuracy;
    private long combatDurationSeconds;

    public CombatSummary(List<CombatLogEntry> entries) {
        this.entries = entries;
        this.startTime = findStartTime();
        this.endTime = findEndTime();
        calculateStatistics();
    }
    
    /**
     * Constructor with session data for accurate damage tracking.
     */
    public CombatSummary(List<CombatLogEntry> entries, com.muzlik.pvpcombat.data.CombatSession session, org.bukkit.entity.Player player) {
        this.entries = entries;
        this.startTime = findStartTime();
        this.endTime = findEndTime();
        calculateStatistics();
        
        // Override values with session data
        if (session != null && player != null) {
            this.totalDamageDealt = session.getDamageDealt(player);
            this.totalDamageReceived = session.getDamageReceived(player);
            this.hitsLanded = session.getHitsLanded(player);
            
            // Calculate total hits exchanged (both players)
            int opponentHits = session.getHitsLanded(session.getOpponent(player));
            this.totalAttacks = this.hitsLanded + opponentHits;
            
            // Calculate actual combat duration from session
            long durationMs = System.currentTimeMillis() - session.getStartTime();
            this.combatDurationSeconds = durationMs / 1000;
            
            // Calculate accuracy (your hits / total hits exchanged)
            if (this.totalAttacks > 0) {
                this.accuracy = ((double) this.hitsLanded / this.totalAttacks) * 100.0;
            }
        }
    }

    /**
     * Finds the combat start time.
     */
    private LocalDateTime findStartTime() {
        return entries.stream()
            .filter(e -> e.getEventType() == EventType.COMBAT_START)
            .map(CombatLogEntry::getTimestamp)
            .findFirst()
            .orElse(entries.isEmpty() ? LocalDateTime.now() : entries.get(0).getTimestamp());
    }

    /**
     * Finds the combat end time.
     */
    private LocalDateTime findEndTime() {
        return entries.stream()
            .filter(e -> e.getEventType() == EventType.COMBAT_END)
            .map(CombatLogEntry::getTimestamp)
            .findFirst()
            .orElse(entries.isEmpty() ? LocalDateTime.now() : entries.get(entries.size() - 1).getTimestamp());
    }

    /**
     * Calculates all combat statistics.
     */
    private void calculateStatistics() {
        totalAttacks = 0;
        hitsLanded = 0;
        totalDamageDealt = 0.0;
        totalDamageReceived = 0.0;
        knockbackExchanges = 0;

        for (CombatLogEntry entry : entries) {
            switch (entry.getEventType()) {
                case HIT_LANDED:
                case HIT_MISSED:
                    totalAttacks++;
                    if (entry.isHitLanded()) {
                        hitsLanded++;
                        totalDamageDealt += entry.getDamage();
                    }
                    break;
                case DAMAGE_RECEIVED:
                    totalDamageReceived += entry.getDamage();
                    break;
                case KNOCKBACK_GIVEN:
                case KNOCKBACK_RECEIVED:
                    knockbackExchanges++;
                    break;
            }
        }

        accuracy = totalAttacks > 0 ? (double) hitsLanded / totalAttacks * 100.0 : 0.0;
        combatDurationSeconds = Duration.between(startTime, endTime).getSeconds();
    }

    // Getters
    public int getTotalAttacks() { return totalAttacks; }
    public int getHitsLanded() { return hitsLanded; }
    public double getAccuracy() { return accuracy; }
    public double getTotalDamageDealt() { return totalDamageDealt; }
    public double getTotalDamageReceived() { return totalDamageReceived; }
    public int getKnockbackExchanges() { return knockbackExchanges; }
    public long getCombatDurationSeconds() { return combatDurationSeconds; }

    /**
     * Formats duration as a readable string.
     */
    public String getFormattedDuration() {
        long minutes = combatDurationSeconds / 60;
        long seconds = combatDurationSeconds % 60;
        return String.format("%dm %ds", minutes, seconds);
    }

    /**
     * Gets a detailed summary as a formatted string.
     */
    public String getDetailedSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Combat Statistics ===\n");
        sb.append(String.format("Duration: %s\n", getFormattedDuration()));
        sb.append(String.format("Attacks: %d total, %d landed (%.1f%% accuracy)\n",
                               totalAttacks, hitsLanded, accuracy));
        sb.append(String.format("Damage: %.1f dealt, %.1f received\n",
                               totalDamageDealt, totalDamageReceived));
        sb.append(String.format("Knockback Exchanges: %d\n", knockbackExchanges));

        // Movement patterns (simplified)
        long movementEvents = entries.stream()
            .filter(e -> e.getEventType() == EventType.MOVEMENT)
            .count();
        sb.append(String.format("Movement Events: %d\n", movementEvents));

        return sb.toString();
    }

    /**
     * Gets summary statistics for a specific player.
     */
    public PlayerCombatStats getPlayerStats(String playerName) {
        // Simplified implementation - would need to track player-specific data
        return new PlayerCombatStats(playerName, hitsLanded, totalDamageDealt, accuracy);
    }

    /**
     * Inner class for player-specific combat statistics.
     */
    public static class PlayerCombatStats {
        private final String playerName;
        private final int hitsLanded;
        private final double damageDealt;
        private final double accuracy;

        public PlayerCombatStats(String playerName, int hitsLanded, double damageDealt, double accuracy) {
            this.playerName = playerName;
            this.hitsLanded = hitsLanded;
            this.damageDealt = damageDealt;
            this.accuracy = accuracy;
        }

        public String getPlayerName() { return playerName; }
        public int getHitsLanded() { return hitsLanded; }
        public double getDamageDealt() { return damageDealt; }
        public double getAccuracy() { return accuracy; }
    }
}