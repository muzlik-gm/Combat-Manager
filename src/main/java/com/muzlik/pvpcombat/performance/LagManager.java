package com.muzlik.pvpcombat.performance;

import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import com.muzlik.pvpcombat.data.LagAdjustment;
import com.muzlik.pvpcombat.data.PerformanceData;
import com.muzlik.pvpcombat.utils.AsyncUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central manager for lag detection and timer adjustments during combat.
 * Monitors server TPS and player ping to make real-time adjustments to combat timers.
 */
public class LagManager {

    private final PvPCombatPlugin plugin;
    private final PingTracker pingTracker;
    private final TPSMonitor tpsMonitor;
    private final PerformanceMonitor performanceMonitor;

    // Lag adjustments per session
    private final Map<UUID, LagAdjustment> sessionAdjustments;

    // Configuration values loaded from config
    private double tpsThreshold;
    private int pingThreshold;
    private int baseExtensionSeconds;
    private double extensionMultiplier;
    private long cleanupIntervalTicks;

    // Server-wide lag tracking
    private volatile boolean serverWideLagDetected;
    private volatile long lastServerLagTime;

    public LagManager(PvPCombatPlugin plugin, TPSMonitor tpsMonitor, PerformanceMonitor performanceMonitor) {
        this.plugin = plugin;
        this.pingTracker = new PingTracker(plugin);
        this.tpsMonitor = tpsMonitor;
        this.performanceMonitor = performanceMonitor;
        this.sessionAdjustments = new ConcurrentHashMap<>();

        loadConfiguration();
        startMonitoringTasks();
    }

    /**
     * Loads lag-related configuration from plugin config.
     */
    private void loadConfiguration() {
        this.tpsThreshold = plugin.getConfig().getDouble("lag.tps-threshold", 18.0);
        this.pingThreshold = plugin.getConfig().getInt("lag.ping-threshold", 200);
        this.baseExtensionSeconds = plugin.getConfig().getInt("lag.base-extension-seconds", 5);
        this.extensionMultiplier = plugin.getConfig().getDouble("lag.extension-multiplier", 1.5);
        this.cleanupIntervalTicks = plugin.getConfig().getLong("lag.cleanup-interval-ticks", 1200L); // 60 seconds
    }

    /**
     * Starts the background monitoring tasks.
     */
    private void startMonitoringTasks() {
        // TPS monitoring task (runs every second) - performance aware
        AsyncUtils.runSyncTimer(plugin, () -> {
            performanceMonitor.timeOperation("tps-monitoring", () -> {
                tpsMonitor.updateTPS();
                checkServerWideLag();
            });
        }, 20L, 20L);

        // Cleanup task (runs every cleanup interval)
        new BukkitRunnable() {
            @Override
            public void run() {
                cleanupExpiredAdjustments();
            }
        }.runTaskTimer(plugin, cleanupIntervalTicks, cleanupIntervalTicks);
    }

    /**
     * Registers a combat session for lag monitoring.
     */
    public void registerSession(UUID sessionId) {
        sessionAdjustments.put(sessionId, new LagAdjustment(sessionId));
    }

    /**
     * Unregisters a combat session and cleans up its adjustments.
     */
    public void unregisterSession(UUID sessionId) {
        LagAdjustment adjustment = sessionAdjustments.remove(sessionId);
        if (adjustment != null) {
            plugin.getLogger().info("Lag adjustments cleaned up for session " + sessionId + ": " + adjustment.getAdjustmentSummary());
        }
    }

    /**
     * Updates ping for a player and checks for lag adjustments.
     */
    public void updatePlayerPing(Player player) {
        pingTracker.updatePlayerPing(player);
    }

