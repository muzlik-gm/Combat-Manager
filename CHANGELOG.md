# Changelog

All notable changes to PvP Combat System will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-11-21

### 🎉 Initial Release

First stable release of PvP Combat System - a comprehensive, feature-rich PvP combat plugin for Paper/Spigot servers.

---

### ✨ Core Features

#### Combat System
- **Real-time combat detection** with automatic session initialization
- **Concurrent session management** supporting unlimited simultaneous fights
- **Thread-safe architecture** preventing race conditions and data corruption
- **Automatic combat timer** with configurable duration (default: 30s)
- **Timer reset on damage** to prevent premature combat exits
- **Minimum damage threshold** to prevent accidental combat triggers (0.5 hearts)

#### Statistics & Tracking
- **Per-session statistics**:
  - Damage dealt and received (precise to 0.1 hearts)
  - Hits landed vs total hits exchanged
  - Accuracy percentage calculation
  - Combat duration in seconds
  - Win/Loss/Draw determination
  
- **Cumulative player statistics**:
  - Total wins and losses
  - Kill/Death (K/D) ratio
  - Win rate percentage
  - Total damage dealt across all fights
  - Total damage received across all fights
  - Total hits landed
  - Average combat duration

#### Combat Replay System
- **Timeline recording** of all combat events
- **Hybrid storage system** (memory + compressed files)
- **Replay playback** with admin commands
- **Event filtering** and search capabilities
- **Configurable retention** (default: 30 days)
- **Memory-efficient** with automatic cleanup

---

### 🛡️ Restrictions & Anti-Abuse

#### Item Cooldowns
- **Ender Pearl**:
  - Base cooldown: 10 seconds
  - Combat multiplier: 2.0x (20 seconds during combat)
  - Visual cooldown indicator in hotbar
  - Configurable block-usage option
  
- **Golden Apple**:
  - Base cooldown: 3 seconds
  - Combat multiplier: 1.5x (4.5 seconds during combat)
  - Minecraft native cooldown integration
  - Remaining time display on use attempt
  
- **Enchanted Golden Apple**:
  - Base cooldown: 4 seconds
  - Combat multiplier: 2.0x (8 seconds during combat)
  - Separate tracking from regular golden apples
  - Visual feedback on cooldown

#### Movement Restrictions
- **Elytra**:
  - Block glide activation during combat
  - Prevent firework rocket boosting
  - Block takeoff attempts
  - Configurable minimum safe height
  - Per-world settings support
  - Time-based restrictions (day/night)
  
- **Teleportation**:
  - Block `/tp`, `/teleport`, `/warp`, `/home`, `/spawn`
  - Configurable command list
  - Bypass permission support
  - Custom denial messages

#### Block Interactions
- **Optional block breaking prevention**
- **Optional block placing prevention**
- **Per-world configuration**
- **Whitelist/blacklist support**

#### Anti-Interference System
- **Third-party damage detection**
- **Configurable interference threshold** (default: 10%)
- **Automatic hit blocking** or warning mode
- **Sound and visual feedback**
- **Per-world settings**

---

### 🎨 Visual Features

#### Boss Bar System
- **Dynamic combat timer** with smooth countdown
- **Color-coded urgency** (green → yellow → red)
- **6 built-in themes**:
  - `minimal` - Simple, distraction-free
  - `fire` - 🔥 Fire-themed with emojis
  - `ice` - ❄ Ice-themed with emojis
  - `neon` - ✨ Neon-themed with emojis
  - `dark` - Dark mode aesthetic
  - `clean` - Modern, professional look
- **Live theme switching** with `/combat toggle-style`
- **HEX color support** for full RGB customization
- **Configurable update interval** (default: 1 second)

#### Action Bar
- **Real-time opponent display**
- **Remaining time countdown**
- **Health indicators** (optional)
- **Multiple format styles**:
  - Default, minimal, detailed, funny, medieval, competitive
- **Configurable update rate** (default: 20 ticks)

#### Sound System
- **5 sound profiles**:
  - `default` - Balanced sound effects
  - `subtle` - Quiet, non-intrusive
  - `intense` - Dramatic, epic sounds
  - `calm` - Peaceful, zen-like
  - `electronic` - Futuristic, tech sounds
  - `clean` - Modern UI sounds
- **Event-based triggers**:
  - Combat start
  - Combat end
  - Timer warning (< 10s)
  - Timer reset
  - Interference detection
- **Volume and pitch control** per sound

#### Combat Summary
- **Post-combat statistics display**
- **Formatted chat messages** with colors
- **Detailed breakdown**:
  - Opponent name
  - Hits landed (with percentage)
  - Damage dealt and received
  - Combat duration
  - Result (WIN/LOSS/DRAW)
- **Configurable delivery** (chat, GUI, or both)

---

### 📋 Commands

#### Player Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/combat status` | View current combat status and opponent | `pvpcombat.command.status` |
| `/combat summary` | Display personal combat statistics | `pvpcombat.command.summary` |
| `/combat toggle-style` | Cycle through visual themes | `pvpcombat.command.toggle-style` |

#### Admin Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/combat inspect <player>` | Real-time combat data inspection | `pvpcombat.admin.inspect` |
| `/combat summary <player>` | View any player's statistics | `pvpcombat.admin` |
| `/combat reload` | Reload configuration | `pvpcombat.admin` |
| `/combat debug` | Toggle debug mode | `pvpcombat.admin.debug` |
| `/replay <player>` | View combat replay timeline | `pvpcombat.admin` |

---

### ⚡ Performance & Optimization

#### Asynchronous Processing
- **Combat logging** runs off main thread
- **Statistics calculation** async processing
- **Database operations** non-blocking
- **Configurable thread pool** (default: 4 threads)

