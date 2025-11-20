package com.muzlik.pvpcombat.data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks detailed combat statistics with thread-safe operations.
 */
public class CombatStatistics {

    private final UUID sessionId;

    // Core combat stats
    private final AtomicInteger totalAttacks;
    private final AtomicInteger hitsLanded;
    private final AtomicLong totalDamageDealt;
    private final AtomicLong totalDamageReceived;

    // Accuracy and precision
    private final AtomicInteger criticalHits;
    private final AtomicInteger totalHits;
    private final AtomicLong totalDistance;

    // Movement and positioning
    private final AtomicInteger knockbackGiven;
    private final AtomicInteger knockbackReceived;
    private final AtomicLong totalMovementDistance;

    // Timing statistics
    private final AtomicLong totalCombatTime;
    private final AtomicInteger timerResets;
    private LocalDateTime firstHit;
    private LocalDateTime lastHit;

    // Weapon usage tracking
    private final Map<String, AtomicInteger> weaponUsage;
    private final Map<String, AtomicLong> damageByWeapon;

    // Advanced metrics
    private final AtomicInteger interferenceAttempts;
    private final AtomicInteger successfulInterferences;
    private final AtomicInteger restrictionViolations;

    public CombatStatistics(UUID sessionId) {
        this.sessionId = sessionId;

        this.totalAttacks = new AtomicInteger(0);
        this.hitsLanded = new AtomicInteger(0);
        this.totalDamageDealt = new AtomicLong(0);
        this.totalDamageReceived = new AtomicLong(0);

        this.criticalHits = new AtomicInteger(0);
        this.totalHits = new AtomicInteger(0);
        this.totalDistance = new AtomicLong(0);

        this.knockbackGiven = new AtomicInteger(0);
        this.knockbackReceived = new AtomicInteger(0);
        this.totalMovementDistance = new AtomicLong(0);

        this.totalCombatTime = new AtomicLong(0);
        this.timerResets = new AtomicInteger(0);

        this.weaponUsage = new ConcurrentHashMap<>();
        this.damageByWeapon = new ConcurrentHashMap<>();

        this.interferenceAttempts = new AtomicInteger(0);
        this.successfulInterferences = new AtomicInteger(0);
        this.restrictionViolations = new AtomicInteger(0);
    }

    // Attack and damage tracking
    public void recordAttack(boolean hitLanded, double damage, double distance, String weaponType, boolean critical) {
        totalAttacks.incrementAndGet();
        if (hitLanded) {
            hitsLanded.incrementAndGet();
            totalDamageDealt.addAndGet((long) damage);
            totalHits.incrementAndGet();
            totalDistance.addAndGet((long) distance);

            if (critical) {
                criticalHits.incrementAndGet();
            }

            // Update weapon stats
            weaponUsage.computeIfAbsent(weaponType, k -> new AtomicInteger(0)).incrementAndGet();
            damageByWeapon.computeIfAbsent(weaponType, k -> new AtomicLong(0)).addAndGet((long) damage);
        }

        updateTimestamps();
    }

    public void recordDamageReceived(double damage) {
        totalDamageReceived.addAndGet((long) damage);
    }

    public void increment(String type) {
        totalAttacks.incrementAndGet();
        weaponUsage.computeIfAbsent(type, k -> new AtomicInteger(0)).incrementAndGet();
    }

    // Knockback tracking
    public void recordKnockbackGiven() {
        knockbackGiven.incrementAndGet();
    }

    public void recordKnockbackReceived() {
        knockbackReceived.incrementAndGet();
    }

    // Movement tracking
    public void recordMovement(double distance) {
        totalMovementDistance.addAndGet((long) distance);
    }

    // Timer and timing
    public void recordTimerReset() {
        timerResets.incrementAndGet();
    }

    public void addCombatTime(long seconds) {
        totalCombatTime.addAndGet(seconds);
    }

    private void updateTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        if (firstHit == null) {
            firstHit = now;
        }
        lastHit = now;
    }

    // Interference and restrictions
    public void recordInterferenceAttempt(boolean successful) {
        interferenceAttempts.incrementAndGet();
        if (successful) {
            successfulInterferences.incrementAndGet();
        }
    }

    public void recordRestrictionViolation() {
        restrictionViolations.incrementAndGet();
    }

    // Calculated statistics
    public double getAccuracy() {
        int attacks = totalAttacks.get();
        return attacks > 0 ? (double) hitsLanded.get() / attacks * 100.0 : 0.0;
    }

    public double getAverageDamagePerHit() {
        int hits = totalHits.get();
        return hits > 0 ? (double) totalDamageDealt.get() / hits : 0.0;
    }

    public double getAverageDistance() {
        int hits = totalHits.get();
        return hits > 0 ? (double) totalDistance.get() / hits : 0.0;
    }

    public double getCriticalHitRate() {
        int hits = totalHits.get();
        return hits > 0 ? (double) criticalHits.get() / hits * 100.0 : 0.0;
    }

    public long getCombatDurationSeconds() {
        if (firstHit != null && lastHit != null) {
            return java.time.Duration.between(firstHit, lastHit).getSeconds();
        }
        return 0;
    }

    // Getters
    public UUID getSessionId() { return sessionId; }
    public int getTotalAttacks() { return totalAttacks.get(); }
    public int getHitsLanded() { return hitsLanded.get(); }
    public long getTotalDamageDealt() { return totalDamageDealt.get(); }
    public long getTotalDamageReceived() { return totalDamageReceived.get(); }
    public int getCriticalHits() { return criticalHits.get(); }
    public long getTotalDistance() { return totalDistance.get(); }
    public int getKnockbackGiven() { return knockbackGiven.get(); }
    public int getKnockbackReceived() { return knockbackReceived.get(); }
    public long getTotalMovementDistance() { return totalMovementDistance.get(); }
    public long getTotalCombatTime() { return totalCombatTime.get(); }
    public int getTimerResets() { return timerResets.get(); }
    public Map<String, AtomicInteger> getWeaponUsage() { return new ConcurrentHashMap<>(weaponUsage); }
    public Map<String, AtomicLong> getDamageByWeapon() { return new ConcurrentHashMap<>(damageByWeapon); }
    public int getInterferenceAttempts() { return interferenceAttempts.get(); }
    public int getSuccessfulInterferences() { return successfulInterferences.get(); }
    public int getRestrictionViolations() { return restrictionViolations.get(); }
    public LocalDateTime getFirstHit() { return firstHit; }
    public LocalDateTime getLastHit() { return lastHit; }

    /**
     * Gets a summary of all statistics.
     */
    public String getStatisticsSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Combat Statistics for Session ").append(sessionId).append(":\n");
        sb.append("Attacks: ").append(getTotalAttacks()).append(" total, ")
          .append(getHitsLanded()).append(" landed (").append(String.format("%.1f", getAccuracy())).append("%)\n");
        sb.append("Damage: ").append(getTotalDamageDealt()).append(" dealt, ")
          .append(getTotalDamageReceived()).append(" received\n");
        sb.append("Critical Hits: ").append(getCriticalHits()).append(" (")
          .append(String.format("%.1f", getCriticalHitRate())).append("%)\n");
        sb.append("Knockback: ").append(getKnockbackGiven()).append(" given, ")
          .append(getKnockbackReceived()).append(" received\n");
        sb.append("Movement: ").append(getTotalMovementDistance()).append(" blocks\n");
        sb.append("Timer Resets: ").append(getTimerResets()).append("\n");
        sb.append("Duration: ").append(getCombatDurationSeconds()).append(" seconds\n");
        return sb.toString();
    }
}