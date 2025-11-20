package com.muzlik.pvpcombat.data;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe circular buffer for efficient storage of combat replay events.
 * Maintains events within a configurable time window.
 */
public class TimelineBuffer {

    private final ReplayEvent[] buffer;
    private final int capacity;
    private final long maxAgeSeconds;
    private int head = 0;
    private int size = 0;
    private long totalEvents = 0;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Object cleanupLock = new Object();

    public TimelineBuffer(int capacity, long maxAgeSeconds) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        if (maxAgeSeconds <= 0) {
            throw new IllegalArgumentException("Max age must be positive");
        }

        this.capacity = capacity;
        this.maxAgeSeconds = maxAgeSeconds;
        this.buffer = new ReplayEvent[capacity];
    }

    /**
     * Adds an event to the buffer, performing automatic cleanup of old events.
     */
    public void addEvent(ReplayEvent event) {
        lock.writeLock().lock();
        try {
            // Add event at head position
            buffer[head] = event;
            head = (head + 1) % capacity;

            if (size < capacity) {
                size++;
            }

            totalEvents++;

            // Periodic cleanup (every 100 events or when buffer is full)
            if (totalEvents % 100 == 0 || size == capacity) {
                cleanupOldEvents();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Gets all events within the specified time window.
     */
    public ReplayEvent[] getEventsInWindow(LocalDateTime fromTime) {
        lock.readLock().lock();
        try {
            if (size == 0) {
                return new ReplayEvent[0];
            }

            // Count valid events
            int validCount = 0;
            for (int i = 0; i < size; i++) {
                int index = (head - 1 - i + capacity) % capacity;
                ReplayEvent event = buffer[index];
                if (event != null && event.getTimestamp().isAfter(fromTime)) {
                    validCount++;
                } else {
                    break; // Events are in chronological order, can stop early
                }
            }

            // Extract valid events
            ReplayEvent[] result = new ReplayEvent[validCount];
            for (int i = 0; i < validCount; i++) {
                int index = (head - 1 - i + capacity) % capacity;
                result[i] = buffer[index];
            }

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Gets the most recent events up to the specified limit.
     */
    public ReplayEvent[] getRecentEvents(int limit) {
        lock.readLock().lock();
        try {
            int actualLimit = Math.min(limit, size);
            ReplayEvent[] result = new ReplayEvent[actualLimit];

            for (int i = 0; i < actualLimit; i++) {
                int index = (head - 1 - i + capacity) % capacity;
                result[i] = buffer[index];
            }

            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Removes events older than the configured max age.
     */
    private void cleanupOldEvents() {
        synchronized (cleanupLock) {
            lock.writeLock().lock();
            try {
                LocalDateTime cutoff = LocalDateTime.now().minusSeconds(maxAgeSeconds);
                int removed = 0;

                // Start from the oldest event
                for (int i = 0; i < size; i++) {
                    int index = (head - size + i + capacity) % capacity;
                    ReplayEvent event = buffer[index];

                    if (event != null && event.getTimestamp().isBefore(cutoff)) {
                        buffer[index] = null;
                        removed++;
                    } else {
                        break; // Events are chronological, no need to check further
                    }
                }

                size -= removed;

                // Adjust head if we removed from the beginning
                if (removed > 0 && size > 0) {
                    head = (head - removed + capacity) % capacity;
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    /**
     * Clears all events from the buffer.
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            for (int i = 0; i < capacity; i++) {
                buffer[i] = null;
            }
            head = 0;
            size = 0;
            totalEvents = 0;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Gets current buffer statistics.
     */
    public BufferStats getStats() {
        lock.readLock().lock();
        try {
            return new BufferStats(size, capacity, totalEvents, getMemoryUsage());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Calculates approximate memory usage in bytes.
     */
    private long getMemoryUsage() {
        long usage = 0;
        for (int i = 0; i < capacity; i++) {
            if (buffer[i] != null) {
                usage += buffer[i].getMemoryFootprint();
            }
        }
        return usage + (capacity * 4L); // array overhead
    }

    /**
     * Statistics class for buffer monitoring.
     */
    public static class BufferStats {
        public final int currentSize;
        public final int capacity;
        public final long totalEvents;
        public final long memoryUsageBytes;

        public BufferStats(int currentSize, int capacity, long totalEvents, long memoryUsageBytes) {
            this.currentSize = currentSize;
            this.capacity = capacity;
            this.totalEvents = totalEvents;
            this.memoryUsageBytes = memoryUsageBytes;
        }

        @Override
        public String toString() {
            return String.format("Buffer: %d/%d events, %d total, ~%.1f KB",
                currentSize, capacity, totalEvents, memoryUsageBytes / 1024.0);
        }
    }
}