package com.muzlik.pvpcombat.config;

import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Validates configuration values and provides defaults.
 * Handles type checking, range validation, and fallback values.
 */
public class ConfigurationValidator {

    private final Map<String, ValidationRule> validationRules;

    public ConfigurationValidator() {
        this.validationRules = new HashMap<>();
        initializeDefaultRules();
    }

    /**
     * Initializes default validation rules for common configuration paths.
     */
    private void initializeDefaultRules() {
        // General settings
        addRule("general.enabled", Boolean.class, true, val -> val instanceof Boolean);
        addRule("general.debug-mode", Boolean.class, false, val -> val instanceof Boolean);
        addRule("general.language", String.class, "en", val -> val instanceof String && !((String) val).isEmpty());

        // Combat settings
        addRule("combat.duration", Integer.class, 30, val -> val instanceof Integer && (Integer) val > 0 && (Integer) val <= 300);
        addRule("combat.cooldown", Integer.class, 10, val -> val instanceof Integer && (Integer) val >= 0 && (Integer) val <= 60);
        addRule("combat.max-sessions", Integer.class, 100, val -> val instanceof Integer && (Integer) val >= 0);

        // Performance settings
        addRule("performance.lag.enabled", Boolean.class, true, val -> val instanceof Boolean);
        addRule("performance.lag.tps-threshold", Double.class, 18.0, val -> val instanceof Double && (Double) val > 0 && (Double) val <= 20.0);
        addRule("performance.lag.ping-threshold", Integer.class, 200, val -> val instanceof Integer && (Integer) val > 0 && (Integer) val <= 1000);
        addRule("performance.thread-pool-size", Integer.class, 4, val -> val instanceof Integer && (Integer) val > 0 && (Integer) val <= 16);

        // Integration settings
        addRule("integration.cross-server-sync.enabled", Boolean.class, false, val -> val instanceof Boolean);
        addRule("integration.cross-server-sync.sync-interval", Integer.class, 30, val -> val instanceof Integer && (Integer) val > 0 && (Integer) val <= 300);

        // Logging settings
        addRule("logging.enabled", Boolean.class, true, val -> val instanceof Boolean);

        // Visual settings
        addRule("visual.themes.allow-custom", Boolean.class, true, val -> val instanceof Boolean);
        addRule("visual.animations.enabled", Boolean.class, true, val -> val instanceof Boolean);
        addRule("visual.bossbar.enabled", Boolean.class, true, val -> val instanceof Boolean);
        addRule("visual.actionbar.enabled", Boolean.class, true, val -> val instanceof Boolean);
        addRule("visual.sounds.enabled", Boolean.class, true, val -> val instanceof Boolean);

        // Restrictions
        addRule("restrictions.enderpearl.enabled", Boolean.class, true, val -> val instanceof Boolean);
        addRule("restrictions.elytra.enabled", Boolean.class, true, val -> val instanceof Boolean);
        addRule("restrictions.teleport.enabled", Boolean.class, true, val -> val instanceof Boolean);

        // Anti-cheat
        addRule("anticheat.interference.enabled", Boolean.class, true, val -> val instanceof Boolean);
        addRule("anticheat.interference.max-interference-percentage", Double.class, 10.0, val -> val instanceof Double && (Double) val >= 0 && (Double) val <= 100);

        // Replay
        addRule("replay.enabled", Boolean.class, true, val -> val instanceof Boolean);
        addRule("replay.timeline.capacity", Integer.class, 1000, val -> val instanceof Integer && (Integer) val > 0 && (Integer) val <= 10000);
    }

    /**
     * Adds a validation rule for a configuration path.
     */
    public void addRule(String path, Class<?> expectedType, Object defaultValue, Function<Object, Boolean> validator) {
        validationRules.put(path, new ValidationRule(expectedType, defaultValue, validator));
    }

    /**
     * Validates a configuration value and returns the validated value or default.
     */
    public <T> T validateValue(String path, Object value, Class<T> type) {
        ValidationRule rule = validationRules.get(path);
        if (rule == null) {
            // No validation rule, return cast value or null
            try {
                return type.cast(value);
            } catch (ClassCastException e) {
                return null;
            }
        }

        // Check type
        if (value != null && !rule.expectedType.isInstance(value)) {
            return type.cast(rule.defaultValue);
        }

        // Validate value
        if (value == null || !rule.validator.apply(value)) {
            return type.cast(rule.defaultValue);
        }

        return type.cast(value);
    }

    /**
     * Validates an entire configuration section.
     */
    public ValidationResult validateConfiguration(FileConfiguration config) {
        ValidationResult result = new ValidationResult();

        for (String path : validationRules.keySet()) {
            Object value = config.get(path);
            ValidationRule rule = validationRules.get(path);

            if (value == null) {
                result.addWarning(path, "Missing configuration value, using default: " + rule.defaultValue);
            } else if (!rule.expectedType.isInstance(value)) {
                result.addWarning(path, "Invalid type, expected " + rule.expectedType.getSimpleName() +
                                 ", got " + value.getClass().getSimpleName() + ", using default: " + rule.defaultValue);
            } else if (!rule.validator.apply(value)) {
                result.addWarning(path, "Invalid value: " + value + ", using default: " + rule.defaultValue);
            }
        }

        return result;
    }

    /**
     * Gets the default value for a path.
     */
    public Object getDefaultValue(String path) {
        ValidationRule rule = validationRules.get(path);
        return rule != null ? rule.defaultValue : null;
    }

    /**
     * Validation rule data class.
     */
    private static class ValidationRule {
        final Class<?> expectedType;
        final Object defaultValue;
        final Function<Object, Boolean> validator;

        ValidationRule(Class<?> expectedType, Object defaultValue, Function<Object, Boolean> validator) {
            this.expectedType = expectedType;
            this.defaultValue = defaultValue;
            this.validator = validator;
        }
    }

    /**
     * Result of configuration validation.
     */
    public static class ValidationResult {
        private final java.util.List<String> warnings = new java.util.ArrayList<>();
        private final java.util.List<String> errors = new java.util.ArrayList<>();

        public void addWarning(String path, String message) {
            warnings.add("[" + path + "] " + message);
        }

        public void addError(String path, String message) {
            errors.add("[" + path + "] " + message);
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }

        public java.util.List<String> getWarnings() {
            return new java.util.ArrayList<>(warnings);
        }

        public java.util.List<String> getErrors() {
            return new java.util.ArrayList<>(errors);
        }

        public java.util.List<String> getAllMessages() {
            java.util.List<String> all = new java.util.ArrayList<>(errors);
            all.addAll(warnings);
            return all;
        }
    }
}