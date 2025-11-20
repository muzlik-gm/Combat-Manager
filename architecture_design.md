# PvP Combat System Plugin - Architecture Design Document

## Overview
This document outlines the high-level architectural design for the PvP Combat System plugin based on the technical requirements. The design follows Minecraft plugin best practices including proper event handling, thread safety, and modularity.

## Main Package Structure
```
com.muzlik.pvpcombat
├── core/
│   ├── PvPCombatPlugin.java          # Main plugin class
│   ├── CombatManager.java            # Singleton managing all combat sessions
│   └── LagCompensator.java           # TPS-aware adjustments
├── combat/
│   ├── CombatSession.java            # Individual combat instance
│   ├── CombatState.java              # Enum for combat states
│   ├── CombatLogger.java             # Event logging
│   └── CombatSummary.java            # Fight statistics
├── visual/
│   ├── ThemeManager.java             # Visual themes
│   ├── Theme.java                    # Theme data class
│   ├── BossBarManager.java           # Bossbar handling
│   └── ActionBarManager.java         # Action bar messages
├── restrictions/
│   ├── MovementRestrictor.java       # Ender pearl/elytra restrictions
│   ├── InterferenceDetector.java     # Anti-interference logic
│   └── RestrictionValidator.java     # Validation utilities
├── events/
│   ├── CombatEventListener.java      # Main event listener
│   ├── CombatEvent.java              # Base event class
│   ├── CombatStartEvent.java         # Combat initiation
│   ├── CombatEndEvent.java           # Combat termination
│   └── InterferenceEvent.java        # Third-party interference
├── config/
│   ├── ConfigManager.java            # Configuration loading/validation
│   ├── CombatConfig.java             # Combat settings
│   └── ThemeConfig.java              # Visual theme configurations
├── data/
│   ├── PlayerCombatData.java         # Player-specific combat data
│   ├── HitEvent.java                 # Individual hit data
│   ├── EventReplay.java              # Replay timeline
│   └── CombatStatistics.java         # Aggregated stats
├── commands/
│   ├── CombatCommand.java            # Base command class
│   ├── CombatStatusCommand.java      # /combat status
│   └── CombatAdminCommand.java       # Admin commands
├── integration/
│   ├── PlaceholderAPIHook.java       # PlaceholderAPI integration
│   ├── NetworkMessenger.java         # Cross-server sync
│   └── PluginIntegration.java        # Other plugin hooks
├── utils/
│   ├── AsyncTaskManager.java         # Async task handling
│   ├── CacheManager.java             # Caching utilities
│   └── ValidationUtils.java          # Input validation
└── interfaces/
    ├── CombatHandler.java            # Combat lifecycle interface
    ├── VisualSystem.java             # Visual customization interface
    ├── EventLogger.java              # Logging interface
    └── Configurable.java             # Configuration interface
```

## Key Classes and Responsibilities

### Core Classes
- **PvPCombatPlugin**: Main plugin class handling initialization, shutdown, and lifecycle management
- **CombatManager**: Thread-safe singleton managing all active combat sessions, session lifecycle, and global combat state
- **LagCompensator**: Monitors server TPS and adjusts combat timers for lag compensation

### Combat Classes
- **CombatSession**: Represents a single combat instance with timer, bossbar, and participant tracking
- **CombatLogger**: Records all combat events with timestamps and damage data
- **CombatSummary**: Generates post-combat statistics and formatted summaries

### Visual Classes
- **ThemeManager**: Loads and applies visual themes for bossbars and messages
- **BossBarManager**: Handles bossbar creation, updates, and cleanup
- **ActionBarManager**: Manages action bar messages and formatting

### Restriction Classes
- **MovementRestrictor**: Enforces ender pearl and elytra restrictions during combat
- **InterferenceDetector**: Detects and handles third-party interference in ongoing combats

### Event Classes
- **CombatEventListener**: Main event listener handling all combat-related events
- **CombatEvent**: Base class for custom combat events
- **CombatStartEvent**: Fired when combat begins
- **CombatEndEvent**: Fired when combat ends
- **InterferenceEvent**: Fired when interference is detected

### Configuration Classes
- **ConfigManager**: Loads, validates, and manages all configuration files
- **CombatConfig**: Strongly-typed combat settings
- **ThemeConfig**: Visual theme configurations

### Data Classes
- **PlayerCombatData**: Stores player-specific combat statistics and preferences
- **HitEvent**: Records individual hits with damage, accuracy, and timing
- **EventReplay**: Maintains a rolling buffer of recent combat events for replay
- **CombatStatistics**: Aggregated combat statistics for analysis

