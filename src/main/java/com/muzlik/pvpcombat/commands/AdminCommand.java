package com.muzlik.pvpcombat.commands;

import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import com.muzlik.pvpcombat.admin.CombatInspector;
import com.muzlik.pvpcombat.admin.DebugManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles administrative combat commands with enhanced error handling and permission checking.
 *
 * @author PvPCombat Plugin Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class AdminCommand implements CommandExecutor, TabCompleter {

    private final PvPCombatPlugin plugin;
    private final CombatInspector combatInspector;
    private final DebugManager debugManager;

    /**
     * Constructs a new AdminCommand handler.
     *
     * @param plugin The main plugin instance
     * @throws IllegalArgumentException if plugin is null
     */
    public AdminCommand(PvPCombatPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        this.plugin = plugin;
        this.combatInspector = new CombatInspector(plugin);
        this.debugManager = new DebugManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cThis command can only be used by players.");
                return true;
            }

            Player player = (Player) sender;

            if (!player.hasPermission("pvpcombat.admin")) {
                player.sendMessage("§cYou don't have permission to use this command.");
                return true;
            }

            if (args.length < 1) {
                return false; // Show usage
            }

            String subCommand = args[0].toLowerCase();

            switch (subCommand) {
                case "inspect":
                    return handleInspectCommand(player, args);
                case "summary":
                    return handleAdminSummaryCommand(player, args);
                case "reload":
                    return handleReloadCommand(player);
                case "debug":
                    return handleDebugCommand(player, args);
                default:
                    return false; // Unknown subcommand
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error handling admin command: " + e.getMessage());
            e.printStackTrace();
            if (sender instanceof Player) {
                sender.sendMessage("§cAn error occurred while processing the command. Check console for details.");
            }
            return true;
        }
    }

    /**
     * Shows real-time combat info for a player with enhanced error handling.
     *
     * @param player The admin player executing the command
     * @param args The command arguments
     * @return true if command executed successfully
     */
    private boolean handleInspectCommand(Player player, String[] args) {
        try {
            if (args.length < 2) {
                player.sendMessage("§cUsage: /combat inspect <player>");
                player.sendMessage("§7Inspects real-time combat status of a player.");
                return true;
            }

            String targetName = args[1];
            if (targetName == null || targetName.trim().isEmpty()) {
                player.sendMessage("§cPlayer name cannot be empty.");
                return true;
            }

            Player target = Bukkit.getPlayer(targetName);

            if (target == null) {
                player.sendMessage("§cPlayer '" + targetName + "' is not online.");
                return true;
            }

            // Check if target is within inspection range (if configured)
            if (plugin.getConfigManager() != null) {
                Object rangeValue = plugin.getConfigManager().getConfigValue("commands.admin.inspection-range");
                int rangeLimit = (rangeValue instanceof Number) ? ((Number) rangeValue).intValue() : 50;
                if (rangeLimit > 0 && !player.getWorld().equals(target.getWorld())) {
                    player.sendMessage("§cTarget player is in a different world.");
                    return true;
                }
                if (rangeLimit > 0 && player.getLocation().distance(target.getLocation()) > rangeLimit) {
                    player.sendMessage("§cTarget player is too far away (max: " + rangeLimit + " blocks).");
                    return true;
                }
            }

            combatInspector.inspectPlayer(player, target);
            return true;

        } catch (Exception e) {
            plugin.getLogger().severe("Error in inspect command: " + e.getMessage());
            player.sendMessage("§cFailed to inspect player. Check console for details.");
            return true;
        }
    }

    /**
     * Shows last combat stats for a player with enhanced error handling.
     *
     * @param player The admin player executing the command
     * @param args The command arguments
     * @return true if command executed successfully
     */
    private boolean handleAdminSummaryCommand(Player player, String[] args) {
        try {
            if (args.length < 2) {
                player.sendMessage("§cUsage: /combat summary <player>");
                player.sendMessage("§7Shows the last combat summary for a player.");
                return true;
            }

            String targetName = args[1];
            if (targetName == null || targetName.trim().isEmpty()) {
                player.sendMessage("§cPlayer name cannot be empty.");
                return true;
            }

            Player target = Bukkit.getPlayer(targetName);

            if (target == null) {
                player.sendMessage("§cPlayer '" + targetName + "' is not online.");
                return true;
            }

            combatInspector.showPlayerSummary(player, target);
            return true;

        } catch (Exception e) {
            plugin.getLogger().severe("Error in summary command: " + e.getMessage());
            player.sendMessage("§cFailed to show player summary. Check console for details.");
            return true;
        }
    }

    /**
     * Reloads configuration with proper implementation.
     *
     * @param player The admin player executing the command
     * @return true if command executed successfully
     */
    private boolean handleReloadCommand(Player player) {
        try {
            if (plugin.getConfigManager() == null) {
                player.sendMessage("§cConfiguration manager is not available.");
                return true;
            }

            // Reload configuration using the interface method
            plugin.getConfigManager().reloadConfig();

            player.sendMessage("§aConfiguration reloaded successfully!");
            player.sendMessage("§7All combat systems have been updated with new settings.");

            return true;

        } catch (Exception e) {
            plugin.getLogger().severe("Error reloading configuration: " + e.getMessage());
            player.sendMessage("§cFailed to reload configuration. Check console for details.");
            return true;
        }
    }

    /**
     * Toggles debug mode with enhanced feedback.
     *
     * @param player The admin player executing the command
     * @param args The command arguments
     * @return true if command executed successfully
     */
    private boolean handleDebugCommand(Player player, String[] args) {
        try {
            boolean enabled = debugManager.toggleDebugMode(player);
            player.sendMessage("§aDebug mode " + (enabled ? "enabled" : "disabled") + "!");

            if (enabled) {
                player.sendMessage("§7Debug information will now be displayed in chat.");
                player.sendMessage("§7Use /combat debug again to disable.");
            } else {
                player.sendMessage("§7Debug mode disabled.");
            }

            return true;

        } catch (Exception e) {
            plugin.getLogger().severe("Error toggling debug mode: " + e.getMessage());
            player.sendMessage("§cFailed to toggle debug mode. Check console for details.");
            return true;
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
                List<String> commands = Arrays.asList("inspect", "summary", "reload", "debug");
                for (String cmd : commands) {
                    if (cmd.toLowerCase().startsWith(input)) {
                        completions.add(cmd);
                    }
                }
            } else if (args.length == 2) {
                String subCmd = args[0].toLowerCase();
                if ("inspect".equals(subCmd) || "summary".equals(subCmd)) {
                    String input = args[1].toLowerCase();
                    plugin.getServer().getOnlinePlayers().stream()
                        .filter(p -> p.getName().toLowerCase().startsWith(input))
                        .limit(10)
                        .forEach(p -> completions.add(p.getName()));
                }
            }

            return completions;

        } catch (Exception e) {
            plugin.getLogger().warning("Error in admin tab completion: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}