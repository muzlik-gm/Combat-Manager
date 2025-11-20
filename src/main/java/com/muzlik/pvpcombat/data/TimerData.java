package com.muzlik.pvpcombat.data;

import java.util.UUID;

/**
 * Data class for managing countdown timers with performance tracking.
 * Handles combat timer logic including pause/resume, progress tracking, and expiration detection.
 *
 * @author PvPCombat Plugin Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class TimerData {
    private final UUID sessionId;
    private final long startTime;
    private final int initialDurationSeconds;
    private int remainingSeconds;
    private long lastUpdateTime;
    private boolean paused;

    /**
     * Constructs a new TimerData instance for managing a countdown timer.
     *
     * @param sessionId The unique identifier for the combat session this timer belongs to
     * @param durationSeconds The initial duration of the timer in seconds (must be positive)
     * @throws IllegalArgumentException if sessionId is null or durationSeconds is negative
     */
    public TimerData(UUID sessionId, int durationSeconds) {
        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID cannot be null");
        }
        if (durationSeconds < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
        this.sessionId = sessionId;
        this.startTime = System.currentTimeMillis();
        this.initialDurationSeconds = durationSeconds;
        this.remainingSeconds = durationSeconds;
        this.lastUpdateTime = startTime;
        this.paused = false;
    }

    /**
     * Gets the session ID associated with this timer.
     *
     * @return The unique session identifier
     */
    public UUID getSessionId() { return sessionId; }

    /**
     * Gets the timestamp when this timer was created.
     *
     * @return Start time in milliseconds since epoch
     */
    public long getStartTime() { return startTime; }

    /**
     * Gets the initial duration of the timer.
     *
     * @return Initial duration in seconds
     */
    public int getInitialDurationSeconds() { return initialDurationSeconds; }

    /**
     * Gets the remaining time on the timer.
     *
     * @return Remaining time in seconds
     */
    public int getRemainingSeconds() { return remainingSeconds; }

    /**
     * Sets the remaining time on the timer and updates the last update timestamp.
     *
     * @param remainingSeconds The new remaining time in seconds (must be non-negative)
     * @throws IllegalArgumentException if remainingSeconds is negative
     */
    public void setRemainingSeconds(int remainingSeconds) {
        if (remainingSeconds < 0) {
            throw new IllegalArgumentException("Remaining seconds cannot be negative");
        }
        this.remainingSeconds = remainingSeconds;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * Gets the timestamp of the last timer update.
     *
     * @return Last update time in milliseconds since epoch
     */
    public long getLastUpdateTime() { return lastUpdateTime; }

    /**
     * Checks if the timer is currently paused.
     *
     * @return true if paused, false if running
     */
    public boolean isPaused() { return paused; }

    /**
     * Sets the paused state of the timer.
     *
     * @param paused true to pause the timer, false to resume
     */
    public void setPaused(boolean paused) { this.paused = paused; }

    /**
     * Resets the timer to its initial duration and unpauses it.
     * This method sets the remaining time back to the original duration.
     */
    public void reset() {
        this.remainingSeconds = initialDurationSeconds;
        this.lastUpdateTime = System.currentTimeMillis();
        this.paused = false;
    }

    /**
     * Updates the remaining time based on elapsed time since the last update.
     * This method should be called periodically to decrement the timer.
     *
     * @return true if the timer has expired (remaining seconds <= 0), false otherwise
     */
    public boolean updateElapsedTime() {
        if (paused) return false;

        long currentTime = System.currentTimeMillis();
        long elapsedMs = currentTime - lastUpdateTime;
        int elapsedSeconds = (int) (elapsedMs / 1000);

        if (elapsedSeconds > 0) {
            remainingSeconds = Math.max(0, remainingSeconds - elapsedSeconds);
            lastUpdateTime = currentTime;
        }

        return remainingSeconds <= 0;
    }

    /**
     * Gets the progress of the timer as a normalized value.
     *
     * @return A double between 0.0 (expired) and 1.0 (full time remaining)
     */
    public double getProgress() {
        return (double) remainingSeconds / initialDurationSeconds;
    }

    /**
     * Checks if the timer has expired (no time remaining).
     *
     * @return true if the timer has expired, false otherwise
     */
    public boolean isExpired() {
        return remainingSeconds <= 0;
    }
}