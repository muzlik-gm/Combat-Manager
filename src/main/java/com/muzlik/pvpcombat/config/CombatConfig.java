package com.muzlik.pvpcombat.config;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Combat configuration settings.
 * Handles combat timer, damage settings, bossbar, and actionbar configurations.
 */
public class CombatConfig extends SubConfig {

    // Combat settings
    private boolean enabled;
    private int duration;
    private int cooldown;
    private int maxSessions;

    // Damage settings
    private boolean resetOnDamage;
    private double minDamageTrigger;

    // Bossbar settings
    private boolean bossbarEnabled;
    private int bossbarUpdateInterval;
    private String bossbarTitle;
    private String bossbarColor;
    private String bossbarStyle;

    // Actionbar settings
    private boolean actionbarEnabled;
    private boolean actionbarShowOpponent;
    private int actionbarUpdateInterval;

    /**
     * Creates a new combat configuration instance.
     *
     * @param validator The configuration validator
     * @param config The configuration section
     */
    public CombatConfig(ConfigurationValidator validator, ConfigurationSection config) {
        super(validator, config, "combat");
    }

    @Override
    public void load() {
        enabled = getBoolean("enabled", true);
        duration = getInt("duration", 30);
        cooldown = getInt("cooldown", 10);
        maxSessions = getInt("max-sessions", 100);

        // Damage settings
        ConfigurationSection damageSection = getSection("damage");
        if (damageSection != null) {
            resetOnDamage = damageSection.getBoolean("reset-on-damage", true);
            minDamageTrigger = damageSection.getDouble("min-damage-trigger", 0.5);
        } else {
            resetOnDamage = true;
            minDamageTrigger = 0.5;
        }

        // Bossbar settings
        ConfigurationSection bossbarSection = getSection("bossbar");
        if (bossbarSection != null) {
            bossbarEnabled = bossbarSection.getBoolean("enabled", true);
            bossbarUpdateInterval = bossbarSection.getInt("update-interval", 1);
            bossbarTitle = bossbarSection.getString("title", "&cCombat: &f{time_left}s");
            bossbarColor = bossbarSection.getString("color", "RED");
            bossbarStyle = bossbarSection.getString("style", "SOLID");
        } else {
            bossbarEnabled = true;
            bossbarUpdateInterval = 1;
            bossbarTitle = "&cCombat: &f{time_left}s";
            bossbarColor = "RED";
            bossbarStyle = "SOLID";
        }

        // Actionbar settings
        ConfigurationSection actionbarSection = getSection("actionbar");
        if (actionbarSection != null) {
            actionbarEnabled = actionbarSection.getBoolean("enabled", true);
            actionbarShowOpponent = actionbarSection.getBoolean("show-opponent", true);
            actionbarUpdateInterval = actionbarSection.getInt("update-interval", 20);
        } else {
            actionbarEnabled = true;
            actionbarShowOpponent = true;
            actionbarUpdateInterval = 20;
        }
    }

    @Override
    public void reload() {
        load();
    }

    @Override
    public ConfigurationValidator.ValidationResult validate() {
        ConfigurationValidator.ValidationResult result = new ConfigurationValidator.ValidationResult();

        if (duration < 5) {
            result.addWarning("combat.duration", "Combat duration too short: " + duration + "s, recommended minimum: 5s");
        }
        if (duration > 300) {
            result.addWarning("combat.duration", "Combat duration too long: " + duration + "s, recommended maximum: 300s");
        }
        if (cooldown < 0) {
            result.addError("combat.cooldown", "Cooldown cannot be negative: " + cooldown);
        }
        if (maxSessions < 0) {
            result.addError("combat.max-sessions", "Max sessions cannot be negative: " + maxSessions);
        }
        if (minDamageTrigger < 0) {
            result.addError("combat.damage.min-damage-trigger", "Min damage trigger cannot be negative: " + minDamageTrigger);
        }

        return result;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // Getters
    public int getDuration() {
        return duration;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getMaxSessions() {
        return maxSessions;
    }

    public boolean isResetOnDamage() {
        return resetOnDamage;
    }

    public double getMinDamageTrigger() {
        return minDamageTrigger;
    }

    public boolean isBossbarEnabled() {
        return bossbarEnabled;
    }

    public int getBossbarUpdateInterval() {
        return bossbarUpdateInterval;
    }

    public String getBossbarTitle() {
        return bossbarTitle;
    }

    public String getBossbarColor() {
        return bossbarColor;
    }

    public String getBossbarStyle() {
        return bossbarStyle;
    }

    public boolean isActionbarEnabled() {
        return actionbarEnabled;
    }

    public boolean isActionbarShowOpponent() {
        return actionbarShowOpponent;
    }

    public int getActionbarUpdateInterval() {
        return actionbarUpdateInterval;
    }

    @Override
    public int getLoadPriority() {
        return 1; // Load early as other systems depend on combat settings
    }
}