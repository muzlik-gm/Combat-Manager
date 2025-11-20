# PvP Combat System - Configuration Guide

## Overview

This comprehensive guide covers all configuration options available in the PvP Combat System plugin. The configuration is split across multiple files for better organization and easier management.

## Configuration Files

### config.yml - Main Configuration
The primary configuration file containing all core combat, visual, and performance settings.

### messages.yml - Localization
Contains all user-facing messages, notifications, and UI text with full localization support.

### Auto-Generated Files
- `themes.yml`: Visual theme definitions (automatically generated from config)
- `statistics.yml`: Server-wide combat statistics (updated automatically)

## Quick Start Configuration

For a basic setup, focus on these essential settings:

```yaml
# Basic Combat Settings
general:
  enabled: true
  debug-mode: false

combat:
  duration: 30          # 30-second combat timer
  cooldown: 10          # 10-second safety period
  max-sessions: 50      # Maximum concurrent fights

# Core Restrictions
restrictions:
  enderpearl:
    enabled: true
    cooldown: 10
  elytra:
    enabled: true
    block-glide: true

# Visual Feedback
visual:
  bossbar:
    enabled: true
  actionbar:
    enabled: true
  sounds:
    enabled: true
```

## Detailed Configuration Reference

### General Settings

```yaml
general:
  enabled: true                    # Master switch for the entire plugin
  debug-mode: false               # Enable debug logging (use /combat debug)
  language: "en"                  # Language file to use (future feature)
```

### Commands Configuration

```yaml
commands:
  # Enable/disable specific commands
  enabled:
    status: true                  # /combat status
    summary: true                 # /combat summary
    toggle-style: true            # /combat toggle-style
    inspect: true                 # /combat inspect (admin)
    reload: true                  # /combat reload (admin)
    debug: true                   # /combat debug (admin)

  # Permission requirements for each command
  permissions:
    status: "pvpcombat.command.status"
    summary: "pvpcombat.command.summary"
    toggle-style: "pvpcombat.command.toggle-style"
    inspect: "pvpcombat.admin.inspect"
    reload: "pvpcombat.admin"
    debug: "pvpcombat.admin.debug"

  # Admin-specific settings
  admin:
    gui-update-interval: 5        # Admin GUI refresh rate (seconds)
    debug-verbosity: "MEDIUM"     # Debug detail level (LOW/MEDIUM/HIGH)
    real-time-monitoring: true    # Enable live combat monitoring
    inspection-range: 50          # Max distance for /combat inspect (blocks, 0=unlimited)
```

### Combat System Configuration

#### Core Combat Settings
```yaml
combat:
  # Combat duration and management
  duration: 30                    # Base combat timer in seconds
  cooldown: 10                    # Safety cooldown after combat ends
  max-sessions: 100               # Maximum concurrent combat sessions (0=unlimited)

  # Damage-based combat detection
  damage:
    reset-on-damage: true         # Extend timer when damage is taken
    min-damage-trigger: 0.5       # Minimum damage to trigger combat

  # Boss bar configuration
  bossbar:
    enabled: true
    update-interval: 1            # Update frequency in seconds
    title: "&cCombat: &f{time_left}s"
    color: "RED"
    style: "SOLID"
```

### Movement Restrictions

#### Ender Pearl Restrictions
```yaml
restrictions:
  enderpearl:
    enabled: true                 # Enable ender pearl restrictions
    cooldown: 10                  # Base cooldown in seconds
    combat-cooldown-multiplier: 2.0  # Multiplier during combat
    block-usage: false            # Completely block during combat
```

#### Elytra Restrictions
```yaml
elytra:
  enabled: true                   # Enable elytra restrictions
  block-glide: true              # Prevent gliding during combat
  block-boosting: true           # Prevent firework boosting
  block-takeoff: true            # Prevent takeoff during combat
  min-safe-height: 10            # Minimum height for restrictions
  boost-cooldown: 30             # Cooldown between boosts

  # World-specific settings (overrides global)
  worlds:
    world_nether:
      min-safe-height: 20        # Higher safety height in Nether
      block-glide: false         # Allow gliding in Nether
    world_end:
      block-glide: true          # Strict rules in End
      min-safe-height: 5

  # Time-based restrictions
  time-restrictions:
    block-at-night: false        # Block during night (18000-6000 ticks)
    block-at-day: false          # Block during day (6000-18000 ticks)
```

