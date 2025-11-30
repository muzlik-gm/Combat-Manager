package com.muzlik.pvpcombat.logging;

import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import com.muzlik.pvpcombat.data.*;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Manages replay storage, retrieval, and compression for combat events.
 * Integrates with CombatLogger for event capture and provides admin access controls.
 */
public class CombatReplayManager {

    public enum StorageFormat {
        MEMORY,
        COMPRESSED_FILE,
        HYBRID
    }

    private final PvPCombatPlugin plugin;
    private final Logger logger;
    private final EventTimeline timeline;
    private final Map<UUID, ReplayData> replayCache;

    // Configuration
    private StorageFormat storageFormat;
    private int timelineCapacity;
    private long timelineMaxAgeSeconds;
    private long cacheMaxAgeMinutes;
    private Path replayDirectory;
    private boolean adminOnlyAccess;
    private Set<String> allowedAdmins;

    private final ScheduledExecutorService cleanupExecutor;

    public CombatReplayManager(PvPCombatPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.replayCache = new ConcurrentHashMap<>();

        // Initialize timeline with default values
        this.timelineCapacity = 1000;
        this.timelineMaxAgeSeconds = 600; // 10 minutes
        this.timeline = new EventTimeline(timelineCapacity, timelineMaxAgeSeconds);

        this.cleanupExecutor = Executors.newScheduledThreadPool(1);

        loadConfiguration();
        initializeStorage();

        startCleanupTasks();
        logger.info("CombatReplayManager initialized with format: " + storageFormat);
    }

    /**
     * Loads configuration from plugin config.
     */
    private void loadConfiguration() {
        this.storageFormat = StorageFormat.valueOf(
            plugin.getConfig().getString("replay.storage.format", "HYBRID").toUpperCase()
        );
        this.timelineCapacity = plugin.getConfig().getInt("replay.timeline.capacity", 1000);
        this.timelineMaxAgeSeconds = plugin.getConfig().getInt("replay.timeline.max_age_seconds", 600);
        this.cacheMaxAgeMinutes = plugin.getConfig().getInt("replay.cache.max_age_minutes", 30);
        this.adminOnlyAccess = plugin.getConfig().getBoolean("replay.access.admin_only", true);

        // Load admin list
        this.allowedAdmins = new HashSet<>(
            plugin.getConfig().getStringList("replay.access.allowed_admins")
        );
    }

    /**
     * Initializes storage directory.
     */
    private void initializeStorage() {
        this.replayDirectory = Paths.get(plugin.getDataFolder().getAbsolutePath(), "replays");
        try {
            Files.createDirectories(replayDirectory);
        } catch (IOException e) {
            logger.warning("Failed to create replay directory: " + e.getMessage());
        }
    }

    /**
     * Records a combat event for replay.
     */
    public void recordEvent(UUID sessionId, ReplayEvent event) {
        timeline.addEvent(sessionId, event);

        // Auto-save important events if using file storage
        if (storageFormat == StorageFormat.COMPRESSED_FILE || storageFormat == StorageFormat.HYBRID) {
            if (event.getEventType() == ReplayEvent.ReplayEventType.COMBAT_END ||
                event.getEventType() == ReplayEvent.ReplayEventType.DAMAGE_DEALT) {
                saveReplayAsync(sessionId);
            }
        }
    }

    /**
     * Gets replay data for a session.
     */
    public ReplayData getReplayData(UUID sessionId, Player requester) {
        if (!hasAccess(requester)) {
            return null;
        }

        // Try cache first
        ReplayData cached = replayCache.get(sessionId);
        if (cached != null) {
            return cached;
        }

        // Try loading from file
        if (storageFormat == StorageFormat.COMPRESSED_FILE || storageFormat == StorageFormat.HYBRID) {
            try {
                ReplayData loaded = loadReplayFromFile(sessionId);
                if (loaded != null) {
                    replayCache.put(sessionId, loaded);
                    return loaded;
                }
            } catch (Exception e) {
                logger.warning("Failed to load replay from file for session " + sessionId + ": " + e.getMessage());
            }
        }

        // Generate from timeline
        ReplayData generated = timeline.getReplayData(sessionId);
        if (generated != null && storageFormat != StorageFormat.MEMORY) {
            replayCache.put(sessionId, generated);
        }

        return generated;
    }

