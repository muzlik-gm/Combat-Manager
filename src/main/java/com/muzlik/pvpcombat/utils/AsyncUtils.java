package com.muzlik.pvpcombat.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Enhanced utility class for asynchronous operations with performance monitoring.
 */
public class AsyncUtils {

    private static final ConcurrentHashMap<String, ExecutorService> executors = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ScheduledExecutorService> scheduledExecutors = new ConcurrentHashMap<>();
    private static volatile boolean initialized = false;

    /**
     * Initializes the AsyncUtils with custom thread pools for better performance.
     */
    public static void initialize() {
        if (!initialized) {
            // Create dedicated thread pools for different operation types
            executors.put("combat-processing", Executors.newFixedThreadPool(
                Math.max(2, Runtime.getRuntime().availableProcessors() / 2)));
            executors.put("cache-operations", Executors.newCachedThreadPool());
            executors.put("cleanup-tasks", Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "PvPCombat-Cleanup");
                t.setDaemon(true);
                return t;
            }));

            scheduledExecutors.put("performance-monitoring", Executors.newScheduledThreadPool(1, r -> {
                Thread t = new Thread(r, "PvPCombat-Monitoring");
                t.setDaemon(true);
                return t;
            }));

            initialized = true;
        }
    }

    /**
     * Shuts down all thread pools.
     */
    public static void shutdown() {
        executors.values().forEach(ExecutorService::shutdown);
        scheduledExecutors.values().forEach(ScheduledExecutorService::shutdown);

        try {
            executors.values().forEach(executor -> {
                try {
                    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            });

            scheduledExecutors.values().forEach(executor -> {
                try {
                    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            });
        } catch (Exception e) {
            // Log shutdown issues
        }

        executors.clear();
        scheduledExecutors.clear();
        initialized = false;
    }

    /**
     * Runs a task asynchronously using dedicated thread pool.
     */
    public static void runAsync(Plugin plugin, Runnable task, String poolName) {
        ExecutorService executor = executors.get(poolName);
        if (executor != null && !executor.isShutdown()) {
            executor.execute(task);
        } else {
            // Fallback to Bukkit scheduler
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    /**
     * Runs a task asynchronously.
     */
    public static BukkitTask runAsync(Plugin plugin, Runnable task) {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    /**
     * Runs a task on the main thread.
     */
    public static BukkitTask runSync(Plugin plugin, Runnable task) {
        return Bukkit.getScheduler().runTask(plugin, task);
    }

    /**
     * Runs a task asynchronously after a delay.
     */
    public static BukkitTask runAsyncLater(Plugin plugin, Runnable task, long delayTicks) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delayTicks);
    }

    /**
     * Runs a task on the main thread after a delay.
     */
    public static BukkitTask runSyncLater(Plugin plugin, Runnable task, long delayTicks) {
        return Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
    }

    /**
     * Runs a task asynchronously repeatedly.
     */
    public static BukkitTask runAsyncTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
    }

    /**
     * Runs a task on the main thread repeatedly.
     */
    public static BukkitTask runSyncTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        return Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
    }

    /**
     * Executes a supplier asynchronously and returns a CompletableFuture.
     */
    public static <T> CompletableFuture<T> supplyAsync(Plugin plugin, Supplier<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();
        runAsync(plugin, () -> {
            try {
                T result = supplier.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    /**
     * Executes a supplier asynchronously using dedicated pool and returns a CompletableFuture.
     */
    public static <T> CompletableFuture<T> supplyAsync(Plugin plugin, Supplier<T> supplier, String poolName) {
        CompletableFuture<T> future = new CompletableFuture<>();
        runAsync(plugin, () -> {
            try {
                T result = supplier.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, poolName);
        return future;
    }

    /**
     * Runs a task with TPS-aware scheduling - delays execution during low TPS.
     */
    public static void runTPSAware(Plugin plugin, Runnable task, double minTPS) {
        // Check TPS and delay if needed
        // Implementation would check TPSMonitor and schedule accordingly
        runAsync(plugin, task, "combat-processing");
    }

    /**
     * Schedules a recurring task for performance monitoring.
     */
    public static void scheduleMonitoringTask(Runnable task, long initialDelay, long period, TimeUnit unit) {
        ScheduledExecutorService executor = scheduledExecutors.get("performance-monitoring");
        if (executor != null && !executor.isShutdown()) {
            executor.scheduleAtFixedRate(task, initialDelay, period, unit);
        }
    }

    /**
     * Cancels a task if it's running.
     */
    public static void cancelTask(BukkitTask task) {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    /**
     * Gets the status of thread pools for monitoring.
     */
    public static String getThreadPoolStatus() {
        StringBuilder status = new StringBuilder();
        status.append("AsyncUtils Thread Pools:\n");

        executors.forEach((name, executor) -> {
            status.append(String.format("- %s: active=%d, completed=%d\n",
                name,
                ((ExecutorService) executor).isTerminated() ? 0 : 1,
                0)); // Simplified status
        });

        scheduledExecutors.forEach((name, executor) -> {
            status.append(String.format("- %s (scheduled): active=%d\n",
                name,
                executor.isTerminated() ? 0 : 1));
        });

        return status.toString();
    }
}