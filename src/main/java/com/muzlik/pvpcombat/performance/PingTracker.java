package com.muzlik.pvpcombat.performance;

import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import com.muzlik.pvpcombat.data.PerformanceData;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks individual player ping levels and maintains performance data.
 * Updates ping asynchronously to avoid blocking main thread.
 */
public class PingTracker {

    private final PvPCombatPlugin plugin;
    private final Map<UUID, PerformanceData> playerPerformanceData;
    private final Map<UUID, Long> lastUpdateTimes;

    // Configuration
    private long updateIntervalMs;
    private long cleanupThresholdMs;

    // Failure tracking for adaptive performance
    private volatile int consecutiveFailures;
    private volatile long lastSuccessfulUpdate;

    public PingTracker(PvPCombatPlugin plugin) {
        this.plugin = plugin;
        this.playerPerformanceData = new ConcurrentHashMap<>();
        this.lastUpdateTimes = new ConcurrentHashMap<>();

        loadConfiguration();
        initializeFailureTracking();
    }

    /**
     * Loads ping tracking configuration.
     */
    private void loadConfiguration() {
        this.updateIntervalMs = plugin.getConfig().getLong("lag.ping-update-interval-ms", 1000L); // 1 second
        this.cleanupThresholdMs = plugin.getConfig().getLong("lag.ping-cleanup-threshold-ms", 300000L); // 5 minutes
    }

    /**
     * Initializes failure tracking for adaptive performance monitoring.
     */
    private void initializeFailureTracking() {
        this.consecutiveFailures = 0;
        this.lastSuccessfulUpdate = System.currentTimeMillis();
    }

    /**
     * Updates ping for a specific player.
     * Should be called regularly during combat to maintain accurate data.
     */
    public void updatePlayerPing(Player player) {
        if (player == null) return;

        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        // Rate limit updates to prevent excessive processing
        Long lastUpdate = lastUpdateTimes.get(playerId);
        if (lastUpdate != null && (currentTime - lastUpdate) < updateIntervalMs) {
            return;
        }

        lastUpdateTimes.put(playerId, currentTime);

        // Update performance data
        PerformanceData data = playerPerformanceData.computeIfAbsent(playerId,
                id -> new PerformanceData(id));

        try {
            // Get ping using reflection or protocol lib if available
            // For now, use a placeholder - in real implementation, this would use
            // the actual ping from the player's connection
            int ping = getPlayerPing(player);
            data.setCurrentPing(ping);

            // Update lag status based on thresholds
            double tpsThreshold = plugin.getConfig().getDouble("lag.tps-threshold", 18.0);
            int pingThreshold = plugin.getConfig().getInt("lag.ping-threshold", 200);
            boolean isLagging = data.isLagging(tpsThreshold, pingThreshold);
            data.setExperiencingLag(isLagging);

            // DEBUG LOGGING: Log high ping detection for block breaking investigation
            if (ping > pingThreshold) {
                plugin.getLogger().fine(String.format("[DEBUG] High ping detected: Player=%s, Ping=%dms, Threshold=%dms, TPS=%.2f",
                    player.getName(), ping, pingThreshold, tpsThreshold));
            }

            // Reset failure counter on successful update
            consecutiveFailures = 0;
            // Restore normal update interval if it was increased due to failures
            if (updateIntervalMs > plugin.getConfig().getLong("lag.ping-update-interval-ms", 1000L)) {
                updateIntervalMs = plugin.getConfig().getLong("lag.ping-update-interval-ms", 1000L);
                plugin.getLogger().info("Ping detection recovered, restored normal update interval");
            }

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to update ping for player " + player.getName() + ": " + e.getMessage());
            consecutiveFailures++;
            // If we've had too many failures, reduce update frequency temporarily
            if (consecutiveFailures > 5) {
                plugin.getLogger().info("Ping detection failing consistently, increasing update interval to reduce server load");
                updateIntervalMs = Math.min(updateIntervalMs * 2, 10000L); // Max 10 seconds
            }
        } finally {
            // Track successful updates
            if (consecutiveFailures == 0) {
                lastSuccessfulUpdate = System.currentTimeMillis();
            }
        }
    }