    /**
     * Gets formatted replay timeline as JSON for GUI display.
     */
    public String getReplayTimelineJson(UUID sessionId, Player requester) {
        ReplayData data = getReplayData(sessionId, requester);
        if (data == null) {
            return "{\"error\": \"No replay data available\"}";
        }

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"sessionId\": \"").append(sessionId).append("\",");
        json.append("\"duration\": ").append(data.getDurationSeconds()).append(",");
        json.append("\"eventCount\": ").append(data.getEventCount()).append(",");
        json.append("\"events\": [");

        boolean first = true;
        ReplayEvent[] events = data.getEvents();
        for (ReplayEvent event : events) {
            if (!first) json.append(",");
            json.append("{");
            json.append("\"timestamp\": \"").append(event.getTimestamp()).append("\",");
            json.append("\"type\": \"").append(event.getEventType()).append("\",");
            json.append("\"damage\": ").append(event.getDamage()).append(",");
            json.append("\"critical\": ").append(event.isCritical()).append(",");
            json.append("\"location\": \"").append(event.getLocation()).append("\",");
            json.append("\"weapon\": \"").append(event.getWeaponType()).append("\"");
            if (event.getTargetId() != null) {
                json.append(",\"targetId\": \"").append(event.getTargetId()).append("\"");
            }
            json.append("}");
            first = false;
        }