#### Teleportation Restrictions
```yaml
teleport:
  enabled: true
  blocked-commands:               # Commands blocked during combat
    - "/tp"
    - "/teleport"
    - "/warp"
    - "/home"
    - "/spawn"
    - "/tpa"
    - "/tpahere"
```

### Visual Feedback System

#### Theme Configuration
```yaml
visual:
  themes:
    default-theme: "clean"        # Default theme for new players
    allow-custom: true            # Allow players to change themes

    # Available themes
    available:
      - "minimal"                 # Simple, text-only
      - "fire"                    # Red flame theme
      - "ice"                     # Blue ice theme
      - "neon"                    # Bright neon colors
      - "dark"                    # Dark theme
      - "clean"                   # Clean, modern look

  # Theme transition effects
  animations:
    enabled: true
    theme-transition-duration: 20 # Ticks for theme changes
    theme-transition-steps: 5
```

#### Boss Bar Themes
```yaml
bossbar:
  enabled: true
  update-interval: 1

  # Theme-specific formats with color codes and placeholders
  formats:
    default: "&cCombat: &f{time_left}s"
    minimal: "&7{time_left}"
    fire: "&c🔥 &f{time_left}s &c🔥"
    ice: "&b❄ &f{time_left}s &b❄"
    neon: "&d✨ &f{time_left}s &d✨"
    dark: "&8{time_left}s"
    clean: "&aCombat: &f{time_left}s"

  # Advanced boss bar settings per theme
  custom:
    fire_theme:
      color: "RED"
      style: "SOLID"
    ice_theme:
      color: "BLUE"
      style: "SOLID"
```

#### Action Bar Messages
```yaml
actionbar:
  enabled: true
  show-opponent: true            # Show opponent name
  update-interval: 20            # Ticks between updates

  # Message formats for different styles
  formats:
    default: "&cCombat with &f{opponent} &c- &f{time_left}s"
    minimal: "&7Combat - {time_left}s"
    detailed: "&c[COMBAT] &fFighting &e{opponent} &f- &a{time_left}s &fremaining"
    funny: "&d⚔ &eLOL! &fFighting &b{opponent} &f- &c{time_left}s ⚔"
    medieval: "&6[&4BATTLE&6] &fvs &c{opponent} &f- &e{time_left}s"
    competitive: "&c[&4RANKED&c] &fvs &4{opponent} &f- &c{time_left}s"
```

#### Sound Effects
```yaml
sounds:
  enabled: true
  profile: "default"             # Default sound profile

  # Sound profiles with different audio themes
  profiles:
    default:
      events:
        combat_start:
          sound: "BLOCK_ANVIL_LAND"
          volume: 1.0
          pitch: 1.0
        combat_end:
          sound: "ENTITY_EXPERIENCE_ORB_PICKUP"
          volume: 1.0
          pitch: 1.0
        timer_warning:
          sound: "BLOCK_NOTE_BLOCK_PLING"
          volume: 1.0
          pitch: 2.0
        timer_reset:
          sound: "BLOCK_LEVER_CLICK"
          volume: 0.5
          pitch: 1.5
        interference:
          sound: "ENTITY_VILLAGER_NO"
          volume: 1.0
          pitch: 1.0

    subtle:
      events:
        combat_start:
          sound: "BLOCK_STONE_BUTTON_CLICK_ON"
          volume: 0.7
          pitch: 1.2
        # ... other events with quieter settings

    intense:
      events:
        combat_start:
          sound: "ENTITY_WITHER_SPAWN"
          volume: 1.5
          pitch: 0.8
        # ... other events with dramatic audio
```