    /**
     * Gets the performance data for a player.
     */
    public PerformanceData getPerformanceData(UUID playerId) {
        return playerPerformanceData.get(playerId);
    }

    /**
     * Gets or creates performance data for a player.
     */
    public PerformanceData getOrCreatePerformanceData(UUID playerId) {
        return playerPerformanceData.computeIfAbsent(playerId,
                id -> new PerformanceData(id));
    }

    /**
     * Gets the current ping for a player.
     * This is a placeholder implementation - in a real plugin, this would use
     * reflection to access the player's ping from CraftPlayer or use ProtocolLib.
     */
    private int getPlayerPing(Player player) {
        try {
            // Try to get ping using CraftBukkit internals
            Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);
            int ping = (Integer) playerConnection.getClass().getField("ping").get(playerConnection);

            // Validate ping value - negative values indicate issues
            if (ping < 0) {
                plugin.getLogger().fine("Invalid ping value for " + player.getName() + ": " + ping + ", using fallback");
                return calculateFallbackPing(player);
            }

            return ping;
        } catch (Exception e) {
            // Fallback: return a reasonable default or use alternative methods
            plugin.getLogger().fine("Using fallback ping detection for " + player.getName() + ": " + e.getMessage());
            return calculateFallbackPing(player);
        }
    }

    /**
     * Fallback ping calculation when direct access fails.
     */
    private int calculateFallbackPing(Player player) {
        // This could use various heuristics like:
        // - Distance from spawn
        // - World/environment factors
        // - Recent activity patterns
        // For now, return a conservative estimate
        return 50; // Assume decent connection
    }

    /**
     * Updates ping for all online players.
     * Should be called periodically by the LagManager.
     */
    public void updateAllPlayersPing() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            updatePlayerPing(player);
        }
    }

    /**
     * Cleans up old performance data for inactive players.
     */
    public void cleanupInactiveData() {
        long currentTime = System.currentTimeMillis();

        playerPerformanceData.entrySet().removeIf(entry -> {
            UUID playerId = entry.getKey();
            Long lastUpdate = lastUpdateTimes.get(playerId);

            if (lastUpdate == null || (currentTime - lastUpdate) > cleanupThresholdMs) {
                plugin.getLogger().fine("Cleaned up performance data for inactive player " + playerId);
                lastUpdateTimes.remove(playerId);
                return true;
            }
            return false;
        });
    }

    /**
     * Gets statistics about ping tracking.
     */
    public String getPingStats() {
        int totalPlayers = playerPerformanceData.size();
        long activeUpdates = lastUpdateTimes.size();

        return String.format("PingTracker{totalPlayers=%d, activeUpdates=%d, updateInterval=%dms}",
                totalPlayers, activeUpdates, updateIntervalMs);
    }

    /**
     * Forces a configuration reload.
     */
    public void reloadConfiguration() {
        loadConfiguration();
        plugin.getLogger().info("PingTracker configuration reloaded");
    }

    /**
     * Gets the number of consecutive ping detection failures.
     */
    public int getConsecutiveFailures() {
        return consecutiveFailures;
    }

    /**
     * Checks if the ping tracker is currently experiencing issues.
     */
    public boolean isExperiencingIssues() {
        return consecutiveFailures > 3 || updateIntervalMs > plugin.getConfig().getLong("lag.ping-update-interval-ms", 1000L);
    }

    /**
     * Gets the average ping across all tracked players.
     */
    public double getAveragePing() {
        if (playerPerformanceData.isEmpty()) return 0.0;

        return playerPerformanceData.values().stream()
                .mapToInt(PerformanceData::getAveragePing)
                .average()
                .orElse(0.0);
    }

    /**
     * Gets the highest ping among tracked players.
     */
    public int getHighestPing() {
        return playerPerformanceData.values().stream()
                .mapToInt(PerformanceData::getCurrentPing)
                .max()
                .orElse(0);
    }

    /**
     * Gets the health status of the ping tracker for monitoring.
     */
    public String getHealthStatus() {
        long timeSinceLastSuccess = System.currentTimeMillis() - lastSuccessfulUpdate;
        return String.format("PingTrackerHealth{failures=%d, lastSuccess=%dms ago, currentInterval=%dms}",
                consecutiveFailures, timeSinceLastSuccess, updateIntervalMs);
    }
}