### Command Classes
- **CombatCommand**: Base command class with common functionality
- **CombatStatusCommand**: Handles player combat status queries
- **CombatAdminCommand**: Administrative commands for debugging and management

### Integration Classes
- **PlaceholderAPIHook**: Provides placeholders for other plugins
- **NetworkMessenger**: Handles cross-server combat synchronization
- **PluginIntegration**: Manages hooks with other combat-related plugins

### Utility Classes
- **AsyncTaskManager**: Manages asynchronous tasks for performance
- **CacheManager**: Provides caching for frequently accessed data
- **ValidationUtils**: Input validation and sanitization utilities

## Interfaces for Modularity

### CombatHandler
```java
public interface CombatHandler {
    void onCombatStart(CombatSession session);
    void onCombatEnd(CombatSession session);
    void onCombatTick(CombatSession session);
    boolean canStartCombat(Player attacker, Player defender);
    void handleInterference(Player interferer, Player target);
}
```

### VisualSystem
```java
public interface VisualSystem {
    void displayBossBar(CombatSession session);
    void sendActionBar(Player player, String message);
    void playSound(Player player, Sound sound);
    void clearVisuals(Player player);
}
```

### EventLogger
```java
public interface EventLogger {
    void logCombatEvent(CombatEvent event);
    void logHitEvent(HitEvent event);
    List<CombatEvent> getRecentEvents(int count);
    CombatSummary generateSummary(UUID sessionId);
}
```

### Configurable
```java
public interface Configurable {
    void loadConfig(ConfigurationSection config);
    void saveConfig(ConfigurationSection config);
    boolean validateConfig();
    void reloadConfig();
}
```

## Data Models

### CombatState
```java
public enum CombatState {
    NOT_IN_COMBAT,
    ACTIVE_COMBAT,
    COOLDOWN,
    INTERFERENCE_DETECTED
}
```

### PlayerCombatData
```java
public class PlayerCombatData {
    private UUID playerId;
    private int totalCombats;
    private int wins;
    private int losses;
    private long totalCombatTime;
    private double totalDamageDealt;
    private double totalDamageReceived;
    private Map<String, Integer> weaponUsage;
    private LocalDateTime lastCombat;
}
```

### CombatEvent
```java
public abstract class CombatEvent {
    protected UUID sessionId;
    protected UUID playerId;
    protected long timestamp;
    protected String eventType;

    public CombatEvent(UUID sessionId, UUID playerId, String eventType) {
        this.sessionId = sessionId;
        this.playerId = playerId;
        this.timestamp = System.currentTimeMillis();
        this.eventType = eventType;
    }
}
```

### HitEvent (extends CombatEvent)
```java
public class HitEvent extends CombatEvent {
    private double damage;
    private boolean critical;
    private String weaponType;
    private Location hitLocation;

    public HitEvent(UUID sessionId, UUID playerId, double damage,
                   boolean critical, String weaponType, Location hitLocation) {
        super(sessionId, playerId, "HIT");
        this.damage = damage;
        this.critical = critical;
        this.weaponType = weaponType;
        this.hitLocation = hitLocation;
    }
}
```

## Event Listeners and Handlers

### Main Event Listener Structure
```java
@EventHandler(priority = EventPriority.NORMAL)
public class CombatEventListener implements Listener {

    private final CombatManager combatManager;
    private final InterferenceDetector interferenceDetector;
    private final MovementRestrictor movementRestrictor;

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // Handle combat initiation, timer reset, and interference detection
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // End combat sessions for disconnecting players
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // End combat and generate summary
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Handle ender pearl restrictions
    }

    @EventHandler
    public void onEntityToggleGlide(EntityToggleGlideEvent event) {
        // Handle elytra restrictions
    }
}
```

### Custom Event Structure
- **CombatStartEvent**: Fired when players enter combat
- **CombatEndEvent**: Fired when combat timer expires or players die/disconnect
- **CombatTickEvent**: Fired every second during active combat
- **InterferenceEvent**: Fired when third-party interference is detected

## Configuration Management Structure

### ConfigManager Architecture
```java
public class ConfigManager implements Configurable {
    private FileConfiguration config;
    private CombatConfig combatConfig;
    private ThemeConfig themeConfig;
    private Map<String, Configurable> configSections;

    public void loadAllConfigs() {
        // Load main config.yml
        // Load themes.yml
        // Validate all configurations
        // Initialize config sections
    }

    public void reloadAllConfigs() {
        // Hot-reload configurations
        // Validate new configs
        // Update running instances
    }
}
```

### Configuration Files Structure
- **config.yml**: Main plugin configuration
  - Combat settings (timers, resets, etc.)
  - Visual settings (bossbars, actionbars)
  - Restriction settings (pearls, elytra)
  - Performance settings (max sessions, cleanup intervals)

