package com.muzlik.pvpcombat.data;

import java.util.UUID;

/**
 * Data class for tracking server performance metrics and player ping levels.
 * Thread-safe for concurrent access during combat operations.
 */
public class PerformanceData {

    // TPS tracking
    private volatile double currentTps;
    private volatile double averageTps;

    // Ping tracking per player
    private final UUID playerId;
    private volatile int currentPing;
    private volatile int averagePing;

    // Lag detection thresholds
    private volatile long lastLagSpikeTime;
    private volatile boolean experiencingLag;

    public PerformanceData(UUID playerId) {
        this.playerId = playerId;
        this.currentTps = 20.0; // Assume full TPS initially
        this.averageTps = 20.0;
        this.currentPing = 0;
        this.averagePing = 0;
        this.lastLagSpikeTime = 0;
        this.experiencingLag = false;
    }

    // TPS methods
    public double getCurrentTps() {
        return currentTps;
    }

    public void setCurrentTps(double tps) {
        this.currentTps = tps;
        updateTpsAverage(tps);
    }

    public double getAverageTps() {
        return averageTps;
    }

    private void updateTpsAverage(double newTps) {
        // Simple moving average calculation
        this.averageTps = (averageTps * 0.9) + (newTps * 0.1);
    }

    // Ping methods
    public int getCurrentPing() {
        return currentPing;
    }

    public void setCurrentPing(int ping) {
        this.currentPing = ping;
        updatePingAverage(ping);
    }

    public int getAveragePing() {
        return averagePing;
    }

    private void updatePingAverage(int newPing) {
        // Simple moving average calculation
        this.averagePing = (int) ((averagePing * 0.8) + (newPing * 0.2));
    }

    // Lag detection methods
    public boolean isExperiencingLag() {
        return experiencingLag;
    }

    public void setExperiencingLag(boolean experiencingLag) {
        this.experiencingLag = experiencingLag;
        if (experiencingLag) {
            this.lastLagSpikeTime = System.currentTimeMillis();
        }
    }

    public long getLastLagSpikeTime() {
        return lastLagSpikeTime;
    }

    public long getTimeSinceLastLagSpike() {
        return System.currentTimeMillis() - lastLagSpikeTime;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    /**
     * Checks if the current performance indicates lag based on configured thresholds.
     */
    public boolean isLagging(double tpsThreshold, int pingThreshold) {
        return currentTps < tpsThreshold || currentPing > pingThreshold;
    }

    /**
     * Gets a severity score for current lag (0.0 = no lag, 1.0 = severe lag).
     */
    public double getLagSeverity(double minTps, int maxPing) {
        double tpsSeverity = Math.max(0, (minTps - currentTps) / minTps);
        double pingSeverity = Math.max(0, (currentPing - maxPing) / (double) maxPing);
        return Math.min(1.0, (tpsSeverity + pingSeverity) / 2.0);
    }

    @Override
    public String toString() {
        return String.format("PerformanceData{player=%s, tps=%.2f/%.2f, ping=%d/%d, lagging=%s}",
                playerId, currentTps, averageTps, currentPing, averagePing, experiencingLag);
    }
}