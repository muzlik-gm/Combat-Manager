package com.muzlik.pvpcombat.config;

import org.bukkit.configuration.ConfigurationSection;
import java.util.List;

/**
 * Replay configuration settings.
 * Handles combat replay system, storage formats, timeline settings, and access control.
 */
public class ReplayConfig extends SubConfig {

    // General replay settings
    private boolean replayEnabled;
    private String replayStorageFormat;
    private int replayTimelineCapacity;
    private int replayTimelineMaxAgeSeconds;
    private int replayCacheMaxAgeMinutes;
    private boolean replayAccessAdminOnly;
    private List<String> replayAllowedAdmins;

    // GUI settings
    private boolean replayGuiEnabled;
    private int replayGuiPageSize;
    private boolean replayGuiAutoplayEnabled;
    private double replayGuiAutoplaySpeed;
    private int replayGuiAutoplayInterval;

    /**
     * Creates a new replay configuration instance.
     *
     * @param validator The configuration validator
     * @param config The configuration section
     */
    public ReplayConfig(ConfigurationValidator validator, ConfigurationSection config) {
        super(validator, config, "replay");
    }

    @Override
    public void load() {
        replayEnabled = getBoolean("enabled", true);
        replayStorageFormat = getString("storage.format", "HYBRID");
        replayTimelineCapacity = getInt("timeline.capacity", 1000);
        replayTimelineMaxAgeSeconds = getInt("timeline.max_age_seconds", 600);
        replayCacheMaxAgeMinutes = getInt("cache.max_age_minutes", 30);
        replayAccessAdminOnly = getBoolean("access.admin_only", true);
        replayAllowedAdmins = getSection("access") != null ?
            getSection("access").getStringList("allowed_admins") : List.of();

        // GUI settings
        ConfigurationSection guiSection = getSection("gui");
        if (guiSection != null) {
            replayGuiEnabled = guiSection.getBoolean("enabled", true);
            replayGuiPageSize = guiSection.getInt("page_size", 50);

            ConfigurationSection autoplaySection = guiSection.getConfigurationSection("autoplay");
            if (autoplaySection != null) {
                replayGuiAutoplayEnabled = autoplaySection.getBoolean("enabled", false);
                replayGuiAutoplaySpeed = autoplaySection.getDouble("speed", 1.0);
                replayGuiAutoplayInterval = autoplaySection.getInt("interval", 4);
            } else {
                replayGuiAutoplayEnabled = false;
                replayGuiAutoplaySpeed = 1.0;
                replayGuiAutoplayInterval = 4;
            }
        } else {
            replayGuiEnabled = true;
            replayGuiPageSize = 50;
            replayGuiAutoplayEnabled = false;
            replayGuiAutoplaySpeed = 1.0;
            replayGuiAutoplayInterval = 4;
        }
    }

    @Override
    public void reload() {
        load();
    }

    @Override
    public ConfigurationValidator.ValidationResult validate() {
        ConfigurationValidator.ValidationResult result = new ConfigurationValidator.ValidationResult();

        if (!replayStorageFormat.equals("MEMORY") &&
            !replayStorageFormat.equals("COMPRESSED_FILE") &&
            !replayStorageFormat.equals("HYBRID")) {
            result.addError("replay.storage.format", "Invalid storage format: " + replayStorageFormat +
                          ". Must be MEMORY, COMPRESSED_FILE, or HYBRID");
        }

        if (replayTimelineCapacity < 100) {
            result.addWarning("replay.timeline.capacity", "Timeline capacity too small: " +
                            replayTimelineCapacity + ", recommended minimum: 100");
        }

        if (replayTimelineMaxAgeSeconds < 60) {
            result.addWarning("replay.timeline.max_age_seconds", "Timeline max age too short: " +
                            replayTimelineMaxAgeSeconds + "s, recommended minimum: 60s");
        }

        if (replayCacheMaxAgeMinutes < 5) {
            result.addWarning("replay.cache.max_age_minutes", "Cache max age too short: " +
                            replayCacheMaxAgeMinutes + "min, recommended minimum: 5min");
        }

        if (replayGuiPageSize < 10) {
            result.addWarning("replay.gui.page_size", "GUI page size too small: " +
                            replayGuiPageSize + ", recommended minimum: 10");
        }

        if (replayGuiAutoplaySpeed <= 0) {
            result.addError("replay.gui.autoplay.speed", "Autoplay speed must be positive: " + replayGuiAutoplaySpeed);
        }

        if (replayGuiAutoplayInterval < 1) {
            result.addError("replay.gui.autoplay.interval", "Autoplay interval must be positive: " + replayGuiAutoplayInterval);
        }

        return result;
    }

    @Override
    public boolean isEnabled() {
        return replayEnabled;
    }

    // Getters for general replay settings
    public boolean isReplayEnabled() {
        return replayEnabled;
    }

    public String getReplayStorageFormat() {
        return replayStorageFormat;
    }

    public int getReplayTimelineCapacity() {
        return replayTimelineCapacity;
    }

    public int getReplayTimelineMaxAgeSeconds() {
        return replayTimelineMaxAgeSeconds;
    }

    public int getReplayCacheMaxAgeMinutes() {
        return replayCacheMaxAgeMinutes;
    }

    public boolean isReplayAccessAdminOnly() {
        return replayAccessAdminOnly;
    }

    public List<String> getReplayAllowedAdmins() {
        return replayAllowedAdmins;
    }

    // Getters for GUI settings
    public boolean isReplayGuiEnabled() {
        return replayGuiEnabled;
    }

    public int getReplayGuiPageSize() {
        return replayGuiPageSize;
    }

    public boolean isReplayGuiAutoplayEnabled() {
        return replayGuiAutoplayEnabled;
    }

    public double getReplayGuiAutoplaySpeed() {
        return replayGuiAutoplaySpeed;
    }

    public int getReplayGuiAutoplayInterval() {
        return replayGuiAutoplayInterval;
    }

    /**
     * Checks if a player has admin access to replays.
     *
     * @param playerName The player name
     * @param uuid The player UUID
     * @return true if access is granted, false otherwise
     */
    public boolean hasReplayAccess(String playerName, String uuid) {
        if (!replayAccessAdminOnly) {
            return true;
        }
        return replayAllowedAdmins.contains(playerName) || replayAllowedAdmins.contains(uuid);
    }

    @Override
    public int getLoadPriority() {
        return 8; // Load last as it depends on other systems
    }
}