package com.muzlik.pvpcombat.config;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Performance configuration settings.
 * Handles lag-aware adjustments, async tasks, caching, and TPS monitoring.
 */
public class PerformanceConfig extends SubConfig {

    // Lag-aware settings
    private boolean lagEnabled;
    private double lagTpsThreshold;
    private int lagPingThreshold;
    private int lagBaseExtensionSeconds;
    private double lagExtensionMultiplier;
    private int lagPingUpdateIntervalMs;
    private int lagPingCleanupThresholdMs;
    private int lagTpsHistoryLength;
    private int lagCleanupIntervalTicks;

    // Async settings
    private boolean asyncEnabled;
    private int asyncThreadPoolSize;

    // Caching settings
    private int cachingPlayerDataTtl;
    private int cachingSessionCleanupInterval;

    // Buffer settings
    private int maxEventBuffer;

    /**
     * Creates a new performance configuration instance.
     *
     * @param validator The configuration validator
     * @param config The configuration section
     */
    public PerformanceConfig(ConfigurationValidator validator, ConfigurationSection config) {
        super(validator, config, "performance");
    }

    @Override
    public void load() {
        // Lag-aware settings
        ConfigurationSection lagSection = getSection("lag");
        if (lagSection != null) {
            lagEnabled = lagSection.getBoolean("enabled", true);
            lagTpsThreshold = lagSection.getDouble("tps-threshold", 18.0);
            lagPingThreshold = lagSection.getInt("ping-threshold", 200);
            lagBaseExtensionSeconds = lagSection.getInt("base-extension-seconds", 5);
            lagExtensionMultiplier = lagSection.getDouble("extension-multiplier", 1.5);
            lagPingUpdateIntervalMs = lagSection.getInt("ping-update-interval-ms", 1000);
            lagPingCleanupThresholdMs = lagSection.getInt("ping-cleanup-threshold-ms", 300000);
            lagTpsHistoryLength = lagSection.getInt("tps-history-length", 60);
            lagCleanupIntervalTicks = lagSection.getInt("cleanup-interval-ticks", 1200);
        } else {
            lagEnabled = true;
            lagTpsThreshold = 18.0;
            lagPingThreshold = 200;
            lagBaseExtensionSeconds = 5;
            lagExtensionMultiplier = 1.5;
            lagPingUpdateIntervalMs = 1000;
            lagPingCleanupThresholdMs = 300000;
            lagTpsHistoryLength = 60;
            lagCleanupIntervalTicks = 1200;
        }

        // Async settings
        ConfigurationSection asyncSection = getSection("async");
        if (asyncSection != null) {
            asyncEnabled = asyncSection.getBoolean("enabled", true);
            asyncThreadPoolSize = asyncSection.getInt("thread-pool-size", 4);
        } else {
            asyncEnabled = true;
            asyncThreadPoolSize = 4;
        }

        // Caching settings
        ConfigurationSection cacheSection = getSection("cache");
        if (cacheSection != null) {
            cachingPlayerDataTtl = cacheSection.getInt("player-data-ttl", 30);
            cachingSessionCleanupInterval = cacheSection.getInt("cleanup-interval", 60);
        } else {
            cachingPlayerDataTtl = 30;
            cachingSessionCleanupInterval = 60;
        }

        // Buffer settings (from main performance section)
        maxEventBuffer = getInt("max-event-buffer", 1000);
    }

    @Override
    public void reload() {
        load();
    }

    @Override
    public ConfigurationValidator.ValidationResult validate() {
        ConfigurationValidator.ValidationResult result = new ConfigurationValidator.ValidationResult();

        if (lagTpsThreshold < 0 || lagTpsThreshold > 20) {
            result.addError("performance.lag.tps-threshold", "TPS threshold must be between 0 and 20: " + lagTpsThreshold);
        }
        if (lagPingThreshold < 0) {
            result.addError("performance.lag.ping-threshold", "Ping threshold cannot be negative: " + lagPingThreshold);
        }
        if (lagBaseExtensionSeconds < 0) {
            result.addError("performance.lag.base-extension-seconds", "Base extension seconds cannot be negative: " + lagBaseExtensionSeconds);
        }
        if (lagExtensionMultiplier <= 0) {
            result.addError("performance.lag.extension-multiplier", "Extension multiplier must be positive: " + lagExtensionMultiplier);
        }
        if (lagPingUpdateIntervalMs < 100) {
            result.addWarning("performance.lag.ping-update-interval-ms", "Ping update interval too low: " + lagPingUpdateIntervalMs + "ms, recommended minimum: 100ms");
        }
        if (lagTpsHistoryLength < 10) {
            result.addWarning("performance.lag.tps-history-length", "TPS history length too small: " + lagTpsHistoryLength + ", recommended minimum: 10");
        }

        if (asyncThreadPoolSize < 1) {
            result.addError("performance.async.thread-pool-size", "Thread pool size must be at least 1: " + asyncThreadPoolSize);
        }
        if (asyncThreadPoolSize > 32) {
            result.addWarning("performance.async.thread-pool-size", "Thread pool size very large: " + asyncThreadPoolSize + ", recommended maximum: 32");
        }

        if (cachingPlayerDataTtl < 0) {
            result.addError("performance.cache.player-data-ttl", "Player data TTL cannot be negative: " + cachingPlayerDataTtl);
        }
        if (cachingSessionCleanupInterval < 10) {
            result.addWarning("performance.cache.cleanup-interval", "Cleanup interval too small: " + cachingSessionCleanupInterval + "s, recommended minimum: 10s");
        }

        if (maxEventBuffer < 100) {
            result.addWarning("performance.max-event-buffer", "Max event buffer too small: " + maxEventBuffer + ", recommended minimum: 100");
        }

        return result;
    }

    @Override
    public boolean isEnabled() {
        return lagEnabled || asyncEnabled;
    }

    // Getters for lag settings
    public boolean isLagEnabled() {
        return lagEnabled;
    }

    public double getLagTpsThreshold() {
        return lagTpsThreshold;
    }

    public int getLagPingThreshold() {
        return lagPingThreshold;
    }

    public int getLagBaseExtensionSeconds() {
        return lagBaseExtensionSeconds;
    }

    public double getLagExtensionMultiplier() {
        return lagExtensionMultiplier;
    }

    public int getLagPingUpdateIntervalMs() {
        return lagPingUpdateIntervalMs;
    }

    public int getLagPingCleanupThresholdMs() {
        return lagPingCleanupThresholdMs;
    }

    public int getLagTpsHistoryLength() {
        return lagTpsHistoryLength;
    }

    public int getLagCleanupIntervalTicks() {
        return lagCleanupIntervalTicks;
    }

    // Getters for async settings
    public boolean isAsyncEnabled() {
        return asyncEnabled;
    }

    public int getAsyncThreadPoolSize() {
        return asyncThreadPoolSize;
    }

    // Getters for caching settings
    public int getCachingPlayerDataTtl() {
        return cachingPlayerDataTtl;
    }

    public int getCachingSessionCleanupInterval() {
        return cachingSessionCleanupInterval;
    }

    // Getters for buffer settings
    public int getMaxEventBuffer() {
        return maxEventBuffer;
    }

    @Override
    public int getLoadPriority() {
        return 4; // Load after basic configs
    }
}