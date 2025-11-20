package com.muzlik.pvpcombat.utils;

import com.muzlik.pvpcombat.combat.CombatManager;
import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import com.muzlik.pvpcombat.data.CombatSession;
import com.muzlik.pvpcombat.performance.PerformanceMonitor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Smart cleanup manager for inactive combat pairs and expired sessions.
 * Performs periodic cleanup to maintain performance and memory efficiency.
 */
public class CleanupManager {

    private final PvPCombatPlugin plugin;
    private final CombatManager combatManager;
    private final PerformanceMonitor performanceMonitor;
    private final CacheManager cacheManager;

    // Cleanup intervals
    private static final long EXPIRED_SESSIONS_INTERVAL = 5 * 60 * 20; // 5 minutes in ticks
    private static final long INACTIVE_PLAYERS_INTERVAL = 10 * 60 * 20; // 10 minutes in ticks
    private static final long CACHE_CLEANUP_INTERVAL = 15 * 60 * 20; // 15 minutes in ticks

    // Thresholds
    private static final long INACTIVE_PLAYER_THRESHOLD = 10 * 60 * 1000; // 10 minutes in milliseconds
    private static final int MAX_CLEANUP_BATCH_SIZE = 50; // Max sessions to clean per batch

    public CleanupManager(PvPCombatPlugin plugin, CombatManager combatManager,
                         PerformanceMonitor performanceMonitor, CacheManager cacheManager) {
        this.plugin = plugin;
        this.combatManager = combatManager;
        this.performanceMonitor = performanceMonitor;
        this.cacheManager = cacheManager;

        // Schedule periodic cleanup tasks
        scheduleCleanupTasks();
    }

    /**
     * Schedules all periodic cleanup tasks.
     */
    private void scheduleCleanupTasks() {
        // Expired sessions cleanup - every 5 minutes
        AsyncUtils.runSyncTimer(plugin, this::cleanupExpiredSessions,
            EXPIRED_SESSIONS_INTERVAL, EXPIRED_SESSIONS_INTERVAL);

        // Inactive players cleanup - every 10 minutes
        AsyncUtils.runSyncTimer(plugin, this::cleanupInactivePlayers,
            INACTIVE_PLAYERS_INTERVAL, INACTIVE_PLAYERS_INTERVAL);

        // Cache cleanup - every 15 minutes
        AsyncUtils.runSyncTimer(plugin, this::performCacheMaintenance,
            CACHE_CLEANUP_INTERVAL, CACHE_CLEANUP_INTERVAL);

        plugin.getLogger().info("CleanupManager: Scheduled periodic cleanup tasks");
    }

    /**
     * Cleans up expired combat sessions.
     */
    private void cleanupExpiredSessions() {
        performanceMonitor.startOperation("cleanup-expired-sessions");

        try {
            Map<UUID, CombatSession> activeSessions = combatManager.getActiveSessions();
            int cleanedCount = 0;

            for (Map.Entry<UUID, CombatSession> entry : activeSessions.entrySet()) {
                CombatSession session = entry.getValue();

                if (session.isExpired() && cleanedCount < MAX_CLEANUP_BATCH_SIZE) {
                    combatManager.endCombat(entry.getKey());
                    cleanedCount++;
                }
            }

            if (cleanedCount > 0) {
                plugin.getLogger().info(String.format("CleanupManager: Cleaned up %d expired combat sessions", cleanedCount));
            }
        } finally {
            performanceMonitor.endOperation("cleanup-expired-sessions");
        }
    }

    /**
     * Cleans up inactive players who may have disconnected or become inactive.
     */
    private void cleanupInactivePlayers() {
        performanceMonitor.startOperation("cleanup-inactive-players");

        try {
            Map<UUID, CombatSession> activeSessions = combatManager.getActiveSessions();
            int cleanedCount = 0;
            long currentTime = System.currentTimeMillis();

            for (Map.Entry<UUID, CombatSession> entry : activeSessions.entrySet()) {
                CombatSession session = entry.getValue();

                // Check if either player has been inactive too long
                Player attacker = session.getAttacker();
                Player defender = session.getDefender();

                boolean attackerInactive = attacker != null &&
                    (currentTime - attacker.getLastPlayed()) > INACTIVE_PLAYER_THRESHOLD;
                boolean defenderInactive = defender != null &&
                    (currentTime - defender.getLastPlayed()) > INACTIVE_PLAYER_THRESHOLD;

                if ((attackerInactive || defenderInactive) && cleanedCount < MAX_CLEANUP_BATCH_SIZE) {
                    combatManager.endCombat(entry.getKey());
                    cleanedCount++;

                    if (attackerInactive) {
                        plugin.getLogger().fine(String.format("CleanupManager: Ended combat for inactive player %s",
                            attacker != null ? attacker.getName() : "unknown"));
                    }
                    if (defenderInactive) {
                        plugin.getLogger().fine(String.format("CleanupManager: Ended combat for inactive player %s",
                            defender != null ? defender.getName() : "unknown"));
                    }
                }
            }

            if (cleanedCount > 0) {
                plugin.getLogger().info(String.format("CleanupManager: Cleaned up %d combats with inactive players", cleanedCount));
            }
        } finally {
            performanceMonitor.endOperation("cleanup-inactive-players");
        }
    }

    /**
     * Performs cache maintenance and cleanup.
     */
    private void performCacheMaintenance() {
        performanceMonitor.startOperation("cache-maintenance");

        try {
            // Cache cleanup is handled automatically by CacheManager,
            // but we can add additional maintenance here if needed

            long cacheSize = cacheManager.getTotalCacheSize();
            if (cacheSize > 1000) { // Arbitrary threshold for logging
                plugin.getLogger().info(String.format("CleanupManager: Cache maintenance completed. Total cached items: %d", cacheSize));
            }

            // Force cleanup of expired entries
            cacheManager.clear("performance-metrics"); // Clear old performance data

        } finally {
            performanceMonitor.endOperation("cache-maintenance");
        }
    }

    /**
     * Forces immediate cleanup of all expired sessions.
     */
    public void forceCleanup() {
        plugin.getLogger().info("CleanupManager: Forcing immediate cleanup of all expired sessions");
        cleanupExpiredSessions();
        cleanupInactivePlayers();
        performCacheMaintenance();
    }

    /**
     * Gets cleanup statistics for monitoring.
     */
    public String getCleanupStats() {
        Map<UUID, CombatSession> activeSessions = combatManager.getActiveSessions();
        return String.format("CleanupManager: active_sessions=%d, cache_size=%d",
                activeSessions.size(), cacheManager.getTotalCacheSize());
    }
}