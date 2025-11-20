package com.muzlik.pvpcombat.data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Long-term player statistics tracking across multiple combat sessions.
 */
public class PlayerStatistics {

    private final UUID playerId;

    // Overall combat record
    private final AtomicInteger totalCombats;
    private final AtomicInteger wins;
    private final AtomicInteger losses;
    private final AtomicInteger draws;

    // Damage statistics
    private final AtomicLong totalDamageDealt;
    private final AtomicLong totalDamageReceived;
    private final AtomicLong totalHealingReceived;

    // Accuracy and performance
    private final AtomicInteger totalAttacks;
    private final AtomicInteger hitsLanded;
    private final AtomicInteger criticalHits;

    // Special actions
    private final AtomicInteger knockbacksGiven;
    private final AtomicInteger knockbacksReceived;
    private final AtomicInteger timerResetsUsed;

    // Restrictions and interference
    private final AtomicInteger interferenceAttempts;
    private final AtomicInteger successfulInterferences;
    private final AtomicInteger restrictionViolations;

    // Weapon preferences
    private final Map<String, AtomicInteger> weaponUsage;
    private final Map<String, AtomicLong> damageByWeapon;

    // Time statistics
    private final AtomicLong totalCombatTime;
    private LocalDateTime firstCombat;
    private LocalDateTime lastCombat;

    // Streaks and records
    private int currentWinStreak;
    private int longestWinStreak;
    private int currentLossStreak;
    private int longestLossStreak;
    private double highestAccuracy;
    private long fastestWinTime; // in seconds

    public PlayerStatistics(UUID playerId) {
        this.playerId = playerId;

        this.totalCombats = new AtomicInteger(0);
        this.wins = new AtomicInteger(0);
        this.losses = new AtomicInteger(0);
        this.draws = new AtomicInteger(0);

        this.totalDamageDealt = new AtomicLong(0);
        this.totalDamageReceived = new AtomicLong(0);
        this.totalHealingReceived = new AtomicLong(0);

        this.totalAttacks = new AtomicInteger(0);
        this.hitsLanded = new AtomicInteger(0);
        this.criticalHits = new AtomicInteger(0);

        this.knockbacksGiven = new AtomicInteger(0);
        this.knockbacksReceived = new AtomicInteger(0);
        this.timerResetsUsed = new AtomicInteger(0);

        this.interferenceAttempts = new AtomicInteger(0);
        this.successfulInterferences = new AtomicInteger(0);
        this.restrictionViolations = new AtomicInteger(0);

        this.weaponUsage = new ConcurrentHashMap<>();
        this.damageByWeapon = new ConcurrentHashMap<>();

        this.totalCombatTime = new AtomicLong(0);
    }

    /**
     * Records the outcome of a combat session.
     */
    public void recordCombatOutcome(boolean won, boolean draw, long combatTimeSeconds) {
        totalCombats.incrementAndGet();
        totalCombatTime.addAndGet(combatTimeSeconds);

        if (draw) {
            draws.incrementAndGet();
            resetStreaks();
        } else if (won) {
            wins.incrementAndGet();
            currentWinStreak++;
            currentLossStreak = 0;
            longestWinStreak = Math.max(longestWinStreak, currentWinStreak);

            // Check for fastest win
            if (fastestWinTime == 0 || combatTimeSeconds < fastestWinTime) {
                fastestWinTime = combatTimeSeconds;
            }
        } else {
            losses.incrementAndGet();
            currentLossStreak++;
            currentWinStreak = 0;
            longestLossStreak = Math.max(longestLossStreak, currentLossStreak);
        }

        updateCombatTimestamps();
    }

    /**
     * Records damage statistics.
     */
    public void recordDamage(long damageDealt, long damageReceived, long healingReceived) {
        totalDamageDealt.addAndGet(damageDealt);
        totalDamageReceived.addAndGet(damageReceived);
        totalHealingReceived.addAndGet(healingReceived);
    }

    /**
     * Records attack statistics.
     */
    public void recordAttacks(int attacks, int hits, int criticals, double accuracy) {
        totalAttacks.addAndGet(attacks);
        hitsLanded.addAndGet(hits);
        criticalHits.addAndGet(criticals);
        highestAccuracy = Math.max(highestAccuracy, accuracy);
    }

    /**
     * Records knockback statistics.
     */
    public void recordKnockbacks(int given, int received) {
        knockbacksGiven.addAndGet(given);
        knockbacksReceived.addAndGet(received);
    }

    /**
     * Records timer reset usage.
     */
    public void recordTimerReset() {
        timerResetsUsed.incrementAndGet();
    }

    /**
     * Records interference actions.
     */
    public void recordInterference(boolean successful) {
        interferenceAttempts.incrementAndGet();
        if (successful) {
            successfulInterferences.incrementAndGet();
        }
    }

    /**
     * Records restriction violations.
     */
    public void recordRestrictionViolation() {
        restrictionViolations.incrementAndGet();
    }

