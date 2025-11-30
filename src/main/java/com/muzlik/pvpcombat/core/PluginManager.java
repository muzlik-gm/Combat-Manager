package com.muzlik.pvpcombat.core;

import com.muzlik.pvpcombat.commands.AdminCommand;
import com.muzlik.pvpcombat.commands.CombatCommand;
import com.muzlik.pvpcombat.commands.ReplayCommand;
import com.muzlik.pvpcombat.config.ConfigManager;
import com.muzlik.pvpcombat.logging.CombatLogger;
import com.muzlik.pvpcombat.interfaces.ICombatManager;
import com.muzlik.pvpcombat.interfaces.IConfigManager;
import com.muzlik.pvpcombat.interfaces.IRestrictionManager;
import com.muzlik.pvpcombat.interfaces.IVisualManager;
import com.muzlik.pvpcombat.visual.VisualManager;
import com.muzlik.pvpcombat.combat.CombatTracker;
import com.muzlik.pvpcombat.combat.CombatManager;
import com.muzlik.pvpcombat.performance.PerformanceMonitor;
import com.muzlik.pvpcombat.performance.TPSMonitor;
import com.muzlik.pvpcombat.utils.CacheManager;
import com.muzlik.pvpcombat.restrictions.RestrictionManager;
import com.muzlik.pvpcombat.combat.AntiInterferenceManager;
import org.bukkit.Bukkit;
import com.muzlik.pvpcombat.events.CombatEventListener;

/**
 * Coordinates all subsystems and manages plugin lifecycle.
 */
public class PluginManager {

    private final PvPCombatPlugin plugin;

    private ICombatManager combatManager;
    private IVisualManager visualManager;
    private IRestrictionManager restrictionManager;
    private IConfigManager configManager;
    private CombatTracker combatTracker;

    public PluginManager(PvPCombatPlugin plugin) {
        this.plugin = plugin;
        initializeManagers();
    }

    private void initializeManagers() {
        // Initialize configuration manager first as others depend on it
        this.configManager = new ConfigManager(plugin);

        // Initialize combat tracker
        this.combatTracker = new CombatTracker();

        // Initialize shared components
        CacheManager cacheManager = new CacheManager(plugin);
        CombatLogger combatLogger = new CombatLogger(plugin);
        TPSMonitor tpsMonitor = new TPSMonitor(plugin);
        PerformanceMonitor performanceMonitor = new PerformanceMonitor(plugin, tpsMonitor, cacheManager);

        // Initialize other managers
        this.combatManager = new CombatManager(plugin, combatLogger, null, performanceMonitor, cacheManager, configManager);
        this.visualManager = new VisualManager(plugin, configManager);
        this.restrictionManager = new RestrictionManager((CombatManager) combatManager, cacheManager);

        plugin.getLogger().info("Plugin managers initialized successfully.");
    }

    /**
     * Registers all event listeners.
     */
    public void registerEvents() {
        // Initialize shared components for event listener
        CacheManager cacheManager = new CacheManager(plugin);
        CombatLogger combatLogger = new CombatLogger(plugin);
        TPSMonitor tpsMonitor = new TPSMonitor(plugin);
        PerformanceMonitor performanceMonitor = new PerformanceMonitor(plugin, tpsMonitor, cacheManager);

        // Register CombatEventListener
        CombatEventListener combatListener = new CombatEventListener(
            plugin, 
            (CombatManager) combatManager, 
            combatTracker, 
            new AntiInterferenceManager(plugin, combatManager),
            (RestrictionManager) restrictionManager,
            combatLogger,
            performanceMonitor,
            cacheManager
        );
        Bukkit.getPluginManager().registerEvents(combatListener, plugin);
        
        // Register SafeZoneManager
        com.muzlik.pvpcombat.restrictions.SafeZoneManager safeZoneManager = 
            new com.muzlik.pvpcombat.restrictions.SafeZoneManager(plugin, combatManager);
        Bukkit.getPluginManager().registerEvents(safeZoneManager, plugin);
        
        plugin.getLogger().info("Event listeners registered.");
    }

    /**
     * Registers all commands.
     */
    public void registerCommands() {
        // Register main combat command
        CombatCommand combatCommand = new CombatCommand(plugin);
        if (plugin.getCommand("combat") != null) {
            plugin.getCommand("combat").setExecutor(combatCommand);
            plugin.getCommand("combat").setTabCompleter(combatCommand);
        } else {
            plugin.getLogger().warning("Combat command not found in plugin.yml!");
        }

        // Register combatadmin command with dedicated AdminCommand executor
        if (plugin.getCommand("combatadmin") != null) {
            AdminCommand adminCommand = new AdminCommand(plugin);
            plugin.getCommand("combatadmin").setExecutor(adminCommand);
            plugin.getCommand("combatadmin").setTabCompleter(adminCommand);
        } else {
            plugin.getLogger().warning("Combatadmin command not found in plugin.yml!");
        }

        // Register replay command with dedicated ReplayCommand executor
        if (plugin.getCommand("replay") != null) {
            CombatLogger combatLogger = new CombatLogger(plugin);
            ReplayCommand replayCommand = new ReplayCommand(plugin, combatLogger);
            plugin.getCommand("replay").setExecutor(replayCommand);
            plugin.getCommand("replay").setTabCompleter(replayCommand);
        } else {
            plugin.getLogger().warning("Replay command not found in plugin.yml!");
        }

        plugin.getLogger().info("Commands registered successfully.");
    }

    /**
     * Shuts down all subsystems.
     */
    public void shutdown() {
        plugin.getLogger().info("Plugin subsystems shut down.");
    }

    // Getters for managers
    public ICombatManager getCombatManager() {
        return combatManager;
    }

    public CombatTracker getCombatTracker() {
        return combatTracker;
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
}