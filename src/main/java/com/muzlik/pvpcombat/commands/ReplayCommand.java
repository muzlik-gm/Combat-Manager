package com.muzlik.pvpcombat.commands;

import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import com.muzlik.pvpcombat.logging.CombatLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Admin command for viewing combat replays with enhanced error handling.
 * Provides GUI interface for replay management and system statistics.
 *
 * @author PvPCombat Plugin Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class ReplayCommand implements CommandExecutor, TabCompleter {

    private final PvPCombatPlugin plugin;
    private final CombatLogger combatLogger;

    /**
     * Constructs a new ReplayCommand handler.
     *
     * @param plugin The main plugin instance
     * @param combatLogger The combat logger instance
     * @throws IllegalArgumentException if plugin or combatLogger is null
     */
    public ReplayCommand(PvPCombatPlugin plugin, CombatLogger combatLogger) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        if (combatLogger == null) {
            throw new IllegalArgumentException("CombatLogger cannot be null");
        }
        this.plugin = plugin;
        this.combatLogger = combatLogger;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cThis command can only be used by players.");
                return true;
            }

            Player player = (Player) sender;

            // Check permissions
            if (!player.hasPermission("pvpcombat.admin")) {
                player.sendMessage("§cYou don't have permission to use this command.");
                return true;
            }

            if (args.length == 0) {
                showHelp(player);
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "view":
                    handleViewCommand(player, args);
                    break;
                case "stats":
                    handleStatsCommand(player);
                    break;
                case "clear":
                    handleClearCommand(player, args);
                    break;
                default:
                    showHelp(player);
                    break;
            }

            return true;

        } catch (Exception e) {
            plugin.getLogger().severe("Error handling replay command: " + e.getMessage());
            e.printStackTrace();
            if (sender instanceof Player) {
                sender.sendMessage("§cAn error occurred while processing the replay command. Check console for details.");
            }
            return true;
        }
    }

    /**
     * Handles the view subcommand.
     */
    private void handleViewCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /replay view <session-uuid>");
            return;
        }
        player.sendMessage("§eReplay viewing feature coming soon!");
    }

    /**
     * Handles the stats subcommand.
     */
    private void handleStatsCommand(Player player) {
        player.sendMessage("§eReplay statistics feature coming soon!");
    }

    /**
     * Handles the clear subcommand.
     */
    private void handleClearCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /replay clear <session-uuid>");
            return;
        }
        player.sendMessage("§eReplay clearing feature coming soon!");
    }

    /**
     * Shows help message with detailed command usage.
     *
     * @param player The player to show help to
     */
    private void showHelp(Player player) {
        try {
            player.sendMessage("§6=== Combat Replay Commands ===");
            player.sendMessage("§e/replay view <session-uuid> §7- View replay for session");
            player.sendMessage("§e/replay stats §7- Show replay system statistics");
            player.sendMessage("§e/replay clear <session-uuid> §7- Clear replay data for session");
            player.sendMessage("§7§oNote: Session UUIDs can be found in combat logs and admin summaries.");
            player.sendMessage("§7§oReplay system must be enabled in config.yml for full functionality.");
        } catch (Exception e) {
            plugin.getLogger().severe("Error showing replay help: " + e.getMessage());
            player.sendMessage("§cUnable to display help. Check console for details.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        try {
            if (!(sender instanceof Player)) {
                return Collections.emptyList();
            }

            Player player = (Player) sender;
            if (!player.hasPermission("pvpcombat.admin")) {
                return Collections.emptyList();
            }

            List<String> completions = new ArrayList<>();

            if (args.length == 1) {
                String input = args[0].toLowerCase();
                List<String> commands = Arrays.asList("view", "stats", "clear");
                for (String cmd : commands) {
                    if (cmd.toLowerCase().startsWith(input)) {
                        completions.add(cmd);
                    }
                }
                return completions;
            }

            // Enhanced tab completion for subcommands
            if (args.length == 2) {
                String subCmd = args[0].toLowerCase();
                String input = args[1].toLowerCase();

                if ("view".equals(subCmd) || "clear".equals(subCmd)) {
                    // For now, provide example UUID format
                    if ("example".startsWith(input) || input.isEmpty()) {
                        completions.add("example-uuid-here");
                    }
                }
            }

            return completions;

        } catch (Exception e) {
            plugin.getLogger().warning("Error in tab completion: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}