#### Caching System
- **Player data caching** with TTL (default: 30 minutes)
- **Restriction data caching** for fast lookups
- **Combat state caching** for quick checks
- **Automatic cache cleanup** on interval
- **Memory-efficient** with size limits

#### Lag Compensation
- **TPS monitoring** with configurable threshold (default: 18.0)
- **Automatic timer extension** during server lag
- **Ping-based adjustments** for player latency
- **Configurable extension multiplier** (default: 1.5x)
- **Historical TPS averaging** (60 samples)
- **Per-player ping tracking**

#### Memory Management
- **Event buffer limits** (default: 1000 events)
- **Automatic session cleanup** on interval
- **Replay data compression** for storage
- **Configurable retention periods**
- **Garbage collection friendly**

---

### 🔌 Integrations

#### PlaceholderAPI Support
- `%pvpcombat_in_combat%` - Combat status (true/false)
- `%pvpcombat_time_left%` - Remaining seconds
- `%pvpcombat_opponent%` - Opponent name
- `%pvpcombat_wins%` - Total wins
- `%pvpcombat_losses%` - Total losses
- `%pvpcombat_kd_ratio%` - K/D ratio
- `%pvpcombat_win_rate%` - Win rate percentage
- `%pvpcombat_total_damage_dealt%` - Total damage dealt
- `%pvpcombat_total_damage_received%` - Total damage received

#### Cross-Server Support (Experimental)
- **BungeeCord/Velocity** compatibility
- **Combat state synchronization** across servers
- **Server switch prevention** during combat
- **Network-wide broadcasts**
- **Configurable sync intervals**

---

### 🚫 Combat Logging Protection

#### Anti-Combat Logging
- **Instant death** upon logout during combat
- **Full inventory drop** at logout location
- **Loss recorded** in player statistics
- **Opponent receives win** automatically
- **Server-wide broadcast** of forfeit
- **Configurable punishment** options

---

### 🛠️ Configuration

#### Extensive Customization
- **100+ configuration options**
- **Per-world settings** for most features
- **Hot-reload support** with `/combat reload`
- **YAML validation** with helpful error messages
- **Default value fallbacks** for missing options
- **Comments and examples** in config file

#### Configuration Categories
- General settings
- Combat mechanics
- Restrictions and cooldowns
- Visual customization
- Performance tuning
- Integration settings
- Logging and debugging
- Anti-cheat features
- Statistics tracking
- Replay system

---

### 📊 Logging & Debugging

#### Combat Logging
- **Detailed event logging** to file
- **Configurable log levels** (INFO, DEBUG, WARNING)
- **Automatic log rotation** (max 5 files)
- **File size limits** (default: 10MB)
- **Retention period** (default: 30 days)
- **Memory and file storage** options

#### Debug Mode
- **Real-time event monitoring**
- **Performance metrics** display
- **Session state inspection**
- **Cache statistics**
- **Thread pool monitoring**
- **Configurable verbosity** (LOW, MEDIUM, HIGH)

---

### 🔒 Security & Anti-Cheat

#### Built-in Protections
- **Interference detection** prevents third-party intervention
- **Combat logging punishment** deters exploits
- **Thread-safe operations** prevent duplication glitches
- **Input validation** on all commands
- **Permission checks** on sensitive operations

---

### 🌍 Compatibility

#### Supported Platforms
- **Paper** 1.18.x - 1.21.x (recommended)
- **Spigot** 1.18.x - 1.21.x (supported)
- **Purpur** 1.18.x - 1.21.x (supported)

#### Tested Versions
- ✅ Minecraft 1.21.10 (fully tested)
- ✅ Minecraft 1.21.x series (fully tested)
- ✅ Minecraft 1.20.x series (compatible)
- ✅ Minecraft 1.19.x series (compatible)
- ⚠️ Minecraft 1.18.x series (basic features)

#### Java Requirements
- **Minimum**: Java 17
- **Recommended**: Java 21
- **Tested**: Java 21.0.8

---

### 📦 Technical Details

#### Architecture
- **Event-driven design** for extensibility
- **Interface-based** for modularity
- **Dependency injection** for testability
- **Factory patterns** for object creation
- **Observer pattern** for state changes

#### Dependencies
- **Caffeine** 3.1.8 - High-performance caching
- **Gson** 2.10.1 - JSON serialization
- **Checker Framework** 3.33.0 - Null safety

#### Build Information
- **Build Tool**: Maven 3.9+
- **Compiler**: Java 21
- **Shading**: Maven Shade Plugin 3.5.1
- **Testing**: JUnit 5 (optional)

---

### 🐛 Known Issues

None reported in this release.

---

### 📝 Notes

- This is the **first stable release** (1.0.0)
- All core features are **production-ready**
- Cross-server sync is **experimental** (disabled by default)
- Replay system is **stable** but may have high memory usage with many concurrent fights
- Configuration validation may show warnings for missing optional values (safe to ignore)

---

### 🔮 Future Plans

- Database storage support (MySQL, PostgreSQL)
- Web dashboard for statistics
- Advanced replay features (slow-motion, camera angles)
- Team combat support (2v2, 3v3, etc.)
- Combat arenas and zones
- ELO rating system
- Leaderboards and rankings
- Combat achievements
- API for developers
- More visual themes

---

### 🙏 Credits

**Author**: muzlik  
**Contributors**: Community feedback and testing  
**Special Thanks**: Paper and Spigot development teams

---

### 📄 License

**All Rights Reserved** © 2025 muzlik

This is proprietary software. Unauthorized copying, distribution, modification, or use is strictly prohibited.

---

**Version**: 1.0.0  
**Released**: November 21, 2025  
**Author**: muzlik  
**Support**: Contact author directly