        json.append("]}");
        return json.toString();
    }

    /**
     * Saves replay data to compressed file asynchronously.
     */
    private void saveReplayAsync(UUID sessionId) {
        if (storageFormat == StorageFormat.MEMORY) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                saveReplayToFile(sessionId);
            } catch (Exception e) {
                logger.warning("Failed to save replay for session " + sessionId + ": " + e.getMessage());
            }
        });
    }

    /**
     * Saves replay data to compressed file with player names and timestamp.
     */
    private void saveReplayToFile(UUID sessionId) throws IOException {
        ReplayData data = timeline.getReplayData(sessionId);
        if (data == null) {
            plugin.getLogger().warning("No replay data found for session " + sessionId);
            return;
        }

        // Get player names from the session
        String player1Name = "Unknown";
        String player2Name = "Unknown";
        
        try {
            // Try to get player names from events
            ReplayEvent[] events = data.getEvents();
            if (events != null && events.length > 0) {
                ReplayEvent firstEvent = events[0];
                UUID player1Id = firstEvent.getPlayerId();
                UUID player2Id = firstEvent.getTargetId();
                
                Player p1 = plugin.getServer().getPlayer(player1Id);
                Player p2 = plugin.getServer().getPlayer(player2Id);
                
                if (p1 != null) player1Name = p1.getName();
                if (p2 != null) player2Name = p2.getName();
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get player names for replay: " + e.getMessage());
        }

        // Create filename with player names and timestamp
        String timestamp = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
        );
        String filename = String.format("replay_%s_vs_%s_%s.dat", player1Name, player2Name, timestamp);
        Path filePath = replayDirectory.resolve(filename);
        
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
             GZIPOutputStream gzipOut = new GZIPOutputStream(fos);
             DataOutputStream dos = new DataOutputStream(gzipOut)) {

            // Write metadata
            dos.writeUTF(sessionId.toString());
            dos.writeUTF(player1Name);
            dos.writeUTF(player2Name);
            dos.writeLong(data.getDurationSeconds());
            dos.writeUTF(data.getCreatedAt().toString());

            // Write event data as serialized objects
            ReplayEvent[] events = data.getEvents();
            dos.writeInt(events.length);
            
            for (ReplayEvent event : events) {
                // Write each event field
                dos.writeUTF(event.getPlayerId().toString());
                dos.writeUTF(event.getTargetId() != null ? event.getTargetId().toString() : "");
                dos.writeUTF(event.getEventType().name());
                dos.writeUTF(event.getTimestamp().toString());
                dos.writeDouble(event.getDamage());
                dos.writeBoolean(event.isCritical());
                dos.writeUTF(event.getLocation() != null ? event.getLocation() : "");
                dos.writeUTF(event.getWeaponType() != null ? event.getWeaponType() : "");
                dos.writeUTF(event.getAdditionalData() != null ? event.getAdditionalData() : "");
            }
            
            plugin.getLogger().info("Saved replay: " + filename);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save replay file: " + e.getMessage());
            throw new IOException("Failed to save replay", e);
        }
    }

    /**
     * Loads replay data from compressed file.
     */
    private ReplayData loadReplayFromFile(UUID sessionId) throws IOException, ClassNotFoundException {
        Path filePath = replayDirectory.resolve("replay_" + sessionId + ".dat");
        if (!Files.exists(filePath)) {
            return null;
        }

        try (FileInputStream fis = new FileInputStream(filePath.toFile());
             GZIPInputStream gzipIn = new GZIPInputStream(fis);
             DataInputStream dis = new DataInputStream(gzipIn)) {

            // Read metadata
            UUID fileSessionId = UUID.fromString(dis.readUTF());
            long duration = dis.readLong();
            LocalDateTime createdAt = LocalDateTime.parse(dis.readUTF());

            // Read compressed event data
            int compressedSize = dis.readInt();
            byte[] compressedData = new byte[compressedSize];
            dis.readFully(compressedData);

            return ReplayData.fromCompressedData(fileSessionId, compressedData, duration, createdAt);
        }
    }

    /**
     * Starts periodic cleanup tasks.
     */
    private void startCleanupTasks() {
        // Cleanup old timeline data
        cleanupExecutor.scheduleAtFixedRate(() -> {
            try {
                timeline.cleanupOldSessions(timelineMaxAgeSeconds * 2);
            } catch (Exception e) {
                logger.warning("Failed to cleanup timeline: " + e.getMessage());
            }
        }, 5, 5, TimeUnit.MINUTES);

        // Cleanup old cache entries
        cleanupExecutor.scheduleAtFixedRate(() -> {
            try {
                cleanupOldCacheEntries();
            } catch (Exception e) {
                logger.warning("Failed to cleanup cache: " + e.getMessage());
            }
        }, 10, 10, TimeUnit.MINUTES);

        // Cleanup old replay files
        cleanupExecutor.scheduleAtFixedRate(() -> {
            try {
                cleanupOldReplayFiles();
            } catch (Exception e) {
                logger.warning("Failed to cleanup replay files: " + e.getMessage());
            }
        }, 30, 30, TimeUnit.MINUTES);
    }

    /**
     * Cleans up old cache entries.
     */
    private void cleanupOldCacheEntries() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(cacheMaxAgeMinutes);
        replayCache.entrySet().removeIf(entry ->
            entry.getValue().getCreatedAt().isBefore(cutoff));
    }

    /**
     * Cleans up old replay files.
     */
    private void cleanupOldReplayFiles() throws IOException {
        if (!Files.exists(replayDirectory)) return;

        LocalDateTime cutoff = LocalDateTime.now().minusDays(7); // Keep for 7 days
        Files.walk(replayDirectory)
            .filter(Files::isRegularFile)
            .filter(path -> path.toString().endsWith(".dat"))
            .forEach(file -> {
                try {
                    // Check file age
                    if (Files.getLastModifiedTime(file).toInstant()
                            .isBefore(cutoff.atZone(java.time.ZoneId.systemDefault()).toInstant())) {
                        Files.delete(file);
                        logger.info("Deleted old replay file: " + file.getFileName());
                    }
                } catch (Exception e) {
                    logger.warning("Failed to process replay file for cleanup: " + file);
                }
            });
    }

    /**
     * Checks if a player has access to replay data.
     */
    private boolean hasAccess(Player player) {
        if (!adminOnlyAccess) {
            return true;
        }

        return player.hasPermission("pvpcombat.admin") ||
               allowedAdmins.contains(player.getUniqueId().toString()) ||
               allowedAdmins.contains(player.getName());
    }

    /**
     * Gets replay manager statistics.
     */
    public ReplayManagerStats getStats() {
        EventTimeline.TimelineStats timelineStats = timeline.getOverallStats();
        return new ReplayManagerStats(
            timelineStats.activeSessions,
            timelineStats.totalEvents,
            timelineStats.totalMemoryBytes,
            replayCache.size()
        );
    }

    /**
     * Shuts down the replay manager.
     */
    public void shutdown() {
        cleanupExecutor.shutdown();
        try {
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        timeline.clearAll();
        replayCache.clear();
    }

    /**
     * Statistics class for replay manager.
     */
    public static class ReplayManagerStats {
        public final int activeSessions;
        public final long totalEvents;
        public final long memoryUsageBytes;
        public final int cachedReplays;

        public ReplayManagerStats(int activeSessions, long totalEvents,
                                long memoryUsageBytes, int cachedReplays) {
            this.activeSessions = activeSessions;
            this.totalEvents = totalEvents;
            this.memoryUsageBytes = memoryUsageBytes;
            this.cachedReplays = cachedReplays;
        }

        @Override
        public String toString() {
            return String.format("ReplayManager: %d sessions, %d events, %d cached, ~%.1f MB",
                activeSessions, totalEvents, cachedReplays, memoryUsageBytes / (1024.0 * 1024.0));
        }
    }
}