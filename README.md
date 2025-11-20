# PvP Combat System

A comprehensive PvP combat management plugin for Minecraft servers that provides advanced combat logging, movement restrictions, visual feedback systems, and comprehensive administrative tools.

## Features

### Core Combat System
- **Advanced Combat Detection**: Automatically detects combat situations based on damage dealt/received with configurable thresholds
- **Combat Logging Prevention**: Prevents players from disconnecting during combat with customizable penalties
- **Dynamic Combat Timers**: Combat duration extends on damage with lag-aware adjustments
- **Multi-Player Combat**: Supports 1v1, group combat, and interference detection

### Movement & Interaction Restrictions
- **Ender Pearl Restrictions**: Blocks pearl usage during combat with configurable cooldown multipliers
- **Elytra Restrictions**: Prevents gliding, boosting, and takeoff during combat with world-specific settings
- **Teleportation Blocks**: Prevents command-based teleportation (/tp, /warp, /home) during combat
- **Inventory Management**: Prevents inventory interactions during combat (configurable)

### Visual Feedback Systems
- **Boss Bar Display**: Real-time combat timer with customizable themes (Fire, Ice, Neon, etc.)
- **Action Bar Messages**: Dynamic messages showing opponent, health, and time remaining
- **Sound Effects**: Themed audio cues for combat events with multiple profiles
- **Message Formatting**: Customizable combat notifications with HEX color support and placeholders

### Administrative Tools
- **Real-time Inspection**: Admin GUI for monitoring active combat sessions
- **Combat Replay System**: Detailed replay functionality with timeline buffering
- **Statistics Dashboard**: Comprehensive combat analytics and performance metrics
- **Debug Mode**: Advanced debugging tools for troubleshooting

### Integration & Extensibility
- **PlaceholderAPI Support**: Extensive placeholder system for other plugins
- **Cross-Server Sync**: BungeeCord/Velocity integration for network-wide combat tracking
- **API-First Design**: Comprehensive developer API for custom integrations
- **Event-Driven Architecture**: Rich event system for plugin interoperability

### Performance & Reliability
- **Asynchronous Processing**: Non-blocking event handling and data processing
- **Lag Compensation**: TPS-aware timer adjustments during server lag
- **Memory Management**: TTL-based caching with automatic cleanup
- **Thread Safety**: Concurrent data structures for multi-threaded environments

## Installation & Setup

### System Requirements
- **Minecraft Server**: Spigot/Paper 1.20.4 or higher
- **Java Runtime**: Java 17 or higher (Java 21 recommended for optimal performance)
- **Memory**: Minimum 4GB RAM allocated to server
- **Storage**: ~50MB for plugin files and logs

### Installation Steps

