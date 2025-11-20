package com.muzlik.pvpcombat.data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Manages timeline storage for multiple combat sessions.
 * Each session has its own TimelineBuffer for efficient replay data management.
 */
public class EventTimeline {

    private final Map<UUID, TimelineBuffer> sessionBuffers;
    private final int defaultBufferCapacity;
    private final long defaultMaxAgeSeconds;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public EventTimeline(int defaultBufferCapacity, long defaultMaxAgeSeconds) {
        this.sessionBuffers = new ConcurrentHashMap<>();
        this.defaultBufferCapacity = defaultBufferCapacity;
        this.defaultMaxAgeSeconds = defaultMaxAgeSeconds;
    }

    /**
     * Adds an event to the timeline for the specified session.
     */
    public void addEvent(UUID sessionId, ReplayEvent event) {
        TimelineBuffer buffer = sessionBuffers.computeIfAbsent(sessionId, k ->
            new TimelineBuffer(defaultBufferCapacity, defaultMaxAgeSeconds));
        buffer.addEvent(event);
    }

    /**
     * Gets replay events for a session within the specified time window.
     */
    public ReplayEvent[] getEventsInWindow(UUID sessionId, LocalDateTime fromTime) {
        TimelineBuffer buffer = sessionBuffers.get(sessionId);
        if (buffer == null) {
            return new ReplayEvent[0];
        }
        return buffer.getEventsInWindow(fromTime);
    }

    /**
     * Gets recent events for a session up to the specified limit.
     */
    public ReplayEvent[] getRecentEvents(UUID sessionId, int limit) {
        TimelineBuffer buffer = sessionBuffers.get(sessionId);
        if (buffer == null) {
            return new ReplayEvent[0];
        }
        return buffer.getRecentEvents(limit);
    }

    /**
     * Gets the full replay timeline for a session within the default time window.
     */
    public ReplayEvent[] getFullReplay(UUID sessionId) {
        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(defaultMaxAgeSeconds);
        return getEventsInWindow(sessionId, cutoff);
    }

    /**
     * Gets replay data for a session formatted for storage/compression.
     */
    public ReplayData getReplayData(UUID sessionId) {
        ReplayEvent[] events = getFullReplay(sessionId);
        if (events.length == 0) {
            return null;
        }

        LocalDateTime startTime = events[0].getTimestamp();
        LocalDateTime endTime = events[events.length - 1].getTimestamp();
        long durationSeconds = ChronoUnit.SECONDS.between(startTime, endTime);

        return new ReplayData(sessionId, events, durationSeconds, LocalDateTime.now());
    }

    /**
     * Removes a session's timeline data.
     */
    public void removeSession(UUID sessionId) {
        TimelineBuffer buffer = sessionBuffers.remove(sessionId);
        if (buffer != null) {
            buffer.clear();
        }
    }

    /**
     * Cleans up old sessions based on last activity.
     */
    public void cleanupOldSessions(long maxInactiveSeconds) {
        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(maxInactiveSeconds);

        sessionBuffers.entrySet().removeIf(entry -> {
            ReplayEvent[] recent = entry.getValue().getRecentEvents(1);
            if (recent.length == 0) {
                return true; // Empty buffer
            }

            // Remove if last event is older than cutoff
            return recent[0].getTimestamp().isBefore(cutoff);
        });
    }

    /**
     * Gets statistics for all active sessions.
     */
    public Map<UUID, TimelineBuffer.BufferStats> getSessionStats() {
        Map<UUID, TimelineBuffer.BufferStats> stats = new HashMap<>();
        for (Map.Entry<UUID, TimelineBuffer> entry : sessionBuffers.entrySet()) {
            stats.put(entry.getKey(), entry.getValue().getStats());
        }
        return stats;
    }

    /**
     * Gets overall timeline statistics.
     */
    public TimelineStats getOverallStats() {
        lock.readLock().lock();
        try {
            int totalSessions = sessionBuffers.size();
            long totalEvents = 0;
            long totalMemory = 0;

            for (TimelineBuffer buffer : sessionBuffers.values()) {
                TimelineBuffer.BufferStats stats = buffer.getStats();
                totalEvents += stats.totalEvents;
                totalMemory += stats.memoryUsageBytes;
            }

            return new TimelineStats(totalSessions, totalEvents, totalMemory);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Clears all timeline data.
     */
    public void clearAll() {
        for (TimelineBuffer buffer : sessionBuffers.values()) {
            buffer.clear();
        }
        sessionBuffers.clear();
    }

    /**
     * Checks if a session has replay data available.
     */
    public boolean hasReplayData(UUID sessionId) {
        TimelineBuffer buffer = sessionBuffers.get(sessionId);
        return buffer != null && buffer.getStats().currentSize > 0;
    }

    /**
     * Statistics class for timeline monitoring.
     */
    public static class TimelineStats {
        public final int activeSessions;
        public final long totalEvents;
        public final long totalMemoryBytes;

        public TimelineStats(int activeSessions, long totalEvents, long totalMemoryBytes) {
            this.activeSessions = activeSessions;
            this.totalEvents = totalEvents;
            this.totalMemoryBytes = totalMemoryBytes;
        }

        @Override
        public String toString() {
            return String.format("Timeline: %d sessions, %d events, ~%.1f MB",
                activeSessions, totalEvents, totalMemoryBytes / (1024.0 * 1024.0));
        }
    }
}