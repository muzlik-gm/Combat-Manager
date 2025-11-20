package com.muzlik.pvpcombat.config;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Base class for modular sub-configurations.
 * Provides common functionality for loading, validating, and accessing configuration values.
 */
public abstract class SubConfig {

    protected final ConfigurationValidator validator;
    protected final ConfigurationSection config;
    protected final String sectionName;

    /**
     * Creates a new sub-config instance.
     *
     * @param validator The configuration validator
     * @param config The configuration section
     * @param sectionName The name of this config section
     */
    protected SubConfig(ConfigurationValidator validator, ConfigurationSection config, String sectionName) {
        this.validator = validator;
        this.config = config;
        this.sectionName = sectionName;
    }

    /**
     * Loads and validates the configuration values.
     * Called after construction to initialize the sub-config.
     */
    public abstract void load();

    /**
     * Reloads the configuration values.
     */
    public abstract void reload();

    /**
     * Validates the configuration section.
     *
     * @return Validation result
     */
    public abstract ConfigurationValidator.ValidationResult validate();

    /**
     * Gets a boolean value from the configuration.
     *
     * @param key The configuration key (relative to section)
     * @param defaultValue The default value
     * @return The boolean value
     */
    protected boolean getBoolean(String key, boolean defaultValue) {
        String fullPath = sectionName + "." + key;
        Boolean value = validator.validateValue(fullPath, config.get(fullPath), Boolean.class);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets an integer value from the configuration.
     *
     * @param key The configuration key (relative to section)
     * @param defaultValue The default value
     * @return The integer value
     */
    protected int getInt(String key, int defaultValue) {
        String fullPath = sectionName + "." + key;
        Integer value = validator.validateValue(fullPath, config.get(fullPath), Integer.class);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets a double value from the configuration.
     *
     * @param key The configuration key (relative to section)
     * @param defaultValue The default value
     * @return The double value
     */
    protected double getDouble(String key, double defaultValue) {
        String fullPath = sectionName + "." + key;
        Double value = validator.validateValue(fullPath, config.get(fullPath), Double.class);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets a string value from the configuration.
     *
     * @param key The configuration key (relative to section)
     * @param defaultValue The default value
     * @return The string value
     */
    protected String getString(String key, String defaultValue) {
        String fullPath = sectionName + "." + key;
        String value = validator.validateValue(fullPath, config.getString(fullPath), String.class);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets a configuration section.
     *
     * @param key The configuration key (relative to section)
     * @return The configuration section, or null if not found
     */
    protected ConfigurationSection getSection(String key) {
        return config.getConfigurationSection(key);
    }

    /**
     * Gets the section name.
     *
     * @return The section name
     */
    public String getSectionName() {
        return sectionName;
    }

    /**
     * Checks if this sub-config is enabled.
     *
     * @return true if enabled, false otherwise
     */
    public abstract boolean isEnabled();

    /**
     * Gets the priority order for loading (lower numbers load first).
     *
     * @return The load priority
     */
    public int getLoadPriority() {
        return 0;
    }
}