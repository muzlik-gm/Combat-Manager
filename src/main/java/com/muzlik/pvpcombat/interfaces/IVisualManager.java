package com.muzlik.pvpcombat.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.Sound;

/**
 * Interface for managing visual elements like bossbars, action bars, and sounds.
 */
public interface IVisualManager {

    /**
     * Displays a bossbar for a combat session.
     *
     * @param sessionId The combat session ID
     */
    void displayBossBar(String sessionId);

    /**
     * Sends an action bar message to a player.
     *
     * @param player The player to send to
     * @param message The message to display
     */
    void sendActionBar(Player player, String message);

    /**
     * Plays a sound for a player.
     *
     * @param player The player to play for
     * @param sound The sound to play
     */
    void playSound(Player player, Sound sound);

    /**
     * Clears all visual elements for a player.
     *
     * @param player The player to clear visuals for
     */
    void clearVisuals(Player player);

    /**
     * Updates the bossbar progress for a session.
     *
     * @param sessionId The session ID
     * @param progress Progress from 0.0 to 1.0
     */
    void updateBossBarProgress(String sessionId, double progress);
}