    /**
     * Checks if lag adjustments should be applied for a combat session.
     */
    public int checkAndApplyLagAdjustment(UUID sessionId, Player player1, Player player2) {
        LagAdjustment adjustment = sessionAdjustments.get(sessionId);
        if (adjustment == null) return 0;

        PerformanceData perf1 = pingTracker.getPerformanceData(player1.getUniqueId());
        PerformanceData perf2 = pingTracker.getPerformanceData(player2.getUniqueId());

        if (perf1 == null || perf2 == null) return 0;

        double lagSeverity = calculateCombinedLagSeverity(perf1, perf2);

        if (lagSeverity > 0) {
            adjustment.setActive(true);
            int extension = adjustment.calculateExtension(lagSeverity, baseExtensionSeconds, extensionMultiplier);
            plugin.getLogger().fine(String.format("Applied %d second lag extension to session %s (severity: %.2f)",
                    extension, sessionId, lagSeverity));
            return extension;
        } else {
            adjustment.setActive(false);
            return 0;
        }
    }

    /**
     * Calculates combined lag severity for both players in combat.
     */
    private double calculateCombinedLagSeverity(PerformanceData perf1, PerformanceData perf2) {
        double severity1 = perf1.getLagSeverity(tpsThreshold, pingThreshold);
        double severity2 = perf2.getLagSeverity(tpsThreshold, pingThreshold);

        // If server-wide lag is detected, increase severity
        double serverLagMultiplier = serverWideLagDetected ? 1.5 : 1.0;

        // Return the higher severity between the two players
        return Math.max(severity1, severity2) * serverLagMultiplier;
    }

    /**
     * Checks for server-wide lag conditions.
     */
    private void checkServerWideLag() {
        double currentTps = tpsMonitor.getCurrentTPS();

        if (currentTps < tpsThreshold) {
            if (!serverWideLagDetected) {
                serverWideLagDetected = true;
                lastServerLagTime = System.currentTimeMillis();
                plugin.getLogger().warning(String.format("Server-wide lag detected: TPS %.2f (threshold: %.2f)",
                        currentTps, tpsThreshold));

                // DEBUG LOGGING: Log active sessions during lag spike
                plugin.getLogger().fine(String.format("[DEBUG] Server lag spike: Active lag adjustments=%d, TPS=%.2f",
                    sessionAdjustments.size(), currentTps));
            }
        } else if (serverWideLagDetected && currentTps >= tpsThreshold + 1.0) {
            // Add some hysteresis to prevent flapping
            serverWideLagDetected = false;
            plugin.getLogger().info("Server-wide lag condition cleared: TPS " + currentTps);
        }
    }

    /**
     * Gets the current adjustment for a session.
     */
    public LagAdjustment getSessionAdjustment(UUID sessionId) {
        return sessionAdjustments.get(sessionId);
    }

    /**
     * Checks if server-wide lag is currently detected.
     */
    public boolean isServerWideLagDetected() {
        return serverWideLagDetected;
    }

    /**
     * Gets time since last server lag spike.
     */
    public long getTimeSinceLastServerLag() {
        return System.currentTimeMillis() - lastServerLagTime;
    }

    /**
     * Forces a configuration reload.
     */
    public void reloadConfiguration() {
        loadConfiguration();
        plugin.getLogger().info("LagManager configuration reloaded");
    }

    /**
     * Cleans up expired lag adjustments.
     */
    private void cleanupExpiredAdjustments() {
        long maxInactiveTime = cleanupIntervalTicks * 50; // Convert ticks to milliseconds
        sessionAdjustments.entrySet().removeIf(entry -> {
            LagAdjustment adjustment = entry.getValue();
            if (adjustment.shouldCleanup(maxInactiveTime)) {
                plugin.getLogger().fine("Cleaned up expired lag adjustment for session " + entry.getKey());
                return true;
            }
            return false;
        });
    }

    /**
     * Gets performance statistics for monitoring/debugging.
     */
    public String getPerformanceStats() {
        return String.format("LagManager{serverLag=%s, activeAdjustments=%d, tps=%.2f, pingThreshold=%d}",
                serverWideLagDetected, sessionAdjustments.size(), tpsMonitor.getCurrentTPS(), pingThreshold);
    }
}