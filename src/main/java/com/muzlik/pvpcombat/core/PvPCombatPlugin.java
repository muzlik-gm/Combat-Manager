package com.muzlik.pvpcombat.core;

import com.muzlik.pvpcombat.admin.LoggingManager;
import com.muzlik.pvpcombat.interfaces.ICombatManager;
import com.muzlik.pvpcombat.interfaces.IConfigManager;
import com.muzlik.pvpcombat.interfaces.IRestrictionManager;
import com.muzlik.pvpcombat.interfaces.IVisualManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class extending JavaPlugin.
 */
public class PvPCombatPlugin extends JavaPlugin {

    private static PvPCombatPlugin instance;

    private PluginManager pluginManager;
    private ICombatManager combatManager;
    private IVisualManager visualManager;
    private IRestrictionManager restrictionManager;
    private IConfigManager configManager;
    private LoggingManager loggingManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize plugin manager
        pluginManager = new PluginManager(this);

        // Load configurations
        configManager = pluginManager.getConfigManager();
        configManager.loadConfig();

        // Initialize logging manager
        loggingManager = new LoggingManager(this);

        // Initialize subsystems
        combatManager = pluginManager.getCombatManager();
        visualManager = pluginManager.getVisualManager();
        restrictionManager = pluginManager.getRestrictionManager();

        // Register events and commands
        pluginManager.registerEvents();
        pluginManager.registerCommands();
        
        // Register PlaceholderAPI expansion if available
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new com.muzlik.pvpcombat.integration.PvPCombatExpansion(this).register();
            getLogger().info("PlaceholderAPI expansion registered!");
        }

        getLogger().info("PvPCombat plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        if (pluginManager != null) {
            pluginManager.shutdown();
        }

        instance = null;
        getLogger().info("PvPCombat plugin has been disabled!");
    }

    // Getters for accessing managers
    public static PvPCombatPlugin getInstance() {
        return instance;
    }

    public ICombatManager getCombatManager() {
        return combatManager;
    }

    public IVisualManager getVisualManager() {
        return visualManager;
    }

    public IRestrictionManager getRestrictionManager() {
        return restrictionManager;
    }

    public IConfigManager getConfigManager() {
        return configManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public LoggingManager getLoggingManager() {
        return loggingManager;
    }
}