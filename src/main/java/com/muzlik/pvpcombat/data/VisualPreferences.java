package com.muzlik.pvpcombat.data;

import java.util.UUID;

/**
 * Stores player-specific visual preferences for themes, sounds, and message styles.
 */
public class VisualPreferences {

    private final UUID playerId;
    private String selectedTheme;
    private String selectedSoundProfile;
    private String selectedMessageStyle;
    private boolean animationsEnabled;
    private boolean soundsEnabled;
    private boolean bossBarEnabled;
    private boolean actionBarEnabled;

    public VisualPreferences(UUID playerId) {
        this.playerId = playerId;
        // Default values
        this.selectedTheme = "default";
        this.selectedSoundProfile = "default";
        this.selectedMessageStyle = "minimal";
        this.animationsEnabled = true;
        this.soundsEnabled = true;
        this.bossBarEnabled = true;
        this.actionBarEnabled = true;
    }

    // Constructor for loading from storage
    public VisualPreferences(UUID playerId, String theme, String soundProfile, String messageStyle,
                           boolean animations, boolean sounds, boolean bossBar, boolean actionBar) {
        this.playerId = playerId;
        this.selectedTheme = theme != null ? theme : "default";
        this.selectedSoundProfile = soundProfile != null ? soundProfile : "default";
        this.selectedMessageStyle = messageStyle != null ? messageStyle : "minimal";
        this.animationsEnabled = animations;
        this.soundsEnabled = sounds;
        this.bossBarEnabled = bossBar;
        this.actionBarEnabled = actionBar;
    }

    public UUID getPlayerId() { return playerId; }

    public String getSelectedTheme() { return selectedTheme; }
    public void setSelectedTheme(String theme) { this.selectedTheme = theme; }

    public String getSelectedSoundProfile() { return selectedSoundProfile; }
    public void setSelectedSoundProfile(String profile) { this.selectedSoundProfile = profile; }

    public String getSelectedMessageStyle() { return selectedMessageStyle; }
    public void setSelectedMessageStyle(String style) { this.selectedMessageStyle = style; }

    public boolean isAnimationsEnabled() { return animationsEnabled; }
    public void setAnimationsEnabled(boolean enabled) { this.animationsEnabled = enabled; }

    public boolean isSoundsEnabled() { return soundsEnabled; }
    public void setSoundsEnabled(boolean enabled) { this.soundsEnabled = enabled; }

    public boolean isBossBarEnabled() { return bossBarEnabled; }
    public void setBossBarEnabled(boolean enabled) { this.bossBarEnabled = enabled; }

    public boolean isActionBarEnabled() { return actionBarEnabled; }
    public void setActionBarEnabled(boolean enabled) { this.actionBarEnabled = enabled; }

    /**
     * Resets preferences to defaults.
     */
    public void resetToDefaults() {
        this.selectedTheme = "default";
        this.selectedSoundProfile = "default";
        this.selectedMessageStyle = "minimal";
        this.animationsEnabled = true;
        this.soundsEnabled = true;
        this.bossBarEnabled = true;
        this.actionBarEnabled = true;
    }

    /**
     * Creates a copy of these preferences.
     */
    public VisualPreferences copy() {
        return new VisualPreferences(playerId, selectedTheme, selectedSoundProfile, selectedMessageStyle,
                                   animationsEnabled, soundsEnabled, bossBarEnabled, actionBarEnabled);
    }
}