- **themes.yml**: Visual theme definitions
  - Multiple theme configurations
  - Bossbar colors, titles, styles
  - Sound effects
  - Message formats

- **messages.yml**: Localized messages
  - Combat status messages
  - Error messages
  - Admin notifications

## Performance Considerations

### Asynchronous Operations
- **Bossbar Updates**: Run on async scheduler every 20 ticks
- **Event Logging**: Write to files asynchronously
- **Statistics Calculation**: Process summaries in background threads
- **Network Sync**: Send cross-server packets asynchronously

### Caching Strategy
- **Combat Sessions**: Cache frequently accessed sessions in memory
- **Player Data**: Cache player statistics with TTL
- **Configuration**: Cache parsed config values
- **Themes**: Pre-load and cache theme objects

### Memory Management
- **Session Limits**: Maximum concurrent combat sessions (configurable)
- **Cleanup Tasks**: Scheduled removal of inactive sessions
- **Event Buffers**: Fixed-size queues for replay events
- **Object Pooling**: Reuse common objects (HitEvent, CombatEvent)

### Thread Safety
- **CombatManager**: Use ConcurrentHashMap for session storage
- **Event Handling**: Ensure all event handlers are thread-safe
- **Async Tasks**: Proper synchronization for shared state
- **Configuration**: Atomic updates for config changes

## Integration Points

### PlaceholderAPI Integration
```java
public class PlaceholderAPIHook implements PlaceholderExpansion {
    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        switch (identifier) {
            case "combat_status":
                return getCombatStatus(player);
            case "combat_time_left":
                return getTimeLeft(player);
            case "combat_opponent":
                return getOpponentName(player);
            default:
                return null;
        }
    }
}
```

### BungeeCord/Velocity Integration
```java
public class NetworkMessenger {
    private final PluginMessageListener messageListener;

    public void sendCombatStart(UUID playerId, UUID opponentId) {
        // Send combat start packet to all servers
    }

    public void sendCombatEnd(UUID sessionId) {
        // Send combat end packet to all servers
    }

    public void syncPlayerData(UUID playerId, PlayerCombatData data) {
        // Sync player statistics across servers
    }
}
```

### Plugin Hooks
- **WorldGuard**: Region-specific combat rules
- **Citizens**: NPC combat handling
- **CombatLogX**: Compatibility layer
- **MythicMobs**: Custom mob combat behavior

## UML-like Class Diagrams

### Core Architecture Diagram
```
[CombatManager] <--> [CombatSession]
     |                    |
     |                    | -- [BossBar]
     |                    | -- [TimerTask]
     |                    | -- [CombatLogger]
     v                    v
[CombatEventListener] --> [Event Handlers]
     ^
     |
[InterferenceDetector] --> [MovementRestrictor]
```

### Data Flow Diagram
```
Player Damage Event
        |
        v
[CombatEventListener.onEntityDamage]
        |
        +--> Check Interference
        |       |
        |       v
        |   [InterferenceDetector]
        |       |
        |       +--> Notify/BLOCK
        |
        +--> Start Combat
                |
                v
        [CombatManager.startCombat]
                |
                +--> Create Session
                |       |
                |       v
                |   [CombatSession]
                |       |
                |       +--> [BossBarManager]
                |       +--> [ActionBarManager]
                |
                +--> Schedule Timer
                |       |
                |       v
                |   [TimerTask] --> Update BossBar
                |
                +--> Log Event
                        |
                        v
                [CombatLogger.logEvent]
```

### Configuration Architecture
```
[ConfigManager]
     |
     +--> [CombatConfig]
     +--> [ThemeConfig]
     +--> [MessageConfig]
     |
     v
File System
     |
     +--> config.yml
     +--> themes.yml
     +--> messages.yml
```

### Event Flow Diagram
```
Combat Start
     |
     v
[CombatStartEvent] --> Custom Listeners
     |
     +--> Display BossBar
     +--> Send Action Bar
     +--> Play Sound
     |
     v
Combat Active (Timer Running)
     |
     +--> Hit Events --> Reset Timer
     |       |
     |       +--> Log Hit
     |       +--> Update Stats
     |
     +--> Tick Events --> Update BossBar
     |
     v
Combat End
     |
     v
[CombatEndEvent] --> Custom Listeners
     |
     +--> Clear Visuals
     +--> Generate Summary
     +--> Save Statistics
```

This architecture ensures modularity, performance, and maintainability while following Minecraft plugin development best practices. The design allows for easy extension and integration with other plugins while maintaining thread safety and proper resource management.