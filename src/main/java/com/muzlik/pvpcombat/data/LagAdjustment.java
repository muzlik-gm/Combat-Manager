package com.muzlik.pvpcombat.data;

import java.util.UUID;

/**
 * Data class for managing timer adjustments during lag periods.
 * Tracks how timers should be modified based on performance metrics.
 */
public class LagAdjustment {

    private final UUID sessionId;
    private final long adjustmentStartTime;
    private volatile int extraSecondsGranted;
    private volatile double adjustmentMultiplier;
    private volatile boolean active;
    private volatile long lastAdjustmentTime;

    // Tracking for cleanup
    private volatile long totalAdjustedSeconds;
    private volatile int adjustmentCount;

    public LagAdjustment(UUID sessionId) {
        this.sessionId = sessionId;
        this.adjustmentStartTime = System.currentTimeMillis();
        this.extraSecondsGranted = 0;
        this.adjustmentMultiplier = 1.0;
        this.active = false;
        this.lastAdjustmentTime = 0;
        this.totalAdjustedSeconds = 0;
        this.adjustmentCount = 0;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public long getAdjustmentStartTime() {
        return adjustmentStartTime;
    }

    public int getExtraSecondsGranted() {
        return extraSecondsGranted;
    }

    public void addExtraSeconds(int seconds) {
        this.extraSecondsGranted += seconds;
        this.totalAdjustedSeconds += seconds;
        this.adjustmentCount++;
        this.lastAdjustmentTime = System.currentTimeMillis();
    }

    public double getAdjustmentMultiplier() {
        return adjustmentMultiplier;
    }

    public void setAdjustmentMultiplier(double multiplier) {
        this.adjustmentMultiplier = multiplier;
        this.lastAdjustmentTime = System.currentTimeMillis();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            this.lastAdjustmentTime = System.currentTimeMillis();
        }
    }

    public long getLastAdjustmentTime() {
        return lastAdjustmentTime;
    }

    public long getTotalAdjustedSeconds() {
        return totalAdjustedSeconds;
    }

    public int getAdjustmentCount() {
        return adjustmentCount;
    }

    /**
     * Calculates the effective timer extension based on lag severity.
     */
    public int calculateExtension(double lagSeverity, int baseExtension, double multiplier) {
        if (!active || lagSeverity <= 0) {
            return 0;
        }

        // Calculate extension based on severity and configured multiplier
        int calculatedExtension = (int) Math.ceil(baseExtension * lagSeverity * multiplier);

        // Add to granted seconds and return the new amount
        addExtraSeconds(calculatedExtension);
        return calculatedExtension;
    }

    /**
     * Resets the adjustment data (typically when combat ends).
     */
    public void reset() {
        this.extraSecondsGranted = 0;
        this.adjustmentMultiplier = 1.0;
        this.active = false;
        this.lastAdjustmentTime = 0;
        // Keep total stats for logging/analytics
    }

    /**
     * Checks if this adjustment should be cleaned up (no recent activity).
     */
    public boolean shouldCleanup(long maxInactiveTimeMs) {
        if (active) return false;
        return System.currentTimeMillis() - lastAdjustmentTime > maxInactiveTimeMs;
    }

    /**
     * Gets a summary of the adjustment for logging purposes.
     */
    public String getAdjustmentSummary() {
        return String.format("LagAdjustment{session=%s, active=%s, extraSeconds=%d, multiplier=%.2f, totalAdjusted=%d, adjustments=%d}",
                sessionId, active, extraSecondsGranted, adjustmentMultiplier, totalAdjustedSeconds, adjustmentCount);
    }

    @Override
    public String toString() {
        return String.format("LagAdjustment{session=%s, extraSeconds=%d, active=%s}",
                sessionId, extraSecondsGranted, active);
    }
}