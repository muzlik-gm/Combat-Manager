package com.muzlik.pvpcombat.visual;

import com.muzlik.pvpcombat.interfaces.IConfigManager;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import java.util.logging.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages visual themes for bossbars, messages, and sounds.
 * Supports 6 built-in themes + custom themes with configurable colors, styles, and animations.
 */
public class ThemeManager {

    private final IConfigManager configManager;
    private final Logger logger;
    private final Map<String, Theme> themes;
    private Theme defaultTheme;

    public ThemeManager(IConfigManager configManager, Logger logger) {
        this.configManager = configManager;
        this.logger = logger;
        this.themes = new HashMap<>();
        this.defaultTheme = createDefaultTheme();
    }

    /**
     * Loads themes from configuration.
     */
    public void loadThemes() {
        themes.clear();

        // Load themes from config
        ConfigurationSection themeSection = configManager.getMainConfig().getConfigurationSection("visual.themes");
        if (themeSection != null) {
            ConfigurationSection customSection = themeSection.getConfigurationSection("custom");
            if (customSection != null) {
                for (String themeName : customSection.getKeys(false)) {
                    ConfigurationSection themeConfig = customSection.getConfigurationSection(themeName);
                    if (themeConfig != null) {
                        Theme theme = loadThemeFromConfig(themeName, themeConfig);
                        themes.put(themeName, theme);
                    }
                }
            }
        }

        // Always create built-in themes
        createBuiltInThemes();

        // Ensure default theme exists
        if (!themes.containsKey("default")) {
            themes.put("default", defaultTheme);
        }
    }

    /**
     * Gets a theme by name.
     */
    public Theme getTheme(String themeName) {
        return themes.get(themeName);
    }

    /**
     * Gets the default theme.
     */
    public Theme getDefaultTheme() {
        return themes.getOrDefault("default", defaultTheme);
    }

    /**
     * Creates the default theme.
     */
    private Theme createDefaultTheme() {
        return new Theme(
            "default",
            "&cCombat Timer: &f{time_left}s",
            BarColor.RED,
            BarStyle.SOLID,
            "&cCombat with &f{opponent} &c- &f{time_left}s",
            false,
            "minimal",
            "default"
        );
    }

    /**
     * Creates 6 built-in themes with enhanced features.
     */
    private void createBuiltInThemes() {
        // Minimal theme - Clean and simple
        themes.put("minimal", new Theme(
            "minimal",
            "&7{time_left}",
            BarColor.WHITE,
            BarStyle.SOLID,
            "&7Combat - {time_left}s",
            false,
            "minimal",
            "subtle"
        ));

        // Fire theme - Aggressive and intense
        themes.put("fire", new Theme(
            "fire",
            "&c🔥 &f{time_left}s &c🔥",
            BarColor.RED,
            BarStyle.SEGMENTED_10,
            "&c🔥 Combat with &f{opponent} &c- &f{time_left}s 🔥",
            true,
            "funny",
            "intense"
        ));

        // Ice theme - Cool and calculated
        themes.put("ice", new Theme(
            "ice",
            "&b❄ &f{time_left}s &b❄",
            BarColor.BLUE,
            BarStyle.SEGMENTED_6,
            "&b❄ Combat with &f{opponent} &b- &f{time_left}s ❄",
            true,
            "detailed",
            "calm"
        ));

        // Neon theme - Bright and flashy
        themes.put("neon", new Theme(
            "neon",
            "&d✨ &f{time_left}s &d✨",
            BarColor.PINK,
            BarStyle.SEGMENTED_12,
            "&d✨ Combat with &f{opponent} &d- &f{time_left}s ✨",
            true,
            "funny",
            "electronic"
        ));

        // Dark theme - Stealthy and mysterious
        themes.put("dark", new Theme(
            "dark",
            "&8{time_left}s",
            BarColor.WHITE,
            BarStyle.SOLID,
            "&8Combat with &0{opponent} &8- &f{time_left}s",
            false,
            "minimal",
            "subtle"
        ));

        // Clean theme - Professional and clear
        themes.put("clean", new Theme(
            "clean",
            "&aCombat: &f{time_left}s",
            BarColor.GREEN,
            BarStyle.SOLID,
            "&aCombat with &f{opponent} &a- &f{time_left}s",
            false,
            "detailed",
            "clean"
        ));
    }

    /**
     * Loads a theme from configuration section with enhanced features.
     */
    private Theme loadThemeFromConfig(String themeName, ConfigurationSection config) {
        String bossBarTitle = config.getString("bossbar.title", "&cCombat Timer: &f{time_left}s");
        String bossBarColorStr = config.getString("bossbar.color", "RED");
        String bossBarStyleStr = config.getString("bossbar.style", "SOLID");
        String actionBarFormat = config.getString("actionbar.format", "&cCombat with &f{opponent} &c- &f{time_left}s");
        boolean animatedTransitions = config.getBoolean("animations.enabled", false);
        String messageStyle = config.getString("messages.style", "minimal");
        String soundProfile = config.getString("sounds.profile", "default");

        BarColor bossBarColor = parseBarColor(bossBarColorStr);
        BarStyle bossBarStyle = parseBarStyle(bossBarStyleStr);

        return new Theme(themeName, bossBarTitle, bossBarColor, bossBarStyle, actionBarFormat,
                        animatedTransitions, messageStyle, soundProfile);
    }

    /**
     * Parses BarColor from string.
     */
    private BarColor parseBarColor(String colorStr) {
        try {
            return BarColor.valueOf(colorStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid bossbar color: " + colorStr + ". Using RED.");
            return BarColor.RED;
        }
    }

    /**
     * Parses BarStyle from string.
     */
    private BarStyle parseBarStyle(String styleStr) {
        try {
            return BarStyle.valueOf(styleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid bossbar style: " + styleStr + ". Using SOLID.");
            return BarStyle.SOLID;
        }
    }

    /**
     * Enhanced Theme data class with animation, message styles, and sound profiles.
     */
    public static class Theme {
        private final String name;
        private final String bossBarTitle;
        private final BarColor bossBarColor;
        private final BarStyle bossBarStyle;
        private final String actionBarFormat;
        private final boolean animatedTransitions;
        private final String messageStyle;
        private final String soundProfile;

        // Default constructor for backward compatibility
        public Theme(String name, String bossBarTitle, BarColor bossBarColor,
                    BarStyle bossBarStyle, String actionBarFormat) {
            this(name, bossBarTitle, bossBarColor, bossBarStyle, actionBarFormat, false, "minimal", "default");
        }

        // Enhanced constructor
        public Theme(String name, String bossBarTitle, BarColor bossBarColor,
                    BarStyle bossBarStyle, String actionBarFormat, boolean animatedTransitions,
                    String messageStyle, String soundProfile) {
            this.name = name;
            this.bossBarTitle = bossBarTitle;
            this.bossBarColor = bossBarColor;
            this.bossBarStyle = bossBarStyle;
            this.actionBarFormat = actionBarFormat;
            this.animatedTransitions = animatedTransitions;
            this.messageStyle = messageStyle;
            this.soundProfile = soundProfile;
        }

        public String getName() { return name; }
        public String getBossBarTitle() { return bossBarTitle; }
        public BarColor getBossBarColor() { return bossBarColor; }
        public BarStyle getBossBarStyle() { return bossBarStyle; }
        public String getActionBarFormat() { return actionBarFormat; }
        public boolean hasAnimatedTransitions() { return animatedTransitions; }
        public String getMessageStyle() { return messageStyle; }
        public String getSoundProfile() { return soundProfile; }
    }
}