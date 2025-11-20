package com.muzlik.pvpcombat.combat;

import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import com.muzlik.pvpcombat.data.CombatSession;
import com.muzlik.pvpcombat.data.CombatState;
import com.muzlik.pvpcombat.data.TimerData;
import com.muzlik.pvpcombat.events.CombatEndEvent;
import com.muzlik.pvpcombat.events.CombatStartEvent;
import com.muzlik.pvpcombat.integration.crossserver.NetworkSyncManager;
import com.muzlik.pvpcombat.interfaces.ICombatManager;
import com.muzlik.pvpcombat.interfaces.IConfigManager;
import com.muzlik.pvpcombat.logging.CombatLogger;
import com.muzlik.pvpcombat.performance.LagManager;
import com.muzlik.pvpcombat.performance.PerformanceMonitor;
import com.muzlik.pvpcombat.utils.AsyncUtils;
import com.muzlik.pvpcombat.utils.CacheManager;
import com.muzlik.pvpcombat.visual.VisualManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe singleton managing all active combat sessions.
 */
public class CombatManager implements ICombatManager {

    private final PvPCombatPlugin plugin;
    private final Map<UUID, CombatSession> activeSessions;
    private final Map<UUID, BukkitTask> sessionTimers;
    private final VisualManager visualManager;
    private final CombatLogger combatLogger;
    private final LagManager lagManager;
    private final NetworkSyncManager networkSyncManager;
    private final PerformanceMonitor performanceMonitor;
    private final CacheManager cacheManager;
    private int defaultTimerSeconds;

    private final CombatTracker combatTracker;

    public CombatManager(PvPCombatPlugin plugin, CombatLogger combatLogger, NetworkSyncManager networkSyncManager,
                        PerformanceMonitor performanceMonitor, CacheManager cacheManager, IConfigManager configManager) {
        this.plugin = plugin;
        this.activeSessions = new ConcurrentHashMap<>();
        this.sessionTimers = new ConcurrentHashMap<>();
        this.visualManager = new VisualManager(plugin, configManager);
        this.combatLogger = combatLogger;
        this.combatTracker = new CombatTracker();
        this.lagManager = new LagManager(plugin, performanceMonitor.getTpsMonitor(), performanceMonitor);
        this.combatTracker.setLagManager(this.lagManager);
        this.networkSyncManager = networkSyncManager;
        this.performanceMonitor = performanceMonitor;
        this.cacheManager = cacheManager;
        this.defaultTimerSeconds = plugin.getConfig().getInt("combat.duration", 30);
    }

