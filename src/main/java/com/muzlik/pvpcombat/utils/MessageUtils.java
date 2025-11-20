package com.muzlik.pvpcombat.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Utility class for message formatting and sending.
 */
public class MessageUtils {

    /**
     * Sends a formatted message to a player.
     */
    public static void sendMessage(Player player, String message, Object... args) {
        String formatted = String.format(message, args);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', formatted));
    }

    /**
     * Sends a message from the configuration to a player.
     */
    public static void sendConfigMessage(Player player, FileConfiguration config, String key, Object... args) {
        String message = config.getString(key, "Message not found: " + key);
        sendMessage(player, message, args);
    }

    /**
     * Formats a message with color codes.
     */
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Formats a message with placeholders replaced.
     */
    public static String formatMessage(String template, Object... replacements) {
        return String.format(template, replacements);
    }

    /**
     * Creates a progress bar string.
     */
    public static String createProgressBar(double progress, int length) {
        int filled = (int) (progress * length);
        StringBuilder bar = new StringBuilder();
        bar.append(ChatColor.GREEN);
        for (int i = 0; i < filled; i++) {
            bar.append("█");
        }
        bar.append(ChatColor.RED);
        for (int i = filled; i < length; i++) {
            bar.append("█");
        }
        return bar.toString();
    }
}