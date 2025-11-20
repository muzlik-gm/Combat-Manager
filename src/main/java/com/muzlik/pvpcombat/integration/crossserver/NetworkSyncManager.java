package com.muzlik.pvpcombat.integration.crossserver;

import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import com.muzlik.pvpcombat.data.CombatSession;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract base class for managing cross-server combat synchronization.
 * Provides common functionality for both BungeeCord and Velocity implementations.
 */
public abstract class NetworkSyncManager {

    protected final PvPCombatPlugin plugin;
    protected final ServerCombatState serverCombatState;
    protected final String syncChannel;
    protected boolean enabled;
    protected boolean broadcastEnabled;

    public NetworkSyncManager(PvPCombatPlugin plugin, String syncChannel) {
        this.plugin = plugin;
        this.syncChannel = syncChannel;
        this.serverCombatState = new ServerCombatState(plugin.getServer().getName());
        this.enabled = plugin.getConfig().getBoolean("cross-server-sync.enabled", false);
        this.broadcastEnabled = plugin.getConfig().getBoolean("cross-server-sync.broadcast.enabled", false);
    }

    /**
     * Initializes the network sync manager.
     */
    public abstract void initialize();

    /**
     * Shuts down the network sync manager.
     */
    public abstract void shutdown();

    /**
     * Broadcasts combat start event across the network.
     */
    public abstract CompletableFuture<Void> broadcastCombatStart(CombatSession session);

    /**
     * Broadcasts combat end event across the network.
     */
    public abstract CompletableFuture<Void> broadcastCombatEnd(UUID sessionId, String reason);

    /**
     * Broadcasts a global message to all servers.
     */
    public abstract CompletableFuture<Void> broadcastMessage(String message);

    /**
     * Checks if server switching should be prevented for a player.
     */
    public abstract boolean shouldPreventServerSwitch(Player player);

    /**
     * Gets the message to display when server switching is prevented.
     */
    public String getServerSwitchPreventionMessage(Player player) {
        CombatSyncData state = serverCombatState.getPlayerCombatState(player.getUniqueId());
        if (state != null) {
            return plugin.getConfig().getString("cross-server-sync.messages.switch-prevented",
                "&cCannot switch servers while in combat! Time remaining: {time}s");
        }
        return "&cCannot switch servers while in combat!";
    }

    /**
     * Processes incoming sync packets.
     */
    protected void processIncomingPacket(SyncPacket packet) {
        switch (packet.getType()) {
            case COMBAT_START:
                handleCombatStart(packet.getData());
                break;
            case COMBAT_END:
                handleCombatEnd(packet.getData());
                break;
            case BROADCAST_MESSAGE:
                handleBroadcastMessage(packet.getData());
                break;
            case COMBAT_UPDATE:
                handleCombatUpdate(packet.getData());
                break;
        }
    }

    /**
     * Handles incoming combat start notification.
     */
    protected void handleCombatStart(CombatSyncData data) {
        serverCombatState.updateCombatState(data.getAttackerId(), data);
        serverCombatState.updateCombatState(data.getDefenderId(), data);

        plugin.getLogger().fine(String.format("Received combat start notification: %s vs %s on %s",
            data.getAttackerName(), data.getDefenderName(), data.getServerName()));
    }

    /**
     * Handles incoming combat end notification.
     */
    protected void handleCombatEnd(CombatSyncData data) {
        serverCombatState.updateCombatState(data.getAttackerId(), new CombatSyncData(
            data.getSessionId(), data.getAttackerId(), data.getDefenderId(),
            data.getAttackerName(), data.getDefenderName(), data.getServerName(),
            data.getStartTime(), 0, false
        ));
        serverCombatState.updateCombatState(data.getDefenderId(), new CombatSyncData(
            data.getSessionId(), data.getAttackerId(), data.getDefenderId(),
            data.getAttackerName(), data.getDefenderName(), data.getServerName(),
            data.getStartTime(), 0, false
        ));

        plugin.getLogger().fine(String.format("Received combat end notification for session %s",
            data.getSessionId()));
    }

    /**
     * Handles incoming broadcast message.
     */
    protected void handleBroadcastMessage(CombatSyncData data) {
        if (broadcastEnabled) {
            String message = plugin.getConfig().getString("cross-server-sync.broadcast.format",
                "&6[Network] &e{attacker} &fis now in combat with &e{defender} &fon &b{server}");
            message = message.replace("{attacker}", data.getAttackerName())
                           .replace("{defender}", data.getDefenderName())
                           .replace("{server}", data.getServerName());

            // Broadcast to all players on this server
            plugin.getServer().broadcastMessage(message);
        }
    }

    /**
     * Handles incoming combat update.
     */
    protected void handleCombatUpdate(CombatSyncData data) {
        serverCombatState.updateCombatState(data.getAttackerId(), data);
        serverCombatState.updateCombatState(data.getDefenderId(), data);
    }

    /**
     * Checks if cross-server sync is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Gets the server combat state tracker.
     */
    public ServerCombatState getServerCombatState() {
        return serverCombatState;
    }

    /**
     * Cleans up expired states periodically.
     */
    public void cleanupStates() {
        serverCombatState.cleanupExpiredStates();
    }
}