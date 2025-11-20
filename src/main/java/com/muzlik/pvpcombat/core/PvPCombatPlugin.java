package com.muzlik.pvpcombat.core;

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

    @Override
    public void onEnable() {
        instance = this;

        // Initialize plugin manager
        pluginManager = new PluginManager(this);

        // Load configurations
        configManager = pluginManager.getConfigManager();
        configManager.loadConfig();

        // Initialize subsystems
        combatManager = pluginManager.getCombatManager();
        visualManager = pluginManager.getVisualManager();
        restrictionManager = pluginManager.getRestrictionManager();

        // Register events and commands
        pluginManager.registerEvents();
        pluginManager.registerCommands();

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
}