1. **Download**: Get the latest release JAR from [GitHub Releases](https://github.com/muzlik/pvp-combat/releases)
2. **Placement**: Place the JAR file in your server's `plugins/` directory
3. **Server Restart**: Restart your server completely (not just /reload)
4. **First Run**: The plugin will generate default configuration files
5. **Configuration**: Edit `config.yml` and `messages.yml` in `plugins/PvPCombat/`
6. **Permissions**: Set up permissions using your permission plugin (LuckPerms recommended)
7. **Testing**: Run `/combat status` to verify installation

### Dependencies

#### Required
- **Server Software**: Spigot, Paper, or Purpur 1.20.4+
- **Java Version**: 17+ (21+ recommended)

#### Optional Integrations
- **PlaceholderAPI** (5.0+): Enhanced placeholder support
- **ProtocolLib** (5.0+): Advanced packet handling for anti-cheat features
- **WorldGuard** (7.0+): Region-based combat rule overrides
- **Citizens** (2.0+): NPC combat behavior integration
- **Vault** (1.7+): Economy integration for combat rewards/penalties
- **BungeeCord/Velocity**: Cross-server combat synchronization
- **DiscordSRV** (1.26+): Discord integration for combat notifications

#### Compatibility Notes
- **CombatLogX**: Compatible with CombatLogX v11+ (may require configuration adjustments)
- **MythicMobs**: Full support for custom mob combat behavior
- **Towny/GriefPrevention**: Respects protected regions automatically
- **Essentials**: Compatible with teleport commands and features

## Configuration Guide

The plugin uses a comprehensive configuration system split across multiple files for better organization and maintainability.

### Configuration Files Overview

#### `config.yml` - Main Configuration
The primary configuration file containing all core settings organized by category:

```yaml
# General Settings
general:
  enabled: true
  debug-mode: false
  language: "en"

# Combat Settings
combat:
  duration: 30          # Combat timer duration in seconds
  cooldown: 10          # Post-combat cooldown period
  max-sessions: 100     # Maximum concurrent combat sessions

# Visual Settings
visual:
  themes:
    default-theme: "clean"
    available: ["minimal", "fire", "ice", "neon"]

# Performance Settings
performance:
  lag:
    enabled: true
    tps-threshold: 18.0  # Lag detection threshold
```

#### `messages.yml` - Localization
Contains all user-facing messages, notifications, and UI text. Supports Minecraft color codes and placeholders:

```yaml
combat:
  started: "&c⚔ Combat started against &f{opponent}&c!"
  ended: "&a✓ Combat ended safely"
  time-remaining: "&e{time_left}s remaining"

bossbar:
  default: "&cCombat: &f{time_left}s"
  warning: "&4⚠ &c{time_left}s"
```

#### Auto-Generated Files
- `themes.yml`: Visual theme definitions (generated from config)
- `statistics.yml`: Server-wide combat statistics (auto-updated)
- `logs/`: Combat event logs and replay data

### Quick Start Configuration

For a basic setup, modify these key settings in `config.yml`:

```yaml
# Basic combat settings
combat:
  duration: 30
  cooldown: 10

# Enable core features
restrictions:
  enderpearl:
    enabled: true
    cooldown: 10
  elytra:
    enabled: true
    block-glide: true

# Visual feedback
visual:
  bossbar:
    enabled: true
  actionbar:
    enabled: true
  sounds:
    enabled: true
```

### Advanced Configuration Options

#### Combat Detection Settings
```yaml
combat:
  damage:
    reset-on-damage: true      # Extend timer on any damage
    min-damage-trigger: 0.5    # Minimum damage to trigger combat
  duration: 30                  # Base combat duration
  cooldown: 10                  # Safety cooldown after combat
```

#### Movement Restrictions
```yaml
restrictions:
  enderpearl:
    enabled: true
    cooldown: 10                # Base cooldown in seconds
    combat-cooldown-multiplier: 2.0  # Multiplier during combat
    block-usage: false          # Completely block during combat

  elytra:
    enabled: true
    block-glide: true          # Prevent gliding
    block-boosting: true       # Prevent firework boosting
    block-takeoff: true        # Prevent takeoff
    min-safe-height: 10        # Minimum height for restrictions
    boost-cooldown: 30         # Cooldown between boosts

  teleport:
    enabled: true
    blocked-commands:          # Commands that trigger teleport check
      - "/tp"
      - "/teleport"
      - "/warp"
      - "/home"
      - "/spawn"
```

#### Performance Tuning
```yaml
performance:
  lag:
    enabled: true
    tps-threshold: 18.0         # TPS below this = lag mode
    base-extension-seconds: 5   # Base seconds to add during lag
    extension-multiplier: 1.5   # Severity multiplier

  async:
    enabled: true
    thread-pool-size: 4

  cache:
    player-data-ttl: 30         # Minutes to cache player data
    cleanup-interval: 60        # Seconds between cleanup runs
```

### World-Specific Settings

Configure different rules per world:

```yaml
restrictions:
  elytra:
    worlds:
      world_nether:
        block-glide: false      # Allow gliding in Nether
        min-safe-height: 20     # Higher minimum height
      world_end:
        block-glide: true       # Strict rules in End
        min-safe-height: 5      # Lower minimum height
```

### Integration Configuration

#### PlaceholderAPI
```yaml
integration:
  placeholderapi:
    enabled: true
```

#### Cross-Server Sync
```yaml
integration:
  cross-server-sync:
    enabled: false              # Set to true for BungeeCord/Velocity
    platform: "AUTO"           # BUNGEE, VELOCITY, or AUTO
    channel: "pvpcombat:sync"  # Plugin messaging channel
    broadcast:
      enabled: false
      format: "&6[Network] &e{attacker} &fis now in combat with &e{defender}"
```

### Configuration Validation

The plugin includes built-in configuration validation that will:
- Warn about deprecated settings
- Suggest optimal values for performance
- Detect configuration conflicts
- Provide migration guidance for major updates

Run `/combat reload` after configuration changes to validate and apply settings.

## Commands Reference

### Main Command: `/combat`

The primary command interface supporting both player and admin functions with context-aware permissions.

#### Player Commands

##### `/combat status`
Shows your current combat status and opponent information.
```
Permission: pvpcombat.command.status (default: true)
Usage: /combat status
Output: Current combat state, time remaining, opponent details
```

##### `/combat summary`
Displays detailed statistics from your most recent combat session.
```
Permission: pvpcombat.command.summary (default: true)
Usage: /combat summary
Output: Hits traded, damage dealt/taken, duration, winner
```

##### `/combat toggle-style`
Cycles through available visual themes for boss bars and messages.
```
Permission: pvpcombat.command.toggle-style (default: true)
Usage: /combat toggle-style
Output: Confirmation of style change with available options
```

#### Admin Commands

##### `/combat inspect <player>`
Real-time inspection of a player's combat status and statistics.
```
Permission: pvpcombat.admin.inspect (default: op)
Usage: /combat inspect <player>
Output: Live combat data, opponent info, session statistics
Notes: Range-limited to prevent abuse (default: 50 blocks)
```

##### `/combat summary <player>`
View the last combat summary for any player (admin version).
```
Permission: pvpcombat.admin (default: op)
Usage: /combat summary <player>
Output: Player's combat history and statistics
```

##### `/combat reload`
Reload all configuration files with validation.
```
Permission: pvpcombat.admin (default: op)
Usage: /combat reload
Output: Reload confirmation with validation results
```

##### `/combat debug`
Toggle debug mode for enhanced logging and diagnostics.
```
Permission: pvpcombat.admin.debug (default: op)
Usage: /combat debug
Output: Debug mode status toggle confirmation
```

### Replay Commands: `/replay`

Administrative commands for viewing and managing combat replays.

#### `/replay view <session-uuid>`
Load and display a combat replay for analysis.
```
Permission: pvpcombat.admin (default: op)
Usage: /replay view 123e4567-e89b-12d3-a456-426614174000
Output: Replay data loading confirmation (GUI implementation pending)
Notes: Session UUIDs found in combat logs or admin summaries
```

#### `/replay stats`
Display system-wide replay statistics and performance metrics.
```
Permission: pvpcombat.admin (default: op)
Usage: /replay stats
Output: Active sessions, memory usage, cached replays count
```

#### `/replay clear <session-uuid>`
Remove replay data for a specific combat session.
```
Permission: pvpcombat.admin (default: op)
Usage: /replay clear 123e4567-e89b-12d3-a456-426614174000
Output: Data clearing confirmation (implementation pending)
```

### Command Aliases

- `/pvp` → `/combat` (primary alias)
- `/combatlog` → `/combat` (legacy alias)
- `/cl` → `/combat` (short alias)

### Tab Completion

All commands support intelligent tab completion:
- **Player names**: Auto-complete online player names for admin commands
- **Subcommands**: Context-aware subcommand suggestions based on permissions
- **UUIDs**: Session UUID suggestions for replay commands (future feature)
- **Performance**: Limited to 10 suggestions to prevent lag

### Permission System

#### Player Permissions
```
pvpcombat.command.status          # View combat status
pvpcombat.command.summary         # View combat summaries
pvpcombat.command.toggle-style    # Change visual themes
pvpcombat.use                     # Basic plugin usage (auto-granted)
```

#### Admin Permissions
```
pvpcombat.admin                   # Full admin access
pvpcombat.admin.inspect          # Player inspection tools
pvpcombat.admin.debug            # Debug mode access
pvpcombat.admin.replay           # Replay system access
```

#### Bypass Permissions
```
pvpcombat.bypass.combatlog       # Immune to combat logging penalties
pvpcombat.bypass.restrictions    # Ignore movement restrictions
pvpcombat.bypass.timer          # Combat timer doesn't apply
pvpcombat.bypass.all            # Complete immunity (dangerous!)
```

### Command Examples

#### Basic Player Usage
```
/combat status          # Check if in combat
/combat summary         # View last fight stats
/combat toggle-style    # Change visual theme
```

#### Admin Investigation
```
/combat inspect Steve   # Check Steve's combat status
/combat summary Alex    # View Alex's last fight
/combat debug           # Enable debug logging
```

#### System Management
```
/combat reload          # Apply configuration changes
/replay stats           # Check replay system health
/replay view <uuid>     # Analyze specific combat session
```

## Permissions

- `pvpcombat.command.combat` - Access to basic combat commands (default: true)
- `pvpcombat.admin` - Access to administrative commands (default: op)
- `pvpcombat.bypass.combatlog` - Bypass combat logging restrictions (default: op)
- `pvpcombat.bypass.restrictions` - Bypass movement restrictions (default: op)

## Features & Capabilities

### Combat Detection Engine

#### Smart Combat Detection
- **Damage-Based Triggering**: Combat starts when players deal/receive damage above configurable thresholds
- **Multi-Target Support**: Handles 1v1, group combat, and interference scenarios
- **Environmental Awareness**: Considers fall damage, lava, and environmental hazards
- **Custom Damage Sources**: Support for custom damage types from other plugins

#### Advanced Timer System
- **Dynamic Extension**: Combat timer extends on continued damage with exponential backoff
- **Lag Compensation**: TPS-aware adjustments prevent unfair extensions during server lag
- **Grace Periods**: Configurable cooldown periods after combat ends
- **Session Limits**: Prevents server overload with maximum concurrent session limits

### Visual Feedback Systems

#### Boss Bar Integration
Real-time combat timer display with customizable themes:

**Available Themes:**
- **Minimal**: Clean, text-only display
- **Fire**: 🔥 Red theme with flame effects
- **Ice**: ❄ Blue theme with frost effects
- **Neon**: ✨ Bright, glowing effects
- **Dark**: Shadow theme for serious servers
- **Clean**: Default theme with subtle styling

**Configuration Example:**
```yaml
visual:
  bossbar:
    enabled: true
    update-interval: 1
    formats:
      fire: "&c🔥 &f{time_left}s &c🔥"
      ice: "&b❄ &f{time_left}s &b❄"
```

#### Action Bar Messages
Dynamic messages showing combat status with rich formatting:

**Message Styles:**
- **Detailed**: Shows opponent, health, and time remaining
- **Minimal**: Simple time remaining display
- **Funny**: Themed messages with emojis and humor
- **Competitive**: Serious, tournament-style messages
- **Medieval**: Roleplay-friendly language

#### Sound System
Audio feedback for combat events with multiple profiles:

**Sound Profiles:**
- **Default**: Standard Minecraft sounds
- **Subtle**: Quieter, less intrusive sounds
- **Intense**: Dramatic, high-impact audio
- **Calm**: Peaceful, ambient sounds
- **Electronic**: Tech-themed sound effects
- **Clean**: Minimal UI sounds

### Restriction Management

#### Ender Pearl Control
- **Cooldown Management**: Base cooldowns with combat multipliers
- **Usage Blocking**: Complete prevention during combat
- **World-Specific Rules**: Different rules per world/dimension

#### Elytra Restrictions
- **Flight Prevention**: Block takeoff, gliding, and boosting
- **Height-Based Rules**: Altitude-dependent restrictions
- **Time-Based Overrides**: Day/night specific rules
- **World Variations**: Custom rules for different environments

#### Teleportation Blocks
- **Command Interception**: Blocks /tp, /warp, /home, /spawn commands
- **Plugin Compatibility**: Works with Essentials, CMI, and other teleport plugins
- **Emergency Overrides**: Admin bypass for critical situations

### Statistics & Analytics

#### Real-Time Metrics
- **Hit Tracking**: Accuracy, critical hits, combo tracking
- **Damage Analytics**: DPS, damage distribution, weapon usage
- **Session Statistics**: Duration, participants, outcomes
- **Performance Metrics**: Response times, memory usage

#### Historical Data
- **Player Profiles**: Win/loss ratios, favorite weapons, play styles
- **Server Analytics**: Peak combat times, popular arenas, lag correlations
- **Trend Analysis**: Combat frequency, duration changes, rule effectiveness

### Anti-Interference System

#### Detection Algorithms
- **Third-Party Damage**: Identifies unauthorized damage sources
- **Pattern Recognition**: Learns legitimate combat patterns
- **Threshold Management**: Configurable interference tolerance
- **False Positive Prevention**: Advanced filtering algorithms

#### Response Options
- **Notification Only**: Alert admins without blocking
- **Damage Blocking**: Prevent interfering hits
- **Session Termination**: End combat sessions with interference
- **Penalties**: Automatic penalties for interfering players

### Performance Optimization

#### Asynchronous Processing
- **Event Handling**: Non-blocking combat event processing
- **Data Persistence**: Background saving of statistics and logs
- **Cache Management**: TTL-based player data caching
- **Thread Pools**: Configurable thread pools for intensive operations

#### Memory Management
- **Session Cleanup**: Automatic removal of expired combat sessions
- **Buffer Limits**: Configurable limits on event history and replay data
- **Garbage Collection**: Optimized object lifecycle management

### Integration Ecosystem

#### PlaceholderAPI Support
Comprehensive placeholder system for other plugins:

**Combat Status Placeholders:**
```
%pvpcombat_status%              # "In Combat" or "Safe"
%pvpcombat_time_left%           # Time remaining (seconds)
%pvpcombat_opponent%            # Current opponent name
%pvpcombat_duration%            # Total combat duration
%pvpcombat_session_id%          # Current session UUID
```

**Statistics Placeholders:**
```
%pvpcombat_total_fights%         # Total fights participated in
%pvpcombat_win_rate%            # Win/loss percentage
%pvpcombat_favorite_weapon%     # Most used weapon
%pvpcombat_avg_combat_time%     # Average fight duration
%pvpcombat_accuracy%            # Overall hit accuracy
```

**Advanced Placeholders:**
```
%pvpcombat_health_self%         # Player's current health
%pvpcombat_health_opponent%     # Opponent's current health
%pvpcombat_damage_dealt%        # Damage dealt this fight
%pvpcombat_damage_taken%        # Damage taken this fight
%pvpcombat_combo_count%         # Current hit combo
```

#### Third-Party Integrations

**Economy Plugins (Vault):**
- Combat rewards and penalties
- Tournament prize pools
- Anti-combat-logging fines

**Permission Plugins:**
- Temporary permission grants during combat
- Combat-mode specific permissions
- Admin permission overrides

**Chat Plugins:**
- Combat status in chat formatting
- Anti-spam measures during combat
- Combat announcements

**Scoreboard Plugins:**
- Live combat statistics
- Leaderboards and rankings
- Tournament brackets

### Cross-Server Features

#### BungeeCord/Velocity Integration
- **Combat State Sync**: Maintain combat status across servers
- **Session Migration**: Seamless combat continuation when switching servers
- **Global Statistics**: Network-wide combat leaderboards
- **Broadcast Notifications**: Cross-server combat announcements

#### Configuration Sync
- **Centralized Config**: Single configuration file for entire network
- **Server-Specific Overrides**: Per-server customization options
- **Live Updates**: Configuration changes propagate automatically

## Developer API Integration

The PvP Combat System provides a comprehensive, type-safe API designed for seamless integration with other plugins and custom server features.

### Getting Started

#### Maven Dependency
```xml
<repositories>
    <repository>
        <id>jitpack</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.muzlik</groupId>
        <artifactId>pvp-combat</artifactId>
        <version>1.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

#### Gradle Dependency
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.muzlik:pvp-combat:1.0.0'
}
```

### Core API Classes

#### Plugin Instance Access
```java
import com.muzlik.pvpcombat.core.PvPCombatPlugin;

public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        PvPCombatPlugin combatPlugin = PvPCombatPlugin.getInstance();
        if (combatPlugin != null) {
            // PvP Combat is available
            ICombatManager combatManager = combatPlugin.getCombatManager();
            IVisualManager visualManager = combatPlugin.getVisualManager();
        }
    }
}
```

#### Core Managers

**ICombatManager - Combat Session Management**
```java
public interface ICombatManager {
    // Check combat status
    boolean isInCombat(Player player);
    Player getOpponent(Player player);
    CombatSession getCombatSession(Player player);

    // Combat control
    void forceEndCombat(Player player);
    void forceStartCombat(Player attacker, Player defender);

    // Session management
    Collection<CombatSession> getActiveSessions();
    int getActiveSessionCount();
}
```

**IVisualManager - Visual Feedback Control**
```java
public interface IVisualManager {
    // Boss bar management
    void showBossBar(Player player, String theme);
    void hideBossBar(Player player);
    void updateBossBar(Player player);

    // Action bar messages
    void sendActionBar(Player player, String message);

    // Sound effects
    void playSound(Player player, SoundEvent event);
}
```

**IRestrictionManager - Movement Restrictions**
```java
public interface IRestrictionManager {
    // Check restrictions
    boolean canUseEnderPearl(Player player);
    boolean canGlide(Player player);
    boolean canTeleport(Player player);

    // Restriction control
    void applyRestriction(Player player, RestrictionType type);
    void removeRestriction(Player player, RestrictionType type);
}
```

### Event System

#### Combat Events
Listen to combat lifecycle events for integration:

```java
import com.muzlik.pvpcombat.events.CombatStartEvent;
import com.muzlik.pvpcombat.events.CombatEndEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CombatListener implements Listener {

    @EventHandler
    public void onCombatStart(CombatStartEvent event) {
        Player attacker = event.getAttacker();
        Player defender = event.getDefender();

        // Custom logic when combat starts
        getLogger().info(attacker.getName() + " entered combat with " + defender.getName());
    }

    @EventHandler
    public void onCombatEnd(CombatEndEvent event) {
        Player winner = event.getWinner();
        CombatEndEvent.CombatEndReason reason = event.getReason();

        // Handle combat end
        if (winner != null) {
            giveCombatReward(winner, event.getCombatSession());
        }
    }
}
```

#### Available Events

**CombatStartEvent**
- Fired when players enter combat
- Provides attacker, defender, and session information
- Cancelable to prevent combat

**CombatEndEvent**
- Fired when combat concludes
- Includes winner, reason, and full session statistics
- Provides access to combat summary data

**CombatTickEvent**
- Fired every second during active combat
- Useful for real-time integrations
- Contains current timer and health information

**InterferenceDetectedEvent**
- Fired when third-party interference is detected
- Includes interferer and target information
- Cancelable to allow/block interference

### Data Access APIs

#### Combat Statistics
```java
import com.muzlik.pvpcombat.data.PlayerStatistics;
import com.muzlik.pvpcombat.logging.CombatLogger;

// Get player statistics
PlayerStatistics stats = combatLogger.getPlayerStatistics(player.getUniqueId());
double winRate = stats.getWinRate();
String favoriteWeapon = stats.getFavoriteWeapon();

// Get combat summary
CombatSummary lastFight = combatLogger.getLastCombatSummary(player.getUniqueId());
if (lastFight != null) {
    int hitsLanded = lastFight.getHitsLanded();
    double damageDealt = lastFight.getDamageDealt();
}
```

#### Real-Time Combat Data
```java
import com.muzlik.pvpcombat.data.CombatSession;
import com.muzlik.pvpcombat.data.CombatStatistics;

// Access current combat session
CombatSession session = combatManager.getCombatSession(player);
if (session != null) {
    CombatStatistics stats = session.getStatistics();
    long duration = stats.getCombatDurationSeconds();
    double accuracy = stats.getAccuracy();
}
```

### Custom Implementation Interfaces

#### Custom Combat Handler
Implement your own combat detection logic:

```java
import com.muzlik.pvpcombat.interfaces.ICombatHandler;

public class CustomCombatHandler implements ICombatHandler {

    @Override
    public boolean shouldStartCombat(Player attacker, Player defender, double damage) {
        // Custom combat detection logic
        return damage > 1.0 && !isSameFaction(attacker, defender);
    }

    @Override
    public int getCombatDuration(Player player) {
        // Dynamic duration based on player level
        return player.hasPermission("vip") ? 45 : 30;
    }
}
```

#### Custom Visual System
Create custom visual feedback:

```java
import com.muzlik.pvpcombat.interfaces.IVisualSystem;

public class CustomVisualSystem implements IVisualSystem {

    @Override
    public void showCombatTimer(Player player, int secondsLeft) {
        // Custom boss bar implementation
        player.sendTitle("⚔ COMBAT ⚔", secondsLeft + "s remaining", 0, 20, 0);
    }

    @Override
    public void hideCombatTimer(Player player) {
        player.resetTitle();
    }
}
```

#### Custom Event Logger
Implement specialized logging:

```java
import com.muzlik.pvpcombat.logging.CombatLogEntry;
import com.muzlik.pvpcombat.interfaces.IEventLogger;

public class DatabaseLogger implements IEventLogger {

    @Override
    public void logEvent(CombatLogEntry entry) {
        // Save to custom database
        saveToDatabase(entry);
    }

    @Override
    public List<CombatLogEntry> getRecentEvents(Player player, int limit) {
        // Retrieve from database
        return queryDatabase(player.getUniqueId(), limit);
    }
}
```

### Advanced Integration Examples

#### Tournament Plugin Integration
```java
public class TournamentIntegration {

    private final PvPCombatPlugin combatPlugin;

    public void startTournamentMatch(Player player1, Player player2) {
        // Start controlled combat
        combatPlugin.getCombatManager().forceStartCombat(player1, player2);

        // Disable restrictions for tournament
        combatPlugin.getRestrictionManager().removeRestriction(player1, RestrictionType.ENDERPEARL);
        combatPlugin.getRestrictionManager().removeRestriction(player2, RestrictionType.ENDERPEARL);
    }

    @EventHandler
    public void onTournamentCombatEnd(CombatEndEvent event) {
        // Award tournament points
        Player winner = event.getWinner();
        if (winner != null) {
            awardTournamentPoints(winner, event.getCombatSession().getStatistics());
        }
    }
}
```

#### Economy Integration
```java
public class EconomyIntegration implements Listener {

    @EventHandler
    public void onCombatEnd(CombatEndEvent event) {
        Player winner = event.getWinner();
        Player loser = event.getLoser();

        if (winner != null && loser != null) {
            // Winner gets reward
            economy.depositPlayer(winner, 50.0);

            // Loser pays penalty
            economy.withdrawPlayer(loser, 25.0);

            // Announce results
            broadcastCombatResult(winner, loser, event.getCombatSession());
        }
    }
}
```

#### Custom Damage Tracking
```java
public class DamageTracker extends JavaPlugin implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        // Check if this is tracked combat
        CombatSession session = PvPCombatPlugin.getInstance()
            .getCombatManager().getCombatSession(attacker);

        if (session != null) {
            // Record custom damage data
            recordCustomDamage(attacker, victim, event.getDamage(), event.getCause());
        }
    }
}
```

### API Versioning & Compatibility

The API follows semantic versioning:
- **Major version**: Breaking changes
- **Minor version**: New features (backward compatible)
- **Patch version**: Bug fixes

#### Version Checking
```java
String apiVersion = PvPCombatPlugin.getInstance().getApiVersion();
if (apiVersion.startsWith("1.")) {
    // Compatible with v1.x API
} else {
    // Handle version incompatibility
    getLogger().warning("Incompatible PvP Combat API version: " + apiVersion);
}
```

### Best Practices

#### Error Handling
Always wrap API calls in try-catch blocks:
```java
try {
    CombatSession session = combatManager.getCombatSession(player);
    if (session != null) {
        // Use session data
    }
} catch (Exception e) {
    getLogger().warning("Failed to access combat session: " + e.getMessage());
}
```

#### Performance Considerations
- Cache frequently accessed data
- Use asynchronous operations for heavy processing
- Avoid blocking the main thread
- Implement proper cleanup in onDisable()

#### Thread Safety
- Most API methods are thread-safe
- Event handlers run on the main thread
- Use BukkitRunnable for scheduled tasks

### Migration Guide

#### From v0.x to v1.0
- Update package imports from `com.muzlik.pvpcombat` to new structure
- Replace deprecated methods with new interfaces
- Update event handler signatures
- Review configuration file locations

## Building from Source

### Prerequisites
- **Java Development Kit**: JDK 17 or higher (JDK 21 recommended)
- **Build Tool**: Maven 3.6+ or Gradle 7.0+
- **Git**: For cloning the repository
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code (recommended)

### Development Environment Setup

#### IntelliJ IDEA Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/muzlik/pvp-combat.git
   cd pvp-combat
   ```

2. Open in IntelliJ IDEA:
   - File → Open → Select the project directory
   - Let Maven/Gradle import the project
   - Ensure JDK 17+ is selected as the project SDK

3. Run Configuration:
   - Add new "Maven" run configuration
   - Command line: `clean package`
   - Working directory: `$PROJECT_DIR$`

#### VS Code Setup
1. Install required extensions:
   - Extension Pack for Java
   - Maven for Java
   - Gradle for Java

2. Open the project folder in VS Code

3. The Java extensions will automatically detect and configure the project

### Build Instructions

#### Using Maven (Recommended)
```bash
# Clone repository
git clone https://github.com/muzlik/pvp-combat.git
cd pvp-combat

# Build the project
mvn clean compile

# Run tests
mvn test

# Create JAR file
mvn package

# Install to local repository (for development)
mvn install
```

#### Using Gradle
```bash
# Clone repository
git clone https://github.com/muzlik/pvp-combat.git
cd pvp-combat

# Build the project
gradle build

# Run tests
gradle test

# Create JAR file
gradle jar
```

### Build Artifacts

After successful build, you'll find:

```
target/
├── pvpcombat-1.0.0.jar              # Main plugin JAR
├── pvpcombat-1.0.0-shaded.jar       # Shaded JAR with dependencies
├── original-pvpcombat-1.0.0.jar     # Original JAR without shading
└── test-classes/                     # Compiled test classes
```

#### Choosing the Right JAR
- **pvpcombat-1.0.0.jar**: Use for development/servers with manual dependency management
- **pvpcombat-1.0.0-shaded.jar**: Recommended for production servers (includes all dependencies)

### Development Workflow

#### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CombatManagerTest

# Run with verbose output
mvn test -DforkCount=1 -DreuseForks=false
```

#### Code Quality Checks
```bash
# Check code style
mvn checkstyle:check

# Generate code coverage report
mvn jacoco:report

# Run static analysis
mvn spotbugs:check
```

#### IDE Integration
- **Debugging**: Set breakpoints in your IDE and run the plugin through a test server
- **Hot Swapping**: Most IDEs support hot-swapping code changes during development
- **Test Running**: Run individual test methods directly from your IDE

### Contributing to Development

#### Code Style Guidelines
- Follow Google Java Style Guide
- Use 4 spaces for indentation (no tabs)
- Maximum line length: 120 characters
- Use meaningful variable and method names
- Add JavaDoc comments for all public APIs

#### Git Workflow
```bash
# Create feature branch
git checkout -b feature/your-feature-name

# Make changes and commit
git add .
git commit -m "Add: Your feature description"

# Push to your fork
git push origin feature/your-feature-name

# Create pull request on GitHub
```

#### Pull Request Guidelines
- Ensure all tests pass
- Update documentation if needed
- Add migration notes for breaking changes
- Follow conventional commit format

### Advanced Build Options

#### Custom Build Profiles
```xml
<!-- In pom.xml -->
<profiles>
    <profile>
        <id>development</id>
        <properties>
            <maven.compiler.debug>true</maven.compiler.debug>
        </properties>
    </profile>
</profiles>
```

Use with: `mvn package -Pdevelopment`

#### Building with Specific Java Version
```bash
# Force Java version
mvn package -Djava.version=21

# Use specific Maven version
mvn wrapper:wrapper -Dmaven=3.9.4
```

#### Creating Release Builds
```bash
# Full release build with all checks
mvn clean verify

# Deploy to repository (if configured)
mvn deploy
```

### Troubleshooting Build Issues

#### Common Build Problems

**"Java version error"**
```
Error: Java version mismatch
```
**Solution:** Ensure JAVA_HOME points to JDK 17+
```bash
java -version  # Should show Java 17+
echo $JAVA_HOME  # Should point to JDK installation
```

**"Dependency resolution failed"**
```
Could not resolve dependencies
```
**Solutions:**
- Clear Maven cache: `mvn dependency:purge-local-repository`
- Force refresh: `mvn clean compile -U`
- Check network connectivity to Maven Central

**"Build success but tests fail"**
- Run tests individually to identify issues
- Check test logs for specific failures
- Ensure test environment matches expectations

**"Out of memory during build"**
```bash
# Increase Maven memory
export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=512m"
mvn package
```

#### IDE-Specific Issues

**IntelliJ IDEA:**
- Invalidate caches: File → Invalidate Caches / Restart
- Reimport Maven project: Right-click pom.xml → Maven → Reimport
- Check JDK settings in Project Structure

**VS Code:**
- Reload window: Ctrl/Cmd + Shift + P → "Developer: Reload Window"
- Check Java language server logs
- Ensure all required extensions are installed

### Project Structure for Developers

```
src/main/java/com/muzlik/pvpcombat/
├── core/              # Plugin lifecycle and main classes
├── combat/            # Combat detection and management
├── visual/            # Visual feedback systems
├── restrictions/      # Movement and interaction restrictions
├── events/            # Event system and custom events
├── config/            # Configuration management system
├── data/              # Data models and business logic
├── commands/          # Command implementations
├── integration/       # Third-party plugin integrations
├── utils/             # Utility classes and helpers
├── interfaces/        # Public API interfaces
└── logging/           # Logging and statistics system

src/main/resources/
├── config.yml         # Default configuration
├── messages.yml       # Default messages
└── plugin.yml         # Plugin description

src/test/java/         # Unit and integration tests
src/test/resources/    # Test resources and data
```

## Architecture

The plugin follows a modular architecture with the following package structure:

```
com.muzlik.pvpcombat
├── core/          # Core plugin classes
├── combat/        # Combat session management
├── visual/        # Visual systems (bossbar, actionbar)
├── restrictions/  # Movement and interaction restrictions
├── events/        # Event handling and custom events
├── config/        # Configuration management
├── data/          # Data models and statistics
├── commands/      # Command implementations
├── integration/   # Third-party plugin integrations
├── utils/         # Utility classes
└── interfaces/    # API interfaces
```

## Troubleshooting Guide

### Common Issues & Solutions

#### Plugin Not Loading
**Symptoms:**
- Plugin doesn't appear in `/plugins` list
- Console shows "Could not load" errors
- Missing dependencies errors

**Solutions:**
1. **Check Java Version:**
   ```bash
   java -version  # Must be Java 17+
   ```
2. **Verify Server Software:**
   - Use Spigot, Paper, or Purpur 1.20.4+
   - Avoid CraftBukkit (incompatible)
3. **Dependencies:**
   - Ensure ProtocolLib is compatible (if used)
   - Check PlaceholderAPI version
4. **File Permissions:**
   - Ensure plugin JAR has read/execute permissions
   - Check server directory permissions

#### Combat Not Detecting
**Symptoms:**
- Players can hit each other without entering combat
- No boss bars or messages appear
- `/combat status` shows "not in combat"

**Solutions:**
1. **Check Configuration:**
   ```yaml
   combat:
     enabled: true
     damage:
       min-damage-trigger: 0.5  # Try lowering this
   ```
2. **Verify Permissions:**
   - Ensure players have `pvpcombat.use`
   - Check for conflicting plugins
3. **Debug Mode:**
   ```
   /combat debug  # Enable debug logging
   ```
4. **Plugin Conflicts:**
   - Disable other PvP-related plugins temporarily
   - Check for WorldGuard flags blocking PvP

#### Visual Elements Not Showing
**Symptoms:**
- No boss bars during combat
- Missing action bar messages
- No sound effects

**Solutions:**
1. **Boss Bar Issues:**
   ```yaml
   visual:
     bossbar:
       enabled: true
     themes:
       default-theme: "clean"  # Try changing theme
   ```
2. **Client-Side Issues:**
   - Some clients disable boss bars by default
   - Check client's boss bar settings
3. **Sound Problems:**
   ```yaml
   visual:
     sounds:
       enabled: true
       profile: "default"  # Try different profile
   ```

#### Movement Restrictions Not Working
**Symptoms:**
- Players can use ender pearls in combat
- Elytra gliding works during combat
- Teleport commands succeed

**Solutions:**
1. **Check World Settings:**
   ```yaml
   restrictions:
     enderpearl:
       enabled: true
       worlds:
         world: true  # Ensure current world is enabled
   ```
2. **Permission Bypass:**
   - Remove `pvpcombat.bypass.restrictions` permission
   - Check for admin permissions
3. **Plugin Conflicts:**
   - Some teleport plugins ignore restrictions
   - Check Essentials configuration

#### High Server TPS Drop
**Symptoms:**
- Server TPS drops below 18 during combat
- Lag spikes when many players fight
- Delayed combat detection

**Solutions:**
1. **Performance Tuning:**
   ```yaml
   performance:
     lag:
       enabled: true  # Enable lag compensation
     async:
       enabled: true  # Enable async processing
     cache:
       player-data-ttl: 15  # Reduce cache time
   ```
2. **Session Limits:**
   ```yaml
   combat:
     max-sessions: 50  # Reduce from 100
   ```
3. **Cleanup Intervals:**
   ```yaml
   performance:
     cleanup-interval: 30  # More frequent cleanup
   ```

#### Configuration Not Loading
**Symptoms:**
- `/combat reload` shows errors
- Settings not applying
- Console validation errors

**Solutions:**
1. **YAML Syntax:**
   - Use online YAML validator
   - Check for tabs vs spaces (use spaces)
   - Ensure proper indentation
2. **File Encoding:**
   - Save files as UTF-8 without BOM
   - Avoid special characters in comments
3. **Reload Command:**
   ```
   /combat reload  # Check console output
   ```
4. **Backup and Restore:**
   - Keep backup of working config
   - Restore from backup if needed

### Debug Mode Usage

Enable debug mode for detailed troubleshooting:

```
/combat debug  # Toggle debug mode
```

Debug information includes:
- Combat detection events
- Timer updates
- Restriction applications
- Performance metrics
- Configuration validation

### Log File Analysis

Check server logs for error patterns:

```bash
# Search for PvP Combat errors
grep -i "pvpcombat" logs/latest.log

# Look for specific error types
grep -i "exception" logs/latest.log
grep -i "error" logs/latest.log
```

### Memory Issues

**Symptoms:**
- OutOfMemoryError in console
- Increasing memory usage over time
- Frequent garbage collection

**Solutions:**
1. **Reduce Buffer Sizes:**
   ```yaml
   performance:
     max-event-buffer: 500  # Reduce from 1000

   replay:
     timeline:
       capacity: 500  # Reduce timeline capacity
   ```
2. **Shorter Cleanup Intervals:**
   ```yaml
   performance:
     cleanup-interval: 30  # More frequent cleanup
   ```
3. **Disable Features:**
   ```yaml
   replay:
     enabled: false  # Disable if causing issues
   statistics:
     enabled: false  # Disable statistics tracking
   ```

### Database/Storage Issues

**Symptoms:**
- Statistics not saving
- Replay data corruption
- File system errors

**Solutions:**
1. **File Permissions:**
   - Ensure plugin can write to data folder
   - Check disk space availability
2. **Async Saving:**
   ```yaml
   performance:
     async:
       enabled: true  # Ensure async saving is on
   ```
3. **Backup Data:**
   - Regular backup of `plugins/PvPCombat/` folder
   - Clean reinstall if data corruption suspected

### Network Issues (BungeeCord/Velocity)

**Symptoms:**
- Combat status not syncing across servers
- Cross-server teleport issues
- Plugin channel errors

**Solutions:**
1. **Configuration Sync:**
   ```yaml
   integration:
     cross-server-sync:
       enabled: true
       platform: "AUTO"  # Or specify BUNGEE/VELOCITY
   ```
2. **Plugin Channels:**
   - Ensure BungeeCord/Velocity plugin messaging enabled
   - Check server connection stability
3. **Version Compatibility:**
   - Ensure all servers run same plugin version
   - Check BungeeCord/Velocity version compatibility

### PlaceholderAPI Issues

**Symptoms:**
- Placeholders not working in other plugins
- %pvpcombat_*% shows as raw text

**Solutions:**
1. **Plugin Load Order:**
   - Ensure PvPCombat loads before plugins using placeholders
   - Use `/papi reload` after changes
2. **Placeholder Expansion:**
   - Some plugins need PAPI expansion
   - Check documentation for required expansion
3. **Debug Placeholders:**
   ```
   /papi parse <player> %pvpcombat_status%
   ```

### Common Plugin Conflicts

#### Anti-Cheat Plugins
- Disable combat checks during PvP Combat sessions
- Add exception regions for combat zones
- Configure anti-cheat to ignore combat-tagged players

#### Teleport Plugins
- Essentials: Configure `teleport-safety` in config
- CMI: Add PvP Combat compatibility mode
- Custom teleports: Add combat status checks

#### World Management Plugins
- WorldGuard: Set PvP flags appropriately
- Multiverse: Configure per-world combat rules
- Citizens: Handle NPC combat interactions

## Performance Tuning Guide

### Optimizing for High Player Counts

#### Basic Performance Settings
```yaml
# Recommended for servers with 50+ concurrent players
performance:
  lag:
    enabled: true
    tps-threshold: 19.0  # Higher threshold for better performance

  async:
    enabled: true
    thread-pool-size: 6  # Increase for more cores

  cache:
    player-data-ttl: 20  # Slightly longer cache time
    cleanup-interval: 45  # Less frequent cleanup

combat:
  max-sessions: 75  # Allow more concurrent combats

replay:
  enabled: false  # Disable replay system for performance
```

#### Advanced Performance Configuration
```yaml
# For servers with 100+ players
performance:
  max-event-buffer: 750  # Balanced buffer size
  cleanup-interval-ticks: 2400  # Less frequent cleanup

visual:
  bossbar:
    update-interval: 2  # Less frequent updates
  actionbar:
    update-interval: 30  # Reduce action bar spam

restrictions:
  enderpearl:
    # Reduce cooldown checks
    combat-cooldown-multiplier: 1.5  # Less aggressive
```

### Memory Optimization

#### Low-Memory Configurations
```yaml
# For servers with limited RAM (<4GB)
performance:
  cache:
    player-data-ttl: 10  # Shorter cache time
  cleanup-interval: 20   # More frequent cleanup

replay:
  timeline:
    capacity: 250  # Smaller timeline buffers
  cache:
    max_age_minutes: 15  # Shorter replay cache

statistics:
  max-replay-events: 250  # Fewer stored events
```

#### High-Memory Configurations
```yaml
# For servers with abundant RAM (8GB+)
performance:
  cache:
    player-data-ttl: 45  # Longer cache time
  cleanup-interval: 120  # Less frequent cleanup

replay:
  timeline:
    capacity: 2000  # Larger buffers
  cache:
    max_age_minutes: 60  # Longer replay cache

async:
  thread-pool-size: 8  # More threads for processing
```

### TPS Optimization Strategies

#### Combat System Tuning
```yaml
combat:
  duration: 20  # Shorter combats reduce load
  cooldown: 5   # Shorter cooldowns

damage:
  reset-on-damage: true  # But consider false for performance
  min-damage-trigger: 1.0  # Higher threshold reduces checks
```

#### Visual System Optimization
```yaml
visual:
  bossbar:
    enabled: true
    update-interval: 3  # Reduce update frequency

  actionbar:
    enabled: false  # Disable for maximum performance

  sounds:
    enabled: false  # Disable sounds for performance
```

#### Restriction System Tuning
```yaml
restrictions:
  enderpearl:
    enabled: true
    # Reduce check frequency
    combat-cooldown-multiplier: 1.0  # No multiplier

  elytra:
    enabled: true
    # Less restrictive rules
    block-boosting: false  # Allow boosting
```

### Monitoring Performance

#### Built-in Performance Metrics
```
/replay stats  # Check memory usage
/combat debug  # Monitor combat processing
```

#### External Monitoring
- Use Spark or similar profiler to identify bottlenecks
- Monitor TPS with `/tps` or similar commands
- Track memory usage with `/timings` (Paper servers)

#### Performance Benchmarks

**Small Server (10-25 players):**
- Expected TPS: 19.5-20.0
- Memory usage: 50-100MB
- Recommended settings: Default configuration

**Medium Server (25-100 players):**
- Expected TPS: 18.5-19.5
- Memory usage: 100-300MB
- Recommended: Enable lag compensation, moderate restrictions

**Large Server (100+ players):**
- Expected TPS: 18.0+
- Memory usage: 300MB-1GB+
- Recommended: Aggressive performance tuning, disable non-essential features

### Scaling Strategies

#### Horizontal Scaling (Multiple Servers)
```yaml
integration:
  cross-server-sync:
    enabled: true
    platform: "BUNGEE"  # Or VELOCITY
    broadcast:
      enabled: false  # Reduce network traffic
```

#### Vertical Scaling (Single Powerful Server)
- Increase thread pool sizes
- Enable all caching features
- Use SSD storage for logs
- Allocate more RAM to JVM

#### Feature Prioritization
1. **Essential:** Combat detection, basic restrictions
2. **Important:** Visual feedback, statistics
3. **Optional:** Advanced features, extensive logging

### Troubleshooting Performance Issues

#### High CPU Usage
1. Reduce async thread pool size
2. Disable real-time statistics updates
3. Lower boss bar update frequency
4. Check for plugin conflicts

#### High Memory Usage
1. Reduce cache TTL values
2. Lower timeline buffer capacities
3. Increase cleanup frequencies
4. Disable replay system if not needed

#### TPS Drops During Combat
1. Enable lag compensation
2. Reduce concurrent session limits
3. Disable visual effects temporarily
4. Check for chunk loading issues

## Support

For support, bug reports, or feature requests:
- **GitHub Issues**: [Create an issue](https://github.com/muzlik/pvp-combat/issues)
- **Discord**: Join our community server
- **Documentation**: Check the [Wiki](https://github.com/muzlik/pvp-combat/wiki)

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes with proper tests
4. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Version History

### v1.0.0
- Initial release
- Basic combat detection and logging
- Movement restrictions
- Visual feedback systems
- Configuration management
- PlaceholderAPI integration
- Admin commands