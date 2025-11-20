package com.muzlik.pvpcombat.commands;

import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.attribute.Attribute;

/**
 * Handles player-facing combat commands with enhanced error handling and user feedback.
 *
 * @author PvPCombat Plugin Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class PlayerCommand {

    private final PvPCombatPlugin plugin;

    /**
     * Constructs a new PlayerCommand handler.
     *
     * @param plugin The main plugin instance
     * @throws IllegalArgumentException if plugin is null
     */
    public PlayerCommand(PvPCombatPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        this.plugin = plugin;
    }

    /**
     * Handles player command execution with comprehensive error handling.
     *
     * @param player The player executing the command
     * @param args The command arguments
     * @return true if command was handled, false if usage should be shown
     * @throws IllegalStateException if plugin components are not properly initialized
     */
    public boolean handleCommand(Player player, String[] args) {
        try {
            if (args.length < 1) {
                return false; // Show usage
            }

            String subCommand = args[0].toLowerCase();

            switch (subCommand) {
                case "status":
                    return handleStatusCommand(player);
                case "summary":
                    return handleSummaryCommand(player);
                case "toggle-style":
                    return handleToggleStyleCommand(player);
                default:
                    return false; // Unknown subcommand
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error handling player command: " + e.getMessage());
            e.printStackTrace();
            player.sendMessage("§cAn error occurred while processing your command. Please try again.");
            return true;
        }
    }

    /**
     * Shows player's combat state with enhanced error handling and detailed information.
     *
     * @param player The player to show status for
     * @return true if command executed successfully
     */
    private boolean handleStatusCommand(Player player) {
        try {
            if (plugin.getCombatManager() == null) {
                player.sendMessage("§cCombat system is not available. Please contact an administrator.");
                return true;
            }

            if (!plugin.getCombatManager().isInCombat(player)) {
                player.sendMessage("§aYou are not in combat.");
                player.sendMessage("§7You can engage in PvP combat or wait for the combat timer to expire.");
                return true;
            }

            Player opponent = plugin.getCombatManager().getOpponent(player);

            // Display combat status header
            player.sendMessage("§6=== Combat Status ===");
            player.sendMessage("§eStatus: §cIn Combat");

            if (opponent != null) {
                player.sendMessage("§eOpponent: §f" + opponent.getName());
                // Show opponent health if available
                double opponentHealth = opponent.getHealth();
                double opponentMaxHealth = ((LivingEntity) opponent).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                player.sendMessage(String.format("§eOpponent Health: §c%.1f §7/ §c%.1f ❤", opponentHealth, opponentMaxHealth));
            } else {
                player.sendMessage("§eOpponent: §fUnknown");
            }

            // Show player's own health
            double playerHealth = player.getHealth();
            double playerMaxHealth = ((LivingEntity) player).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            player.sendMessage(String.format("§eYour Health: §a%.1f §7/ §a%.1f ❤", playerHealth, playerMaxHealth));

            player.sendMessage("§eTime Remaining: §fUnknown §7(Enhanced status pending)");
            player.sendMessage("§7§oUse /combat summary to view detailed fight statistics.");

            return true;

        } catch (Exception e) {
            plugin.getLogger().severe("Error showing combat status: " + e.getMessage());
            player.sendMessage("§cFailed to show combat status. The system may be temporarily unavailable.");
            return true;
        }
    }

    /**
     * Formats time in MM:SS format with null safety.
     *
     * @param milliseconds The time in milliseconds to format
     * @return Formatted time string, or "Unknown" if invalid
     */
    private String formatTime(long milliseconds) {
        try {
            if (milliseconds < 0) {
                return "Unknown";
            }
            long seconds = milliseconds / 1000;
            long minutes = seconds / 60;
            seconds %= 60;
            return String.format("%02d:%02d", minutes, seconds);
        } catch (Exception e) {
            plugin.getLogger().warning("Error formatting time: " + e.getMessage());
            return "Unknown";
        }
    }

    /**
     * Handles the summary subcommand.
     */
    private boolean handleSummaryCommand(Player player) {
        try {
            player.sendMessage("§6=== Combat Summary ===");
            player.sendMessage("§eSummary feature coming soon!");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Error showing combat summary: " + e.getMessage());
            player.sendMessage("§cFailed to show combat summary.");
            return true;
        }
    }

    /**
     * Handles the toggle-style subcommand.
     */
    private boolean handleToggleStyleCommand(Player player) {
        try {
            player.sendMessage("§eStyle toggle feature coming soon!");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Error toggling style: " + e.getMessage());
            player.sendMessage("§cFailed to toggle style.");
            return true;
        }
    }
}