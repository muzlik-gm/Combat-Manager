# PvP Combat System Plugin - Technical Requirements Document

## Overview
This document extracts and structures the technical requirements from the Game Design Document (GDD) for the PvP Combat System plugin. It provides detailed implementation points for each feature, including classes, methods, data structures, event handlers, configuration options, dependencies, performance considerations, and edge cases.

## Dependencies
- **Primary API**: Spigot/Paper API (for event handling, bossbars, action bars, commands)
- **Optional**: PlaceholderAPI (for scoreboard/hologram integration)
- **Network**: BungeeCord/Velocity API (for cross-server sync)
- **Libraries**:
  - None specified, but consider JSON libraries for event logging (e.g., Gson for JSON serialization)
  - Possibly a configuration library like SnakeYAML if not using Spigot's built-in
- **Minecraft Version Support**: 1.16+ (for bossbar APIs, modern event handling)

## Core Features Breakdown

### 2.1 Combat Tracking
**Purpose**: Track when players enter combat, display countdown timers, and manage combat state.

**Classes**:
- `CombatSession`: Represents a single combat instance between two players. Fields: `Player attacker`, `Player defender`, `long startTime`, `int timerSeconds`, `BossBar bossBar`, `boolean active`.
- `CombatManager`: Singleton class to manage all active combat sessions. Methods: `startCombat(Player a, Player b)`, `endCombat(UUID playerId)`, `isInCombat(Player p)`, `resetTimer(UUID sessionId)`.

**Methods**:
- `CombatManager.startCombat(Player a, Player b)`: Creates a new CombatSession, assigns bossbar, schedules timer task.
- `CombatManager.resetTimer(UUID sessionId)`: Resets the session's timer and bossbar progress.
- `CombatSession.updateBossBar()`: Updates bossbar progress based on remaining time.
- `CombatSession.sendActionBar()`: Sends action bar message to both players.

**Data Structures**:
- `HashMap<UUID, CombatSession>` for active sessions (key: player UUID, value: session).
- `ConcurrentHashMap` for thread-safety if async operations.
- `Queue<CombatEvent>` for storing recent events (see Replay feature).

**Event Handlers**:
- `EntityDamageByEntityEvent`: Trigger combat start/reset on player-to-player hits.
- `PlayerQuitEvent`: End combat if player logs out.
- `PlayerDeathEvent`: End combat on death.

**Configuration Options** (YAML):
```yaml
combat:
  timer_seconds: 15
  reset_on_hit: true
  reset_on_ability: false  # Placeholder for future expansions
  bossbar:
    title: "Combat Timer"
    color: RED
    style: SOLID
  actionbar:
    message: "You are in combat with {opponent}"
  sounds:
    enter: ENTITY_VILLAGER_NO
    exit: ENTITY_EXPERIENCE_ORB_PICKUP
```

**Performance Implications**:
- Bossbar updates every tick (20/sec) - use scheduled tasks, avoid sync lag.
- Map size limits: cleanup inactive sessions every minute.

**Edge Cases**:
- Player A hits Player B, Player B hits Player C: Only track A vs B initially.
- Server restart: Persist active sessions to file/database.
- Multiple hits in same tick: Debounce to prevent timer spam.

### 2.2 Anti-Interference System
**Purpose**: Detect and handle third-party interference in ongoing combats.

**Classes**:
- `InterferenceDetector`: Utility class for checking interference logic.

**Methods**:
- `InterferenceDetector.checkInterference(Player hitter, Player target)`: Returns boolean if interference detected.
- `CombatManager.handleInterference(Player interferer, Player target)`: Sends messages, plays sounds, optionally cancels event.

**Data Structures**:
- Reuse CombatSession map to check if target is in active combat.

**Event Handlers**:
- `EntityDamageByEntityEvent`: Check for interference before processing damage.

**Configuration Options**:
```yaml
interference:
  enabled: true
  block_hit: false  # Notify-only or block
  message: "{interferer} cannot interfere: {target} is in combat with {opponent}"
  sound: ENTITY_VILLAGER_NO
```

**Performance Implications**:
- Additional check on every hit event - minimal overhead.

