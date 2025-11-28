# PvP Combat System

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](#)
[![Minecraft](https://img.shields.io/badge/minecraft-1.18--1.21-green.svg)](https://www.spigotmc.org/)
[![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-red.svg)](#)

A feature-rich PvP combat plugin for Paper/Spigot servers with advanced tracking, intelligent restrictions, and stunning visual effects. Built for performance and customization.

## ✨ Key Features

### 🎯 Combat Tracking & Statistics
- **Real-time combat detection** with automatic session management
- **Detailed per-session stats**: damage dealt/received, hits landed, accuracy percentage
- **Cumulative statistics**: wins/losses, K/D ratio, win rate, total damage
- **Combat replay system**: review past fights with timeline playback
- **Combat logging protection**: instant death and inventory drop for combat loggers

### 🛡️ Smart Restrictions
- **Ender Pearl cooldown**: 10s base, 20s during combat (fully configurable)
- **Golden Apple cooldown**: 30s base, 45s during combat with visual indicators
- **Enchanted Golden Apple**: 60s base, 120s during combat
- **Elytra restrictions**: block gliding, boosting, and takeoff during combat
- **Teleport blocking**: prevents /tp, /home, /warp, /spawn commands
- **Block restrictions**: optional prevention of block placement/breaking
- **Anti-interference system**: prevents third-party intervention in 1v1 fights

### 🎨 Visual Experience
- **Boss bar timer**: dynamic combat timer with smooth animations
- **Action bar updates**: real-time opponent and timer information
- **6 built-in themes**: minimal, fire, ice, neon, dark, clean
- **Live theme switching**: change visual style mid-combat with `/combat toggle-style`
- **HEX color support**: full RGB color customization
- **5 sound profiles**: default, subtle, intense, calm, electronic, clean

### ⚡ Performance & Optimization
- **Async processing**: combat logging and statistics run off main thread
- **Intelligent lag compensation**: automatic timer adjustments during server lag
- **Advanced caching**: optimized data access with TTL-based cache
- **Thread-safe architecture**: handle concurrent combat sessions without conflicts
- **Memory efficient**: hybrid storage system for replay data

## 📋 Commands

### Player Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/combat status` | View your current combat status and opponent info | `pvpcombat.command.status` |
| `/combat summary` | Display your combat statistics and performance | `pvpcombat.command.summary` |
| `/combat toggle-style` | Cycle through visual themes during combat | `pvpcombat.command.toggle-style` |

### Admin Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/combat inspect <player>` | View real-time combat data for any player | `pvpcombat.admin.inspect` |
| `/combat summary <player>` | Access detailed statistics for any player | `pvpcombat.admin` |
| `/combat reload` | Reload configuration without restarting | `pvpcombat.admin` |
| `/combat debug` | Toggle debug mode for troubleshooting | `pvpcombat.admin.debug` |
| `/replay <player>` | View combat replay for a player | `pvpcombat.admin` |

## Permissions

- `pvpcombat.command.status` - Use /combat status
- `pvpcombat.command.summary` - Use /combat summary
- `pvpcombat.command.toggle-style` - Use /combat toggle-style
- `pvpcombat.admin` - Access all admin commands
- `pvpcombat.admin.inspect` - Use /combat inspect
- `pvpcombat.admin.debug` - Use /combat debug

## ⚙️ Configuration

The plugin is highly configurable with over 100 options. Here are some key settings:

### Combat Settings
```yaml
combat:
  duration: 30              # Combat timer in seconds
  cooldown: 10              # Cooldown after combat ends
  max-sessions: 100         # Maximum concurrent combat sessions
  damage:
    reset-on-damage: true   # Reset timer on any damage
    min-damage-trigger: 0.5 # Minimum damage to trigger combat
```

### Item Cooldowns
```yaml
restrictions:
  enderpearl:
    enabled: true
    cooldown: 10                      # Base cooldown (seconds)
    combat-cooldown-multiplier: 2.0   # 2x during combat = 20s
    block-usage: false                # Completely block during combat
  
  golden-apple:
    enabled: true
    cooldown: 30                      # Base cooldown (seconds)
    combat-cooldown-multiplier: 1.5   # 1.5x during combat = 45s
    block-usage: false
  
  enchanted-golden-apple:
    enabled: true
    cooldown: 60                      # Base cooldown (seconds)
    combat-cooldown-multiplier: 2.0   # 2x during combat = 120s
    block-usage: false
```

### Visual Themes
```yaml
visual:
  themes:
    default-theme: "clean"
    available:
      - "minimal"   # Simple, clean display
      - "fire"      # 🔥 Fire themed
      - "ice"       # ❄ Ice themed
      - "neon"      # ✨ Neon themed
      - "dark"      # Dark mode
      - "clean"     # Modern clean theme
```

### Performance Tuning
```yaml
performance:
  lag:
    enabled: true
    tps-threshold: 18.0           # TPS below this = lag
    base-extension-seconds: 5     # Extra time during lag
  async:
    enabled: true
    thread-pool-size: 4           # Async worker threads
  cache:
    player-data-ttl: 30           # Cache TTL in minutes
```

## 📦 Installation

1. **Download** the plugin JAR file
2. **Place** `PvPCombat-1.0.0.jar` in your server's `plugins` folder
3. **Restart** your server (or use `/reload confirm` at your own risk)
4. **Configure** `plugins/PvPCombat/config.yml` to your preferences
5. **Reload** with `/combat reload` to apply changes without restart

### Optional Dependencies
- **PlaceholderAPI**: For placeholder support in other plugins (auto-detected)

### First-Time Setup
After installation, the plugin will:
- Generate default configuration files
- Create necessary data directories
- Initialize the statistics database
- Load all features automatically

No additional setup required - it works out of the box!

## Requirements

- **Server**: Paper or Spigot 1.18.x - 1.21.x
- **Java**: Java 17 or higher (Java 21 recommended)
- **Dependencies**: None (standalone plugin)

## Supported Versions

### Fully Tested
- 1.21.10, 1.21.9, 1.21.8, 1.21.7, 1.21.6, 1.21.5, 1.21.4, 1.21.3, 1.21.2, 1.21.1, 1.21.0

### Compatible
- 1.20.6, 1.20.5, 1.20.4, 1.20.3, 1.20.2, 1.20.1
- 1.19.x series (most features work)
- 1.18.x series (basic features work)

## 📊 Combat Summary Display

After each combat, players receive a detailed summary:

```
╔═══════════════════════════════════════╗
║        COMBAT SUMMARY                 ║
╠═══════════════════════════════════════╣
║ Opponent: PlayerName                  ║
║ Hits Landed: 15/30 (50.0%)            ║
║ Damage Dealt: 12.5 ❤                 ║
║ Damage Received: 8.0 ❤               ║
║ Duration: 23s                         ║
║ Result: VICTORY                       ║
╚═══════════════════════════════════════╝
```

**Statistics Explained:**
- **Hits Landed**: Your hits / Total hits exchanged (accuracy %)
- **Damage Dealt**: Total damage you dealt in hearts
- **Damage Received**: Total damage you took in hearts
- **Duration**: Actual fight time in seconds
- **Result**: WIN, LOSS, or DRAW

## 🚫 Combat Logging Protection

Players who disconnect during combat face consequences:
- ⚡ **Instant death** upon logout
- 💀 **Full inventory drop** at logout location
- 📉 **Loss recorded** in statistics
- 🏆 **Opponent gets win** and notification
- 📢 **Server-wide broadcast** of the forfeit

This prevents players from escaping combat by logging out.

## 🔌 PlaceholderAPI Support

Use these placeholders in other plugins:

| Placeholder | Description |
|-------------|-------------|
| `%pvpcombat_in_combat%` | Returns true/false if player is in combat |
| `%pvpcombat_time_left%` | Remaining combat time in seconds |
| `%pvpcombat_opponent%` | Current opponent's name |
| `%pvpcombat_wins%` | Total wins |
| `%pvpcombat_losses%` | Total losses |
| `%pvpcombat_kd_ratio%` | Kill/Death ratio |
| `%pvpcombat_win_rate%` | Win rate percentage |

## 📝 License

**All Rights Reserved** © 2025 muzlik

This plugin is proprietary software. Unauthorized copying, distribution, modification, or use of this software, via any medium, is strictly prohibited without explicit written permission from the author.

### Permitted Use
- ✅ Use on your Minecraft server
- ✅ Configure and customize settings
- ✅ Share configuration examples

### Prohibited Actions
- ❌ Redistribution or resale
- ❌ Decompilation or reverse engineering
- ❌ Modification of plugin files
- ❌ Claiming as your own work

For licensing inquiries or commercial use, please contact the author.

## 💬 Support

For support, bug reports, or feature requests, please contact:
- **Author**: muzlik
- **Support**: Contact via server or direct message

## 🙏 Credits

**Author**: muzlik  
**Version**: 1.0.0  
**Released**: November 21, 2025

Special thanks to the Paper and Spigot communities for their excellent documentation and support.

---

<div align="center">

**PvP Combat System v1.0.0**

Made with ❤️ for the Minecraft community

© 2025 muzlik - All Rights Reserved

[⬆ Back to Top](#pvp-combat-system)

</div>