#### Message Styles
```yaml
messages:
  default-style: "minimal"

  # Custom message format strings
  formats:
    minimal: "&7{time_left}s remaining"
    detailed: "&cCombat with &f{opponent}&c - &f{time_left}s &cleft &f(Health: &a{health}&f/&a{max_health}&f)"
    funny: "&d⚔ &eGet rekt &b{opponent}&e! &c{time_left}s &eleft! ⚔"
    medieval: "&6[&4BATTLE&6] &fThou fighteth &c{opponent} &f- &e{time_left} &fseconds remaineth"
    competitive: "&c[&4RANKED&c] &fElite duel vs &4{opponent} &f- &c{time_left}s"
```

### Performance Configuration

#### Lag-Aware Adjustments
```yaml
performance:
  lag:
    enabled: true                # Enable lag compensation
    tps-threshold: 18.0          # TPS below this triggers lag mode
    ping-threshold: 200          # Ping threshold in milliseconds
    base-extension-seconds: 5    # Base seconds to add during lag
    extension-multiplier: 1.5    # Severity multiplier
    ping-update-interval-ms: 1000 # How often to check ping
    ping-cleanup-threshold-ms: 300000  # Cleanup inactive players
    tps-history-length: 60       # TPS averaging window
    cleanup-interval-ticks: 1200 # Lag adjustment cleanup

  # Asynchronous processing
  async:
    enabled: true
    thread-pool-size: 4          # Number of async threads

  # Caching configuration
  cache:
    player-data-ttl: 30          # Player data cache time (minutes)
    cleanup-interval: 60         # Cache cleanup interval (seconds)

  # Event buffer limits
  max-event-buffer: 1000         # Maximum events in memory buffer
```

### Integration Settings

#### PlaceholderAPI Integration
```yaml
integration:
  placeholderapi:
    enabled: true                # Enable PAPI placeholders
```

#### Cross-Server Synchronization
```yaml
cross-server-sync:
  enabled: false                 # Enable cross-server features
  platform: "AUTO"              # BUNGEE, VELOCITY, or AUTO
  channel: "pvpcombat:sync"     # Plugin messaging channel

  # Network broadcast settings
  broadcast:
    enabled: false
    format: "&6[Network] &e{attacker} &fis now in combat with &e{defender} &fon &b{server}"

  # Server switching prevention
  prevent-server-switch:
    enabled: true
    message: "&cCannot switch servers while in combat! Time remaining: {time}s"

  # Sync configuration
  sync-interval: 30              # Sync interval in seconds
  timeout: 5                     # Network timeout in seconds

  # Connection pooling
  connection-pool:
    enabled: true
    max-connections: 10
    idle-timeout: 300
```

### Logging Configuration
```yaml
logging:
  enabled: true
  level: "INFO"                  # INFO, DEBUG, WARNING

  # Log file management
  max-files: 5                   # Maximum log files to keep
  max-size-mb: 10                # Maximum size per log file

  # Combat event logging
  combat:
    detailed:
      enabled: true              # Enable detailed combat logging
    storage:
      type: "BOTH"               # FILE, MEMORY, BOTH
    summary:
      delivery: "CHAT"           # CHAT, GUI, STORAGE, NONE

    # Log retention
    retention:
      days: 30                   # Days to keep combat logs

    # Memory storage settings
    memory:
      max-entries: 10000         # Maximum entries in memory

    # Statistics to include
    include-stats:
      - "hits_landed"
      - "damage_dealt"
      - "accuracy"
      - "knockback_exchanges"
      - "combat_duration"
```

### Anti-Cheat Integration
```yaml
anticheat:
  interference:
    enabled: true                # Enable interference detection
    max-interference-percentage: 10.0  # Max third-party damage (%)
    interference-window: 5       # Detection window in seconds
    block-hits: false            # Block interfering hits (false = notify only)

    # Notification message
    message: "&c{interferer} cannot interfere: &f{target} &cis already in combat with &f{opponent}!"

    # Sound notification
    sound:
      enabled: true
      sound: "ENTITY_VILLAGER_NO"
      volume: 1.0
      pitch: 1.0

    # Per-world overrides
    worlds:
      world_nether:
        enabled: false          # Disable in Nether
      # Add more worlds as needed
```

