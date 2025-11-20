package com.muzlik.pvpcombat.data;

import org.bukkit.entity.Player;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tracks interference incidents for a combat session.
 */
public class InterferenceData {
    private final AtomicInteger interferenceCount;
    private volatile long lastInterferenceTime;

    public InterferenceData() {
        this.interferenceCount = new AtomicInteger(0);
        this.lastInterferenceTime = 0;
    }

    /**
     * Records an interference incident.
     */
    public void recordInterference(Player interferer, Player target, Player opponent) {
        interferenceCount.incrementAndGet();
        lastInterferenceTime = System.currentTimeMillis();
    }

    /**
     * Gets the total number of interference incidents.
     */
    public int getInterferenceCount() {
        return interferenceCount.get();
    }

    /**
     * Gets the timestamp of the last interference.
     */
    public long getLastInterferenceTime() {
        return lastInterferenceTime;
    }

    /**
     * Checks if interference occurred within the given time window (in milliseconds).
     */
    public boolean hasRecentInterference(long timeWindowMs) {
        return (System.currentTimeMillis() - lastInterferenceTime) <= timeWindowMs;
    }

    /**
     * Resets the interference data.
     */
    public void reset() {
        interferenceCount.set(0);
        lastInterferenceTime = 0;
    }

    /**
     * Gets a copy of this data for thread-safe reading.
     */
    public InterferenceData getSnapshot() {
        InterferenceData snapshot = new InterferenceData();
        snapshot.interferenceCount.set(this.interferenceCount.get());
        snapshot.lastInterferenceTime = this.lastInterferenceTime;
        return snapshot;
    }
}