    /**
     * Records weapon usage.
     */
    public void recordWeaponUsage(String weaponType, long damage) {
        weaponUsage.computeIfAbsent(weaponType, k -> new AtomicInteger(0)).incrementAndGet();
        if (damage > 0) {
            damageByWeapon.computeIfAbsent(weaponType, k -> new AtomicLong(0)).addAndGet(damage);
        }
    }

    private void resetStreaks() {
        currentWinStreak = 0;
        currentLossStreak = 0;
    }

    private void updateCombatTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        if (firstCombat == null) {
            firstCombat = now;
        }
        lastCombat = now;
    }

    // Calculated statistics
    public double getWinRate() {
        int total = totalCombats.get();
        return total > 0 ? (double) wins.get() / total * 100.0 : 0.0;
    }

    public double getOverallAccuracy() {
        int attacks = totalAttacks.get();
        return attacks > 0 ? (double) hitsLanded.get() / attacks * 100.0 : 0.0;
    }

    public double getKDRatio() {
        int deaths = losses.get();
        return deaths > 0 ? (double) wins.get() / deaths : wins.get();
    }

    public double getAverageDamagePerCombat() {
        int combats = totalCombats.get();
        return combats > 0 ? (double) totalDamageDealt.get() / combats : 0.0;
    }

    public double getAverageCombatTime() {
        int combats = totalCombats.get();
        return combats > 0 ? (double) totalCombatTime.get() / combats : 0.0;
    }

    public String getFavoriteWeapon() {
        return weaponUsage.entrySet().stream()
            .max(Map.Entry.comparingByValue((a, b) -> Integer.compare(a.get(), b.get())))
            .map(Map.Entry::getKey)
            .orElse("None");
    }

    // Getters
    public UUID getPlayerId() { return playerId; }
    public int getTotalCombats() { return totalCombats.get(); }
    public int getWins() { return wins.get(); }
    public int getLosses() { return losses.get(); }
    public int getDraws() { return draws.get(); }
    public long getTotalDamageDealt() { return totalDamageDealt.get(); }
    public long getTotalDamageReceived() { return totalDamageReceived.get(); }
    public long getTotalHealingReceived() { return totalHealingReceived.get(); }
    public int getTotalAttacks() { return totalAttacks.get(); }
    public int getHitsLanded() { return hitsLanded.get(); }
    public int getCriticalHits() { return criticalHits.get(); }
    public int getKnockbacksGiven() { return knockbacksGiven.get(); }
    public int getKnockbacksReceived() { return knockbacksReceived.get(); }
    public int getTimerResetsUsed() { return timerResetsUsed.get(); }
    public int getInterferenceAttempts() { return interferenceAttempts.get(); }
    public int getSuccessfulInterferences() { return successfulInterferences.get(); }
    public int getRestrictionViolations() { return restrictionViolations.get(); }
    public Map<String, AtomicInteger> getWeaponUsage() { return new ConcurrentHashMap<>(weaponUsage); }
    public Map<String, AtomicLong> getDamageByWeapon() { return new ConcurrentHashMap<>(damageByWeapon); }
    public long getTotalCombatTime() { return totalCombatTime.get(); }
    public LocalDateTime getFirstCombat() { return firstCombat; }
    public LocalDateTime getLastCombat() { return lastCombat; }
    public int getCurrentWinStreak() { return currentWinStreak; }
    public int getLongestWinStreak() { return longestWinStreak; }
    public int getCurrentLossStreak() { return currentLossStreak; }
    public int getLongestLossStreak() { return longestLossStreak; }
    public double getHighestAccuracy() { return highestAccuracy; }
    public long getFastestWinTime() { return fastestWinTime; }

    /**
     * Gets a comprehensive statistics summary.
     */
    public String getStatisticsSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Player Statistics:\n");
        sb.append("Combat Record: ").append(getWins()).append("W - ").append(getLosses()).append("L - ")
          .append(getDraws()).append("D (").append(String.format("%.1f", getWinRate())).append("% win rate)\n");
        sb.append("KD Ratio: ").append(String.format("%.2f", getKDRatio())).append("\n");
        sb.append("Accuracy: ").append(String.format("%.1f", getOverallAccuracy())).append("% (Best: ")
          .append(String.format("%.1f", getHighestAccuracy())).append("%)\n");
        sb.append("Total Damage: ").append(getTotalDamageDealt()).append(" dealt, ")
          .append(getTotalDamageReceived()).append(" received\n");
        sb.append("Critical Hits: ").append(getCriticalHits()).append("\n");
        sb.append("Knockbacks: ").append(getKnockbacksGiven()).append(" given, ")
          .append(getKnockbacksReceived()).append(" received\n");
        sb.append("Favorite Weapon: ").append(getFavoriteWeapon()).append("\n");
        sb.append("Streaks: ").append(getCurrentWinStreak()).append(" current wins, ")
          .append(getLongestWinStreak()).append(" longest\n");
        sb.append("Total Combat Time: ").append(getTotalCombatTime()).append(" seconds\n");
        return sb.toString();
    }
}