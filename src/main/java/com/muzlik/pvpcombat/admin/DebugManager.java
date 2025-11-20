package com.muzlik.pvpcombat.admin;

import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Debug mode functionality for showing triggers, checks, sync packets, and cooldown states.
 */
public class DebugManager {

    private final PvPCombatPlugin plugin;
    private final Set<UUID> debugPlayers;
    private static final int DEBUG_UPDATE_INTERVAL = 20; // ticks

    public DebugManager(PvPCombatPlugin plugin) {
        this.plugin = plugin;
        this.debugPlayers = new HashSet<>();
    }

    /**
     * Toggles debug mode for a player.
     */
    public boolean toggleDebugMode(Player player) {
        UUID playerId = player.getUniqueId();

        if (debugPlayers.contains(playerId)) {
            debugPlayers.remove(playerId);
            disableDebugMode(player);
            return false;
        } else {
            debugPlayers.add(playerId);
            enableDebugMode(player);
            return true;
        }
    }

    /**
     * Enables debug mode for a player.
     */
    private void enableDebugMode(Player player) {
        player.sendMessage("§aDebug mode enabled! You will now see detailed combat information.");

        // Start debug update task
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!debugPlayers.contains(player.getUniqueId()) ||
                    !player.isOnline()) {
                    this.cancel();
                    return;
                }

                showDebugInfo(player);
            }
        }.runTaskTimer(plugin, 0L, DEBUG_UPDATE_INTERVAL);
    }

    /**
     * Disables debug mode for a player.
     */
    private void disableDebugMode(Player player) {
        player.sendMessage("§cDebug mode disabled.");
    }

    /**
     * Shows debug information to the player.
     */
    private void showDebugInfo(Player player) {
        // Show combat triggers and checks
        showCombatTriggers(player);

        // Show sync packet information
        showSyncPackets(player);

        // Show cooldown states
        showCooldownStates(player);
    }

    /**
     * Shows combat triggers and checks.
     */
    private void showCombatTriggers(Player player) {
        // Placeholder for trigger information
        player.sendMessage("§7[DEBUG] Combat Triggers:");
        player.sendMessage("§7[DEBUG] - Damage checks: Active");
        player.sendMessage("§7[DEBUG] - Timer updates: Active");
        player.sendMessage("§7[DEBUG] - Interference detection: Active");
    }

    /**
     * Shows sync packet information.
     */
    private void showSyncPackets(Player player) {
        // Placeholder for sync packet info
        player.sendMessage("§7[DEBUG] Sync Packets:");
        player.sendMessage("§7[DEBUG] - Last packet: None (Cross-server sync pending)");
        player.sendMessage("§7[DEBUG] - Packet queue: 0");
        player.sendMessage("§7[DEBUG] - Network status: Disconnected");
    }

    /**
     * Shows cooldown states.
     */
    private void showCooldownStates(Player player) {
        // Placeholder for cooldown information
        player.sendMessage("§7[DEBUG] Cooldown States:");
        player.sendMessage("§7[DEBUG] - Combat timer: N/A");
        player.sendMessage("§7[DEBUG] - Restriction cooldowns: None");
        player.sendMessage("§7[DEBUG] - Visual update cooldown: Ready");
    }

    /**
     * Checks if debug mode is enabled for a player.
     */
    public boolean isDebugEnabled(Player player) {
        return debugPlayers.contains(player.getUniqueId());
    }

    /**
     * Logs debug information (only when debug mode is active).
     */
    public void logDebug(Player player, String message) {
        if (debugPlayers.contains(player.getUniqueId())) {
            player.sendMessage("§7[DEBUG] §f" + message);
        }
    }

    /**
     * Gets the set of players with debug mode enabled.
     */
    public Set<UUID> getDebugPlayers() {
        return new HashSet<>(debugPlayers);
    }
}