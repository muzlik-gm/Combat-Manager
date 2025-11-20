package com.muzlik.pvpcombat.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.muzlik.pvpcombat.core.PvPCombatPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.concurrent.Callable;

/**
 * Centralized caching manager with performance monitoring and automatic cleanup.
 */
public class CacheManager {

    private final PvPCombatPlugin plugin;
    private static final Map<String, Object> caches = new ConcurrentHashMap<>();
    private volatile boolean enabled = true;

    public CacheManager(PvPCombatPlugin plugin) {
        this.plugin = plugin;

        // Initialize default caches
        initializeCaches();

        // Schedule periodic cleanup
        AsyncUtils.scheduleMonitoringTask(this::performCleanup,
            5, 5, TimeUnit.MINUTES);
    }

    /**
     * Initializes default caches for different data types.
     */
    private void initializeCaches() {
        // Combat state cache - short lived for active sessions
        createCache("combat-states", 1000, 10, TimeUnit.MINUTES);

        // Player statistics cache - longer lived
        createCache("player-stats", 5000, 30, TimeUnit.MINUTES);

        // Restriction data cache - medium lived
        createCache("restriction-data", 2000, 15, TimeUnit.MINUTES);

        // Visual preferences cache - long lived
        createCache("visual-prefs", 10000, 2, TimeUnit.HOURS);

        // Performance metrics cache - short lived
        createCache("performance-metrics", 100, 1, TimeUnit.MINUTES);
    }

    /**
     * Creates a new cache with specified parameters.
     */
    public <K, V> Cache<K, V> createCache(String name, int maxSize, long duration, TimeUnit unit) {
        Cache<K, V> cache = Caffeine.<K,V>newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(duration, unit)
                .recordStats()
                .removalListener((key, value, cause) -> {
                    // Optional: Log cache evictions for monitoring
                    if (plugin.getConfig().getBoolean("performance.cache-debug", false)) {
                        plugin.getLogger().fine(String.format("Cache %s evicted: %s -> %s (%s)",
                                name, key, value, cause));
                    }
                })
                .build();

        caches.put(name, cache);
        return cache;
    }

    /**
     * Gets or creates a cache by name.
     */
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getCache(String name) {
        return (Cache<K, V>) caches.get(name);
    }

    /**
     * Puts a value in the specified cache.
     */
    public void put(String cacheName, String key, Object value) {
        if (!enabled) return;

        @SuppressWarnings("unchecked")
        Cache<String, Object> cache = (Cache<String, Object>) caches.get(cacheName);
        if (cache != null) {
            cache.put(key, value);
        }
    }

    /**
     * Gets a value from the specified cache.
     */
    public Object get(String cacheName, String key) {
        if (!enabled) return null;

        @SuppressWarnings("unchecked")
        Cache<String, Object> cache = (Cache<String, Object>) caches.get(cacheName);
        return cache != null ? cache.getIfPresent(key) : null;
    }

    /**
     * Gets a value with a loader function (compute if absent).
     */
    public <T> T get(String cacheName, String key, Function<String, T> loader) {
        if (!enabled) return loader.apply(key);

        @SuppressWarnings("unchecked")
        Cache<String, Object> cache = (Cache<String, Object>) caches.get(cacheName);
        if (cache != null) {
            try {
                return (T) cache.get(key, k -> loader.apply((String) k));
            } catch (Exception e) {
                plugin.getLogger().warning("Cache loader failed for " + cacheName + ": " + key);
                return loader.apply(key);
            }
        }
        return loader.apply(key);
    }

    /**
     * Removes a key from the specified cache.
     */
    public void remove(String cacheName, String key) {
        @SuppressWarnings("unchecked")
        Cache<String, Object> cache = (Cache<String, Object>) caches.get(cacheName);
        if (cache != null) {
            cache.invalidate(key);
        }
    }

    /**
     * Clears all entries from the specified cache.
     */
    public void clear(String cacheName) {
        @SuppressWarnings("unchecked")
        Cache<String, Object> cache = (Cache<String, Object>) caches.get(cacheName);
        if (cache != null) {
            cache.invalidateAll();
        }
    }

    /**
     * Clears all caches.
     */
    public void clearAll() {
        caches.values().forEach(c -> ((Cache<?,?>) c).invalidateAll());
    }

    /**
     * Gets cache statistics for monitoring.
     */
    public String getCacheStats(String cacheName) {
        @SuppressWarnings("unchecked")
        Cache<String, Object> cache = (Cache<String, Object>) caches.get(cacheName);
        if (cache == null) return "Cache not found: " + cacheName;

        CacheStats stats = cache.stats();
        return String.format("Cache %s: size=%d, hitRate=%.2f%%, evictions=%d, loads=%d",
                cacheName,
                cache.estimatedSize(),
                stats.hitRate() * 100,
                stats.evictionCount(),
                stats.loadCount());
    }

    /**
     * Gets statistics for all caches.
     */
    public String getAllCacheStats() {
        StringBuilder stats = new StringBuilder("CacheManager Statistics:\n");
        caches.forEach((name, cache) -> {
            stats.append("- ").append(getCacheStats(name)).append("\n");
        });
        return stats.toString();
    }

    /**
     * Performs periodic cleanup of expired entries.
     */
    private void performCleanup() {
        if (!enabled) return;

        long startTime = System.nanoTime();
        caches.values().forEach(c -> ((Cache<?,?>) c).cleanUp());

        long duration = (System.nanoTime() - startTime) / 1_000_000; // Convert to milliseconds

        if (plugin.getConfig().getBoolean("performance.cache-debug", false)) {
            plugin.getLogger().info(String.format("Cache cleanup completed in %dms", duration));
        }
    }

    /**
     * Enables or disables caching.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            clearAll();
        }
    }

    /**
     * Checks if caching is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Gets the total number of cached items across all caches.
     */
    public long getTotalCacheSize() {
        return caches.values().stream().mapToLong(c -> ((Cache<?,?>) c).estimatedSize()).sum();
    }
}