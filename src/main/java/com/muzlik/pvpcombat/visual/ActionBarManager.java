package com.muzlik.pvpcombat.visual;

import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.scheduler.BukkitRunnable;

import com.muzlik.pvpcombat.combat.CombatManager;
import com.muzlik.pvpcombat.data.CombatSession;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages action bar messages with HEX color support and real-time updates.
 */
public class ActionBarManager {

    private final PvPCombatPlugin plugin;
    private final Map<UUID, BukkitRunnable> activeActionBars;

    public ActionBarManager(PvPCombatPlugin plugin) {
        this.plugin = plugin;
        this.activeActionBars = new ConcurrentHashMap<>();
    }

    /**
     * Sends an action bar message to a player.
     */
    public void sendActionBar(Player player, String message) {
        if (!plugin.getConfig().getBoolean("visual.actionbar.enabled", true)) {
            return;
        }

        String formattedMessage = formatMessage(message);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(formattedMessage));
    }

    /**
     * Starts real-time action bar updates for a combat session.
     */
    public void startActionBarUpdates(String sessionId, Player player1, Player player2) {
        if (!plugin.getConfig().getBoolean("visual.actionbar.enabled", true)) {
            return;
        }

        int updateInterval = plugin.getConfig().getInt("visual.actionbar.update-interval", 20);

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getCombatManager().isInCombat(player1) ||
                    !plugin.getCombatManager().isInCombat(player2)) {
                    cancel();
                    return;
                }

                String message = buildActionBarMessage(player1, player2);
                sendActionBar(player1, message);
                sendActionBar(player2, message);
            }
        };

        task.runTaskTimer(plugin, 0L, updateInterval);
        activeActionBars.put(player1.getUniqueId(), task);
        activeActionBars.put(player2.getUniqueId(), task);
    }

    /**
     * Clears action bar for a specific player.
     */
    public void clearActionBar(Player player) {
        BukkitRunnable task = activeActionBars.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
        // Send empty message to clear action bar
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
    }

    /**
     * Clears all active action bars.
     */
    public void clearAllActionBars() {
        for (BukkitRunnable task : activeActionBars.values()) {
            task.cancel();
        }
        activeActionBars.clear();
    }

    /**
     * Builds the action bar message with opponent and time information.
     */
    private String buildActionBarMessage(Player player1, Player player2) {
        String format = plugin.getConfig().getString("visual.actionbar.format",
            "&cCombat with &f{opponent} &c- &f{time_left}s");

        Player opponent1 = plugin.getCombatManager().getOpponent(player1);
        Player opponent2 = plugin.getCombatManager().getOpponent(player2);

        int timeLeft = getCombatTimeLeft(player1);

        String message1 = format
            .replace("{opponent}", opponent1 != null ? opponent1.getName() : "Unknown")
            .replace("{time_left}", String.valueOf(timeLeft));

        String message2 = format
            .replace("{opponent}", opponent2 != null ? opponent2.getName() : "Unknown")
            .replace("{time_left}", String.valueOf(timeLeft));

        // Return the same message since both players see the same info
        return message1;
    }

    /**
     * Gets remaining combat time for a player.
     */
    private int getCombatTimeLeft(Player player) {
        CombatManager cm = (CombatManager) plugin.getCombatManager();
        for (CombatSession session : cm.getActiveSessions().values()) {
            if (session.getAttacker().getUniqueId().equals(player.getUniqueId()) ||
                session.getDefender().getUniqueId().equals(player.getUniqueId())) {
                return session.getRemainingTime();
            }
        }
        return 0;
    }

    /**
     * Formats message with HEX color support and placeholders using MessageFormatter.
     */
    private String formatMessage(String message) {
        MessageFormatter formatter = new MessageFormatter(plugin);
        return formatter.formatMessage(message, null, new java.util.HashMap<>());
    }

    /**
     * Gets active action bar tasks for cleanup.
     */
    public Map<UUID, BukkitRunnable> getActiveActionBars() {
        return new ConcurrentHashMap<>(activeActionBars);
    }
}