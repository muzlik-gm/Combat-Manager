package com.muzlik.pvpcombat.integration.crossserver;

import com.muzlik.pvpcombat.core.PvPCombatPlugin;

/**
 * Factory class for creating cross-server sync managers.
 * Handles platform detection and initialization.
 */
public class CrossServerIntegration {

    private final PvPCombatPlugin plugin;
    private NetworkSyncManager syncManager;

    public CrossServerIntegration(PvPCombatPlugin plugin) {
        this.plugin = plugin;
        initializeSyncManager();
    }

    private void initializeSyncManager() {
        if (!plugin.getConfig().getBoolean("integration.cross-server-sync.enabled", false)) {
            plugin.getLogger().info("Cross-server sync is disabled");
            return;
        }

        String platform = plugin.getConfig().getString("integration.cross-server-sync.platform", "AUTO").toUpperCase();

        try {
            switch (platform) {
                case "BUNGEE":
                case "BUNGEECORD":
                    // Try to load BungeeCord classes
                    Class.forName("net.md_5.bungee.api.ProxyServer");
                    // syncManager = new BungeeSyncHandler(plugin, null); // Would need bungee plugin instance
                    plugin.getLogger().warning("BungeeCord platform detected but handler creation requires bungee plugin instance");
                    break;

                case "VELOCITY":
                    // Try to load Velocity classes
                    Class.forName("com.velocitypowered.api.proxy.ProxyServer");
                    // syncManager = new VelocitySyncHandler(plugin, null); // Would need velocity proxy instance
                    plugin.getLogger().warning("Velocity platform detected but handler creation requires velocity proxy instance");
                    break;

                case "AUTO":
                default:
                    // Auto-detect platform
                    try {
                        Class.forName("net.md_5.bungee.api.ProxyServer");
                        plugin.getLogger().info("Auto-detected BungeeCord platform");
                        // syncManager = new BungeeSyncHandler(plugin, null);
                        plugin.getLogger().warning("BungeeCord detected but handler creation requires bungee plugin instance");
                    } catch (ClassNotFoundException bungeeEx) {
                        try {
                            Class.forName("com.velocitypowered.api.proxy.ProxyServer");
                            plugin.getLogger().info("Auto-detected Velocity platform");
                            // syncManager = new VelocitySyncHandler(plugin, null);
                            plugin.getLogger().warning("Velocity detected but handler creation requires velocity proxy instance");
                        } catch (ClassNotFoundException velocityEx) {
                            plugin.getLogger().info("No proxy platform detected, running in standalone mode");
                        }
                    }
                    break;
            }

            if (syncManager != null) {
                syncManager.initialize();
                plugin.getLogger().info("Cross-server sync initialized for platform: " + platform);
            }

        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize cross-server sync: " + e.getMessage());
            plugin.getLogger().warning("Cross-server sync features will be disabled");
        }
    }

    /**
     * Gets the active network sync manager.
     */
    public NetworkSyncManager getSyncManager() {
        return syncManager;
    }

    /**
     * Checks if cross-server sync is available.
     */
    public boolean isSyncAvailable() {
        return syncManager != null && syncManager.isEnabled();
    }

    /**
     * Shuts down the cross-server integration.
     */
    public void shutdown() {
        if (syncManager != null) {
            syncManager.shutdown();
            syncManager = null;
        }
    }
}