### Statistics System
```yaml
statistics:
  enabled: true                  # Enable statistics tracking
  track-hits: true              # Track detailed hit data
  save-interval: 5              # Save interval in minutes
  max-replay-events: 500        # Events per combat session
```

### Replay System
```yaml
replay:
  enabled: true                  # Enable combat replay system
  storage:
    format: "HYBRID"            # MEMORY, COMPRESSED_FILE, HYBRID

  # Timeline configuration
  timeline:
    capacity: 1000              # Events per session
    max_age_seconds: 600        # Timeline retention time

  # Replay caching
  cache:
    max_age_minutes: 30         # Cache lifetime

  # Access control
  access:
    admin_only: true            # Restrict to admins only
    allowed_admins:             # Specific admin players
      - "admin-uuid-1"
      - "admin-uuid-2"

  # GUI settings
  gui:
    enabled: true               # Enable GUI replay interface
    page_size: 50               # Events per page

  # Auto-play configuration
  autoplay:
    enabled: false              # Enable auto-play
    speed: 1.0                  # Playback speed multiplier
    interval: 4                 # Ticks between events
```

## Configuration Validation

The plugin includes automatic configuration validation that checks for:

- **YAML Syntax Errors**: Proper YAML formatting
- **Required Fields**: Missing mandatory configuration options
- **Value Ranges**: Invalid numeric ranges or enum values
- **Cross-References**: Broken references between configuration sections
- **Deprecation Warnings**: Outdated configuration options

### Validation Command
```bash
# Validate configuration after changes
/combat reload
```

The reload command will show validation results in console:
```
[PvPCombat] Configuration reloaded successfully
[PvPCombat] Validation: 0 errors, 2 warnings
[PvPCombat] Warning: 'old-setting' is deprecated, use 'new-setting' instead
```

## Configuration Examples

### PvP Arena Server
```yaml
combat:
  duration: 60                  # Longer fights for arenas
  cooldown: 5                   # Quick respawn

restrictions:
  enderpearl:
    enabled: false              # Allow pearls in arena
  elytra:
    enabled: false              # Allow elytra in arena

visual:
  themes:
    default-theme: "competitive"
  bossbar:
    formats:
      competitive: "&c[&4RANKED&c] &fvs &4{opponent} &f- &c{time_left}s"
```

### Roleplay Server
```yaml
combat:
  duration: 45                  # Realistic fight duration

visual:
  themes:
    default-theme: "medieval"
  messages:
    formats:
      medieval: "&6[&4BATTLE&6] &fThou fighteth &c{opponent} &f- &e{time_left} &fseconds remaineth"

restrictions:
  elytra:
    enabled: false              # No modern elytra
  enderpearl:
    enabled: false              # No pearls for balance
```

### High-Performance Server
```yaml
performance:
  async:
    thread-pool-size: 8
  cache:
    player-data-ttl: 60

visual:
  bossbar:
    update-interval: 2          # Reduce updates
  actionbar:
    enabled: false              # Disable for performance

combat:
  max-sessions: 200             # Handle more players
```

## Hot Reloading

Most configuration changes can be applied without restarting the server:

```bash
# Reload configuration
/combat reload
```

**Changes that require restart:**
- Plugin enable/disable
- Thread pool size changes
- Major structural changes

**Changes applied immediately:**
- Combat timers and cooldowns
- Visual themes and messages
- Restriction settings
- Performance tuning options

## Configuration Backup

Always backup your configuration before major changes:

```bash
# Create backup
cp config.yml config.yml.backup
cp messages.yml messages.yml.backup

# Restore if needed
cp config.yml.backup config.yml
```

## Troubleshooting Configuration

### Configuration Not Loading
1. Check YAML syntax with online validator
2. Ensure proper file encoding (UTF-8)
3. Verify file permissions
4. Check server logs for specific errors

### Settings Not Applying
1. Use `/combat reload` after changes
2. Check for YAML formatting errors
3. Verify setting paths are correct
4. Check for conflicting settings

### Performance Issues
1. Review performance settings
2. Monitor with `/replay stats`
3. Adjust cache and buffer sizes
4. Consider disabling non-essential features