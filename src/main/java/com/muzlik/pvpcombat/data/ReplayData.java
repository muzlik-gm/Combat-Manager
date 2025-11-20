package com.muzlik.pvpcombat.data;

import java.io.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Compressed storage format for replay data.
 * Handles serialization and compression of ReplayEvent arrays.
 */
public class ReplayData {

    private final UUID sessionId;
    private final ReplayEvent[] events;
    private final long durationSeconds;
    private final LocalDateTime createdAt;
    private volatile byte[] compressedData;

    public ReplayData(UUID sessionId, ReplayEvent[] events, long durationSeconds, LocalDateTime createdAt) {
        this.sessionId = sessionId;
        this.events = events.clone(); // Defensive copy
        this.durationSeconds = durationSeconds;
        this.createdAt = createdAt;
    }

    /**
     * Gets the compressed data, compressing on first access.
     */
    public synchronized byte[] getCompressedData() throws IOException {
        if (compressedData == null) {
            compressedData = compressEvents();
        }
        return compressedData.clone();
    }

    /**
     * Compresses the events array using GZIP.
     */
    private byte[] compressEvents() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
             ObjectOutputStream oos = new ObjectOutputStream(gzipOut)) {

            oos.writeObject(events);
            oos.flush();
            gzipOut.finish();

            return baos.toByteArray();
        }
    }

    /**
     * Decompresses and recreates ReplayData from compressed bytes.
     */
    public static ReplayData fromCompressedData(UUID sessionId, byte[] compressedData,
                                              long durationSeconds, LocalDateTime createdAt) throws IOException, ClassNotFoundException {
        ReplayEvent[] events = decompressEvents(compressedData);
        return new ReplayData(sessionId, events, durationSeconds, createdAt);
    }

    /**
     * Decompresses events from GZIP compressed data.
     */
    private static ReplayEvent[] decompressEvents(byte[] compressedData) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzipIn = new GZIPInputStream(bais);
             ObjectInputStream ois = new ObjectInputStream(gzipIn)) {

            return (ReplayEvent[]) ois.readObject();
        }
    }

    /**
     * Gets the events array (decompressed if necessary).
     */
    public ReplayEvent[] getEvents() {
        return events.clone(); // Defensive copy
    }

    /**
     * Calculates compression ratio.
     */
    public double getCompressionRatio() {
        if (compressedData == null) {
            try {
                getCompressedData();
            } catch (IOException e) {
                return 1.0; // No compression
            }
        }

        long uncompressedSize = calculateUncompressedSize();
        return uncompressedSize > 0 ? (double) compressedData.length / uncompressedSize : 1.0;
    }

    /**
     * Calculates approximate uncompressed size.
     */
    private long calculateUncompressedSize() {
        long size = 0;
        for (ReplayEvent event : events) {
            size += event.getMemoryFootprint();
        }
        return size + 64; // Object overhead
    }

    /**
     * Gets replay statistics.
     */
    public ReplayStats getStats() {
        int totalEvents = events.length;
        int hits = 0, misses = 0, movements = 0, abilities = 0;
        double totalDamage = 0.0;

        for (ReplayEvent event : events) {
            switch (event.getEventType()) {
                case HIT_LANDED:
                    hits++;
                    totalDamage += event.getDamage();
                    break;
                case HIT_MISSED:
                    misses++;
                    break;
                case MOVEMENT:
                    movements++;
                    break;
                case ABILITY_USED:
                    abilities++;
                    break;
                default:
                    break;
            }
        }

        return new ReplayStats(totalEvents, hits, misses, movements, abilities, totalDamage);
    }

    // Getters
    public UUID getSessionId() { return sessionId; }
    public long getDurationSeconds() { return durationSeconds; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getEventCount() { return events.length; }

    /**
     * Statistics class for replay data.
     */
    public static class ReplayStats {
        public final int totalEvents;
        public final int hitsLanded;
        public final int hitsMissed;
        public final int movements;
        public final int abilitiesUsed;
        public final double totalDamage;

        public ReplayStats(int totalEvents, int hitsLanded, int hitsMissed,
                          int movements, int abilitiesUsed, double totalDamage) {
            this.totalEvents = totalEvents;
            this.hitsLanded = hitsLanded;
            this.hitsMissed = hitsMissed;
            this.movements = movements;
            this.abilitiesUsed = abilitiesUsed;
            this.totalDamage = totalDamage;
        }

        public double getHitAccuracy() {
            int totalHits = hitsLanded + hitsMissed;
            return totalHits > 0 ? (double) hitsLanded / totalHits : 0.0;
        }

        @Override
        public String toString() {
            return String.format("Replay: %d events, %d hits (%.1f%%), %.1f dmg, %d moves, %d abilities",
                totalEvents, hitsLanded, getHitAccuracy() * 100, totalDamage, movements, abilitiesUsed);
        }
    }
}