    @Override
    public UUID startCombat(Player attacker, Player defender) {
        performanceMonitor.startOperation("combat-start");

        try {
            // Check cache first for existing combat state
            String cacheKey = attacker.getUniqueId() + ":" + defender.getUniqueId();
            CombatSession cachedSession = (CombatSession) cacheManager.get("combat-states", cacheKey);

            if (cachedSession != null && cachedSession.isActive()) {
                return null; // Already in combat
            }

            // Check if either player is already in combat
            if (isInCombat(attacker) || isInCombat(defender)) {
                return null; // Cannot start new combat
            }

            UUID sessionId = UUID.randomUUID();
            CombatSession session = new CombatSession(sessionId, attacker, defender, defaultTimerSeconds);

            activeSessions.put(attacker.getUniqueId(), session);
            activeSessions.put(defender.getUniqueId(), session);

            // Cache the combat state
            cacheManager.put("combat-states", cacheKey, session);

            // Register session with lag manager for performance monitoring
            lagManager.registerSession(sessionId);

            // Start timer task asynchronously
            AsyncUtils.runAsync(plugin, () -> startTimerTask(session), "combat-processing");

            // Initialize visual elements (keep on main thread for thread safety)
            AsyncUtils.runSync(plugin, () -> {
                visualManager.displayBossBar(sessionId.toString());
                visualManager.getActionBarManager().startActionBarUpdates(sessionId.toString(), attacker, defender);
                visualManager.getSoundManager().playCombatStartSound(attacker);
                visualManager.getSoundManager().playCombatStartSound(defender);
            });

            // Fire CombatStartEvent
            plugin.getServer().getPluginManager().callEvent(new CombatStartEvent(session, attacker, defender));

            // Broadcast combat start across network asynchronously if sync is enabled
            if (networkSyncManager != null && networkSyncManager.isEnabled()) {
                AsyncUtils.runAsync(plugin, () -> {
                    networkSyncManager.broadcastCombatStart(session).whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            plugin.getLogger().warning("Failed to broadcast combat start: " + throwable.getMessage());
                        }
                    });
                }, "combat-processing");
            }

            // Log combat start asynchronously
            AsyncUtils.runAsync(plugin, () ->
                combatLogger.logCombatStart(sessionId, attacker, defender), "combat-processing");

            plugin.getLogger().info("Combat started between " + attacker.getName() + " and " + defender.getName());
            return sessionId;
        } finally {
            performanceMonitor.endOperation("combat-start");
        }
    }

    @Override
    public boolean endCombat(UUID playerId) {
        performanceMonitor.startOperation("combat-end");

        try {
            CombatSession session = activeSessions.remove(playerId);
            if (session != null) {
                UUID sessionId = session.getSessionId();

                // Calculate combat duration
                long combatDuration = System.currentTimeMillis() - session.getStartTime();
                
                // Update combat tracker with combat time for both players
                com.muzlik.pvpcombat.data.PlayerCombatData attackerData = combatTracker.getPlayerData(session.getAttacker().getUniqueId());
                com.muzlik.pvpcombat.data.PlayerCombatData defenderData = combatTracker.getPlayerData(session.getDefender().getUniqueId());
                
                attackerData.addCombatTime(combatDuration);
                defenderData.addCombatTime(combatDuration);
                
                // Increment total combats for both players
                attackerData.incrementCombats();
                defenderData.incrementCombats();
                
                // Update last combat timestamp
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                attackerData.setLastCombat(now);
                defenderData.setLastCombat(now);
                
                // Log the combat data for debugging
                plugin.getLogger().info(String.format("Combat ended - %s: %.1f damage dealt, %d total combats | %s: %.1f damage dealt, %d total combats",
                    session.getAttacker().getName(), attackerData.getTotalDamageDealt(), attackerData.getTotalCombats(),
                    session.getDefender().getName(), defenderData.getTotalDamageDealt(), defenderData.getTotalCombats()));

                // Remove from cache
                String cacheKey = session.getAttacker().getUniqueId() + ":" + session.getDefender().getUniqueId();
                cacheManager.remove("combat-states", cacheKey);

                // Remove both players from the session
                activeSessions.remove(session.getAttacker().getUniqueId());
                activeSessions.remove(session.getDefender().getUniqueId());

                session.setActive(false);
                session.setState(CombatState.NOT_IN_COMBAT);

                // Unregister session from lag manager
                lagManager.unregisterSession(sessionId);

                // Cancel timer task
                BukkitTask timerTask = sessionTimers.remove(sessionId);
                if (timerTask != null) {
                    timerTask.cancel();
                }

                // Clear visual elements (keep on main thread)
                AsyncUtils.runSync(plugin, () -> {
                    visualManager.clearVisuals(session.getAttacker());
                    visualManager.clearVisuals(session.getDefender());
                    visualManager.getSoundManager().playCombatEndSound(session.getAttacker());
                    visualManager.getSoundManager().playCombatEndSound(session.getDefender());
                });

                // Fire CombatEndEvent
                Player winner = session.getAttacker().getUniqueId().equals(playerId) ? session.getDefender() : session.getAttacker();
                Player loser = session.getAttacker().getUniqueId().equals(playerId) ? session.getAttacker() : session.getDefender();
                plugin.getServer().getPluginManager().callEvent(new CombatEndEvent(session, winner, loser, CombatEndEvent.CombatEndReason.FORCE_END));

                // Broadcast combat end across network asynchronously if sync is enabled
                if (networkSyncManager != null && networkSyncManager.isEnabled()) {
                    AsyncUtils.runAsync(plugin, () -> {
                        networkSyncManager.broadcastCombatEnd(session.getSessionId(), "Combat ended").whenComplete((result, throwable) -> {
                            if (throwable != null) {
                                plugin.getLogger().warning("Failed to broadcast combat end: " + throwable.getMessage());
                            }
                        });
                    }, "combat-processing");
                }

                // Log combat end and generate summaries asynchronously
                AsyncUtils.runAsync(plugin, () -> {
                    combatLogger.logCombatEnd(session.getSessionId(), session.getAttacker(),
                                              session.getDefender(), "Combat ended");
                    combatLogger.generateSummary(session.getSessionId(), session.getAttacker());
                    combatLogger.generateSummary(session.getSessionId(), session.getDefender());
                }, "combat-processing");

                plugin.getLogger().info("Combat ended for player " + playerId + " (Duration: " + (combatDuration / 1000) + "s)");
                return true;
            }
            return false;
        } finally {
            performanceMonitor.endOperation("combat-end");
        }
    }

    @Override
    public boolean isInCombat(Player player) {
        return activeSessions.containsKey(player.getUniqueId());
    }

    @Override
    public boolean resetTimer(UUID sessionId) {
        // Find session by sessionId (need to iterate since map is keyed by player UUID)
        for (CombatSession session : activeSessions.values()) {
            if (session.getSessionId().equals(sessionId)) {
                session.resetTimer();

                // Update bossbar progress
                double progress = session.getTimerData().getProgress();
                visualManager.updateBossBarProgress(sessionId.toString(), progress);

                // Play timer reset sound
                visualManager.getSoundManager().playTimerResetSound(session.getAttacker());
                visualManager.getSoundManager().playTimerResetSound(session.getDefender());

                return true;
            }
        }
        return false;
    }

    @Override
    public Player getOpponent(Player player) {
        CombatSession session = activeSessions.get(player.getUniqueId());
        return session != null ? session.getOpponent(player) : null;
    }

    /**
     * Gets all active sessions (for cleanup).
     */
    public Map<UUID, CombatSession> getActiveSessions() {
        return new ConcurrentHashMap<>(activeSessions);
    }

    /**
     * Starts a timer task for a combat session.
     */
    private void startTimerTask(CombatSession session) {
        UUID sessionId = session.getSessionId();

        BukkitRunnable timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!session.isActive()) {
                    cancel();
                    return;
                }

                // Update player ping data for lag detection
                lagManager.updatePlayerPing(session.getAttacker());
                lagManager.updatePlayerPing(session.getDefender());

                // Check for lag adjustments
                int lagExtension = lagManager.checkAndApplyLagAdjustment(sessionId,
                        session.getAttacker(), session.getDefender());

                if (lagExtension > 0) {
                    // Apply lag extension to timer
                    TimerData timerData = session.getTimerData();
                    int newRemaining = timerData.getRemainingSeconds() + lagExtension;
                    timerData.setRemainingSeconds(newRemaining);
                    session.setTimerSeconds(newRemaining);

                    plugin.getLogger().fine(String.format("Extended combat timer for session %s by %d seconds due to lag",
                            sessionId, lagExtension));
                }

                // Update timer and check if expired
                boolean expired = session.updateTimer();

                if (expired) {
                    // Combat timer expired - end combat
                    endCombat(session.getAttacker().getUniqueId());
                } else {
                    // Periodic sync of combat state across network
                    if (networkSyncManager != null && networkSyncManager.isEnabled()) {
                        // Sync every 30 seconds or when significant changes occur
                        if (session.getTimerData().getRemainingSeconds() % 30 == 0) {
                            networkSyncManager.broadcastCombatStart(session).whenComplete((result, throwable) -> {
                                if (throwable != null) {
                                    plugin.getLogger().fine("Failed to sync combat state: " + throwable.getMessage());
                                }
                            });
                        }
                    }

                    // Update visual elements with current time
                    int remainingTime = session.getRemainingTime();
                    double progress = session.getTimerData().getProgress();
                    
                    // Update bossbar progress and title
                    visualManager.updateBossBarProgress(sessionId.toString(), progress);
                    String title = plugin.getConfig().getString("combat.bossbar.title", "&cCombat: &f{time_left}s")
                        .replace("{time_left}", String.valueOf(remainingTime))
                        .replace("&", "§");
                    visualManager.updateBossBarTitle(sessionId.toString(), title);

                    // Play warning sound at 5 seconds
                    if (remainingTime == 5) {
                        visualManager.getSoundManager().playTimerWarningSound(session.getAttacker());
                        visualManager.getSoundManager().playTimerWarningSound(session.getDefender());
                    }
                }
            }
        };

        // Run every second (20 ticks)
        timerTask.runTaskTimer(plugin, 0L, 20L);
        sessionTimers.put(sessionId, (BukkitTask) timerTask);
    }

    /**
     * Gets a session by its ID.
     */
    public CombatSession getSessionById(String sessionId) {
        try {
            UUID id = UUID.fromString(sessionId);
            for (CombatSession session : activeSessions.values()) {
                if (session.getSessionId().equals(id)) {
                    return session;
                }
            }
        } catch (IllegalArgumentException e) {
            // Invalid UUID format
        }
        return null;
    }

    /**
     * Gets the visual manager.
     */
    public VisualManager getVisualManager() {
        return visualManager;
    }

    /**
     * Gets the lag manager for performance monitoring.
     */
    public LagManager getLagManager() {
        return lagManager;
    }

    /**
     * Cleans up expired sessions.
     */
    public void cleanupExpiredSessions() {
        performanceMonitor.startOperation("cleanup-expired-sessions");

        try {
            // Run cleanup asynchronously to avoid blocking main thread
            AsyncUtils.runAsync(plugin, () -> {
                activeSessions.entrySet().removeIf(entry -> {
                    CombatSession session = entry.getValue();
                    if (session.isExpired()) {
                        // End combat synchronously as it needs to interact with main thread
                        AsyncUtils.runSync(plugin, () -> endCombat(entry.getKey()));
                        return true;
                    }
                    return false;
                });
            }, "cleanup-tasks");
        } finally {
            performanceMonitor.endOperation("cleanup-expired-sessions");
        }
    }

    /**
     * Gets the network sync manager for cross-server functionality.
     */
    public NetworkSyncManager getNetworkSyncManager() {
        return networkSyncManager;
    }

    /**
     * Gets the combat tracker for statistics.
     */
    public CombatTracker getCombatTracker() {
        return combatTracker;
    }
}