**Edge Cases**:
- Group fights: Allow configurable "multi-player combat" mode.
- Allies: Future expansion for team-based checks.

### 2.3 Ender Pearl & Elytra Rules
**Purpose**: Apply restrictions during combat.

**Classes**:
- `MovementRestrictor`: Handles pearl/elytra logic.

**Methods**:
- `MovementRestrictor.canUseEnderPearl(Player p)`: Checks combat state and config.
- `MovementRestrictor.canUseElytra(Player p)`: Altitude/world checks.

**Event Handlers**:
- `PlayerInteractEvent`: For pearl throws.
- `EntityToggleGlideEvent`: For elytra.

**Configuration Options**:
```yaml
movement_restrictions:
  ender_pearl:
    cooldown_seconds: 10
    world_blacklist: ["world_nether"]
  elytra:
    disabled_in_combat: true
    altitude_threshold: 100
```

**Edge Cases**:
- Creative mode: Bypass restrictions.
- World-specific: Different rules per world.

### 2.4 Visual Customization System
**Purpose**: Allow themeable bossbars, messages, sounds.

**Classes**:
- `ThemeManager`: Loads and applies themes.
- `Theme`: Enum or class with color, title, sound settings.

**Methods**:
- `ThemeManager.applyTheme(CombatSession session, String themeName)`: Sets bossbar style.

**Data Structures**:
- `Map<String, Theme>` for loaded themes.

**Configuration Options**:
- Nested YAML sections for multiple themes (minimal, fire, etc.).

**Performance**: Theme loading on startup.

### 2.5 Combat Log & Summary System
**Purpose**: Record and display fight statistics.

**Classes**:
- `CombatLogger`: Records events.
- `CombatSummary`: Data class for stats (hits, damage, etc.).

**Methods**:
- `CombatLogger.logHit(Player hitter, double damage)`: Appends to session log.
- `CombatSummary.generate(ChatColor color)`: Formats summary message.

**Data Structures**:
- `List<HitEvent>` per session (HitEvent: timestamp, damage, accuracy).

**Event Handlers**:
- Tied to damage events.

**Configuration**: Output format (chat, GUI).

### 2.6 Replay-Style Event Timeline
**Classes**:
- `EventReplay`: Stores last N events as JSON.
- Methods: `addEvent(String event)`, `toJson()`.

**Data Structures**: `Deque<Event>` for FIFO.

### 2.7 Lag-Aware Combat Decisions
**Classes**: `LagCompensator`.
**Methods**: Adjust timer based on TPS.
**Data Structures**: Rolling average TPS.

### 2.8 Cross-Server Combat Sync
**Dependencies**: Bungee/Velocity channels.
**Classes**: `NetworkMessenger`.
**Methods**: Send sync packets.

### 2.9 Admin & Developer Tools
**Classes**: Command classes extending CommandExecutor.
**Methods**: Inspect, debug commands.

### 2.10 Configuration System
**Classes**: `ConfigManager` with validation.
**Methods**: `loadConfig()`, `validate()`.

### 2.11 Performance Optimization
- Async event handling where possible.
- Cached combat states.
- Scheduled cleanup tasks.

## User Experience Flow (Technical)
1. **Entering Combat**: EntityDamageByEntityEvent → CombatManager.startCombat() → Schedule timer task → Send bossbar/actionbar.
2. **During Combat**: Reset on hits → Update bossbar.
3. **Ending**: Timer expires → End session → Optional summary.

## Commands & Permissions
- `/combat status`: CommandStatus class, permission: `combat.use`.
- Admin commands: `/combat inspect`, permission: `combat.admin`.

## Performance Implications
- Event-driven: Minimal idle overhead.
- Memory: Limit concurrent sessions (e.g., 1000 max).
- TPS: Avoid heavy computations in sync events.

## Edge Cases & Integration Points
- Multi-world: Per-world configs.
- Plugins: Hook into other PvP plugins.
- Errors: Graceful degradation on API failures.
- Integration: PlaceholderAPI for custom placeholders.

This document serves as the foundation for implementation. All features are modular and configurable.