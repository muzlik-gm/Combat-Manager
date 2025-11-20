package com.muzlik.pvpcombat.config;

import org.bukkit.configuration.ConfigurationSection;
import java.util.List;

/**
 * Logging configuration settings.
 * Handles combat logging, storage types, retention policies, and summary delivery.
 */
public class LoggingConfig extends SubConfig {

    // General logging settings
    private boolean loggingEnabled;
    private String loggingLevel;
    private int loggingMaxFiles;
    private int loggingMaxSizeMb;

    // Combat logging settings
    private boolean combatLoggingDetailedEnabled;
    private String combatLoggingStorageType;
    private String combatLoggingSummaryDelivery;
    private int combatLoggingRetentionDays;
    private int combatLoggingMemoryMaxEntries;
    private List<String> combatLoggingIncludeStats;

    /**
     * Creates a new logging configuration instance.
     *
     * @param validator The configuration validator
     * @param config The configuration section
     */
    public LoggingConfig(ConfigurationValidator validator, ConfigurationSection config) {
        super(validator, config, "logging");
    }

    @Override
    public void load() {
        loggingEnabled = getBoolean("enabled", true);
        loggingLevel = getString("level", "INFO");
        loggingMaxFiles = getInt("max-files", 5);
        loggingMaxSizeMb = getInt("max-size-mb", 10);

        // Combat logging settings
        ConfigurationSection combatSection = getSection("combat");
        if (combatSection != null) {
            ConfigurationSection detailedSection = combatSection.getConfigurationSection("detailed");
            if (detailedSection != null) {
                combatLoggingDetailedEnabled = detailedSection.getBoolean("enabled", true);
            } else {
                combatLoggingDetailedEnabled = true;
            }

            ConfigurationSection storageSection = combatSection.getConfigurationSection("storage");
            if (storageSection != null) {
                combatLoggingStorageType = storageSection.getString("type", "BOTH");
            } else {
                combatLoggingStorageType = "BOTH";
            }

            ConfigurationSection summarySection = combatSection.getConfigurationSection("summary");
            if (summarySection != null) {
                combatLoggingSummaryDelivery = summarySection.getString("delivery", "CHAT");
            } else {
                combatLoggingSummaryDelivery = "CHAT";
            }

            ConfigurationSection retentionSection = combatSection.getConfigurationSection("retention");
            if (retentionSection != null) {
                combatLoggingRetentionDays = retentionSection.getInt("days", 30);
            } else {
                combatLoggingRetentionDays = 30;
            }

            ConfigurationSection memorySection = combatSection.getConfigurationSection("memory");
            if (memorySection != null) {
                combatLoggingMemoryMaxEntries = memorySection.getInt("max-entries", 10000);
            } else {
                combatLoggingMemoryMaxEntries = 10000;
            }

            ConfigurationSection statsSection = combatSection.getConfigurationSection("include-stats");
            if (statsSection != null) {
                combatLoggingIncludeStats = statsSection.getStringList("stats");
            } else {
                combatLoggingIncludeStats = List.of(
                    "hits_landed", "damage_dealt", "accuracy", "knockback_exchanges", "combat_duration"
                );
            }
        } else {
            combatLoggingDetailedEnabled = true;
            combatLoggingStorageType = "BOTH";
            combatLoggingSummaryDelivery = "CHAT";
            combatLoggingRetentionDays = 30;
            combatLoggingMemoryMaxEntries = 10000;
            combatLoggingIncludeStats = List.of(
                "hits_landed", "damage_dealt", "accuracy", "knockback_exchanges", "combat_duration"
            );
        }
    }

    @Override
    public void reload() {
        load();
    }

    @Override
    public ConfigurationValidator.ValidationResult validate() {
        ConfigurationValidator.ValidationResult result = new ConfigurationValidator.ValidationResult();

        if (!loggingLevel.equals("INFO") && !loggingLevel.equals("DEBUG") &&
            !loggingLevel.equals("WARNING") && !loggingLevel.equals("ERROR")) {
            result.addWarning("logging.level", "Invalid log level: " + loggingLevel +
                            ". Valid levels: INFO, DEBUG, WARNING, ERROR");
        }

        if (loggingMaxFiles < 1) {
            result.addError("logging.max-files", "Max files must be positive: " + loggingMaxFiles);
        }

        if (loggingMaxSizeMb < 1) {
            result.addError("logging.max-size-mb", "Max size must be positive: " + loggingMaxSizeMb);
        }

        if (!combatLoggingStorageType.equals("FILE") &&
            !combatLoggingStorageType.equals("MEMORY") &&
            !combatLoggingStorageType.equals("BOTH")) {
            result.addError("logging.combat.storage.type", "Invalid storage type: " + combatLoggingStorageType +
                          ". Must be FILE, MEMORY, or BOTH");
        }

        if (!combatLoggingSummaryDelivery.equals("CHAT") &&
            !combatLoggingSummaryDelivery.equals("GUI") &&
            !combatLoggingSummaryDelivery.equals("STORAGE") &&
            !combatLoggingSummaryDelivery.equals("NONE")) {
            result.addWarning("logging.combat.summary.delivery", "Invalid summary delivery: " + combatLoggingSummaryDelivery +
                            ". Valid options: CHAT, GUI, STORAGE, NONE");
        }

        if (combatLoggingRetentionDays < 1) {
            result.addWarning("logging.combat.retention.days", "Retention days too low: " + combatLoggingRetentionDays +
                            ", recommended minimum: 1 day");
        }

        if (combatLoggingMemoryMaxEntries < 100) {
            result.addWarning("logging.combat.memory.max-entries", "Max memory entries too low: " +
                            combatLoggingMemoryMaxEntries + ", recommended minimum: 100");
        }

        return result;
    }

    @Override
    public boolean isEnabled() {
        return loggingEnabled;
    }

    // Getters for general logging settings
    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public int getLoggingMaxFiles() {
        return loggingMaxFiles;
    }

    public int getLoggingMaxSizeMb() {
        return loggingMaxSizeMb;
    }

    // Getters for combat logging settings
    public boolean isCombatLoggingDetailedEnabled() {
        return combatLoggingDetailedEnabled;
    }

    public String getCombatLoggingStorageType() {
        return combatLoggingStorageType;
    }

    public String getCombatLoggingSummaryDelivery() {
        return combatLoggingSummaryDelivery;
    }

    public int getCombatLoggingRetentionDays() {
        return combatLoggingRetentionDays;
    }

    public int getCombatLoggingMemoryMaxEntries() {
        return combatLoggingMemoryMaxEntries;
    }

    public List<String> getCombatLoggingIncludeStats() {
        return combatLoggingIncludeStats;
    }

    @Override
    public int getLoadPriority() {
        return 6; // Load after other configs
    }
}