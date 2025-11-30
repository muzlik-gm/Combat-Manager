# TrueCombatManager

**The Ultimate PvP Combat Management Plugin for Minecraft Servers**

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/yourusername/TrueCombatManager)
[![Minecraft](https://img.shields.io/badge/minecraft-1.20.4+-green.svg)](https://www.spigotmc.org/)
[![License](https://img.shields.io/badge/license-MIT-yellow.svg)](LICENSE)

---

## 🎯 Overview

TrueCombatManager is a comprehensive, high-performance PvP combat management plugin designed for modern Minecraft servers. It provides advanced combat tracking, newbie protection, restriction systems, and visual feedback - all optimized for zero lag.

### ✨ Key Features

- **🛡️ Newbie Protection** - Protects new players without armor from PvP
- **⚔️ Combat Management** - Real-time combat tracking with lag compensation
- **🚫 Smart Restrictions** - Block items/commands during combat (tridents, ender pearls, etc.)
- **🎨 Visual System** - 6 themes with BossBar, ActionBar, and sound effects
- **📊 Statistics Tracking** - Comprehensive combat stats with PlaceholderAPI support
- **🔧 Performance Optimized** - Zero lag with intelligent logging system
- **🌐 Multi-Server Support** - Cross-server combat sync (BungeeCord/Velocity)

---

## 📋 Table of Contents

- [Features](#-features)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Commands](#-commands)
- [Permissions](#-permissions)
- [PlaceholderAPI](#-placeholderapi)
- [Dependencies](#-dependencies)
- [Support](#-support)

---

## 🎮 Features

### Combat Management
- ✅ Real-time combat tracking
- ✅ Configurable combat duration (default: 30s)
- ✅ Lag compensation system
- ✅ Combat logging protection (instant death on logout)
- ✅ Automatic combat end on death
- ✅ Combat forfeit system

### Newbie Protection
- ✅ Protects players without armor
- ✅ XP level threshold (configurable)
- ✅ Prevents damage dealing AND receiving
- ✅ Bypass permission support
- ✅ Customizable messages

### Restriction Systems
- ✅ **Tridents** - Block throwing and riptide in combat
- ✅ **Ender Pearls** - Block usage and safezone teleportation
- ✅ **Respawn Anchors** - Block usage during combat
- ✅ **Elytra** - Block gliding and firework boosts
- ✅ **End Crystals** - Block placement/breaking
- ✅ **Golden Apples** - Configurable cooldowns
- ✅ **Commands** - Block teleport commands
- ✅ **Safezones** - Prevent entry during combat with visual barriers

### Visual System
- ✅ 6 Built-in Themes (Default, Minimal, Intense, Elegant, Neon, Retro)
- ✅ BossBar display with timer
- ✅ ActionBar notifications
- ✅ Sound effects (configurable)
- ✅ Client-side glass barriers (ProtocolLib)
- ✅ Per-player style preferences

### Statistics & Tracking
- ✅ Wins/Losses tracking
- ✅ K/D ratio calculation
- ✅ Damage dealt/received
- ✅ Combat time tracking
- ✅ Knockback exchanges
- ✅ PlaceholderAPI integration

### Performance
- ✅ Optimized for zero lag
- ✅ Intelligent logging system
- ✅ Async operations
- ✅ Efficient caching
- ✅ Minimal memory footprint

---

## 📦 Installation

### Requirements
- **Minecraft:** 1.20.4+ (Paper/Spigot)
- **Java:** 21+
- **ProtocolLib:** 5.0+ (Recommended for barriers)
- **WorldGuard:** 7.0+ (Optional, for safezone protection)
- **PlaceholderAPI:** 2.11+ (Optional, for placeholders)

### Steps
1. Download the latest release
2. Place `TrueCombatManager-1.0.0.jar` in your `plugins/` folder
3. Install ProtocolLib (highly recommended)
4. Restart your server
5. Configure `plugins/TrueCombatManager/config.yml`
6. Reload with `/combat reload`

---

## ⚙️ Configuration

### Quick Start

```yaml
# Enable/disable the plugin
general:
  enabled: true

# Combat duration in seconds
combat:
  duration: 30

# Newbie protection
newbie-protection:
  enabled: true
  xp-level-threshold: 3
  require-any-armor: true

# Console logging (disable for best performance)
logging:
  console-enabled: false
```

### Key Configuration Sections

#### Combat Settings
```yaml
combat:
  duration: 30                    # Combat duration in seconds
  allow-flight: false             # Allow flight during combat
  cancel-on-death: true           # End combat on death
```

#### Newbie Protection
```yaml
newbie-protection:
  enabled: true
  prevent-damage-dealing: true    # Newbies can't attack
  prevent-damage-receiving: true  # Newbies can't be attacked
  xp-level-threshold: 3           # Players with >3 XP not protected
  require-any-armor: true         # Need at least 1 armor piece
```

#### Restrictions
```yaml
restrictions:
  trident:
    enabled: true                 # Block tridents in combat
  
  enderpearl:
    enabled: true
    block-usage: true             # Block ender pearls
  
  respawn-anchor:
    enabled: true                 # Block respawn anchors
  
  elytra:
    enabled: true
    block-glide: true             # Block elytra gliding
  
  teleport:
    enabled: true
    blocked-commands:             # Commands to block
      - "tp"
      - "home"
      - "spawn"
```

#### Safezone Protection
```yaml
restrictions:
  safezone:
    enabled: true
    block-entry: true             # Block safezone entry
    protected-regions:            # WorldGuard regions
      - "spawn"
      - "safezone"
    barrier:
      material: "GLASS"           # Barrier block type
      height: 4                   # Barrier height
```

---

## 🎮 Commands

### Player Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/combat status` | Check your combat status | `pvpcombat.command.status` |
| `/combat summary` | View your combat statistics | `pvpcombat.command.summary` |
| `/combat toggle-style` | Change visual theme | `pvpcombat.command.toggle-style` |

### Admin Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/combat inspect <player>` | Inspect player's combat status | `pvpcombat.admin.inspect` |
| `/combat reload` | Reload configuration | `pvpcombat.admin` |
| `/combat debug` | Toggle debug mode | `pvpcombat.admin.debug` |
| `/combat logging <enabled\|disabled>` | Control console logging | `pvpcombat.admin` |

---

## 🔐 Permissions

### Player Permissions
- `pvpcombat.command.status` - Use /combat status (default: true)
- `pvpcombat.command.summary` - Use /combat summary (default: true)
- `pvpcombat.command.toggle-style` - Change visual theme (default: true)

### Admin Permissions
- `pvpcombat.admin` - Access all admin commands (default: op)
- `pvpcombat.admin.inspect` - Inspect players (default: op)
- `pvpcombat.admin.debug` - Toggle debug mode (default: op)

### Bypass Permissions
- `pvpcombat.bypass.combatlog` - Bypass combat logging restrictions (default: op)
- `pvpcombat.bypass.restrictions` - Bypass all restrictions (default: op)
- `pvpcombat.bypass.newbie` - Bypass newbie protection (default: op)

---

## 📊 PlaceholderAPI

### Combat Status
- `%pvpcombat_in_combat%` - true/false
- `%pvpcombat_time_left%` - Remaining seconds
- `%pvpcombat_opponent%` - Opponent name

### Lifetime Statistics
- `%pvpcombat_wins%` - Total wins
- `%pvpcombat_losses%` - Total losses
- `%pvpcombat_total_combats%` - Total combats
- `%pvpcombat_kd_ratio%` - K/D ratio
- `%pvpcombat_win_rate%` - Win rate percentage
- `%pvpcombat_total_damage_dealt%` - Total damage dealt
- `%pvpcombat_total_damage_received%` - Total damage received

### Session Statistics
- `%pvpcombat_session_damage_dealt%` - Damage in current fight
- `%pvpcombat_session_damage_received%` - Damage received in current fight
- `%pvpcombat_knockback_exchanges%` - Knockback exchanges

---

## 🔌 Dependencies

### Required
- **Spigot/Paper** 1.20.4+
- **Java** 21+

### Optional (Recommended)
- **ProtocolLib** 5.0+ - For client-side barriers
- **WorldGuard** 7.0+ - For safezone protection
- **PlaceholderAPI** 2.11+ - For placeholders

### Optional (Integrations)
- **Citizens** - NPC combat support
- **CombatLogX** - Integration support
- **MythicMobs** - Mob combat support

---

## 🎨 Visual Themes

### Available Themes
1. **Default** - Classic red/yellow theme
2. **Minimal** - Clean gray theme
3. **Intense** - Bold red/orange theme
4. **Elegant** - Sophisticated purple theme
5. **Neon** - Bright cyan/pink theme
6. **Retro** - Vintage green/yellow theme

### Customization
Players can switch themes with `/combat toggle-style`

---

## 🚀 Performance

### Optimizations
- ✅ **Zero Lag** - Optimized for high-performance servers
- ✅ **Async Operations** - Non-blocking database operations
- ✅ **Smart Caching** - Efficient data caching
- ✅ **Intelligent Logging** - No performance impact when disabled
- ✅ **Minimal Memory** - Low memory footprint

### Logging Control
```bash
# Disable logging for best performance (default)
/combat logging disabled

# Enable logging for debugging
/combat logging enabled
```

---

## 🛠️ Advanced Features

### Combat Logging Protection
- Players who logout during combat are instantly killed
- Inventory drops on death
- Opponent receives win credit
- Broadcast message to server

### Lag Compensation
- Automatic TPS monitoring
- Combat timer adjustment based on lag
- Fair combat duration regardless of server performance

### Cross-Server Support
- BungeeCord/Velocity integration
- Combat state synchronization
- Cross-server combat tracking

---

## 📝 Configuration Examples

### Hardcore PvP Server
```yaml
combat:
  duration: 60                    # Longer combat
newbie-protection:
  enabled: false                  # No protection
restrictions:
  trident:
    enabled: true                 # Block all items
  enderpearl:
    enabled: true
  elytra:
    enabled: true
```

### Casual/Friendly Server
```yaml
combat:
  duration: 15                    # Shorter combat
newbie-protection:
  enabled: true                   # Protect newbies
  xp-level-threshold: 10          # Higher threshold
restrictions:
  trident:
    enabled: false                # Allow items
  enderpearl:
    enabled: false
```

---

## 🐛 Troubleshooting

### Common Issues

**Issue:** Newbie protection not working
- Check `newbie-protection.enabled: true`
- Verify player has no armor equipped
- Check player XP level is below threshold
- Ensure player doesn't have bypass permission

**Issue:** Barriers not showing
- Install ProtocolLib
- Check `restrictions.safezone.barrier.enabled: true`
- Verify WorldGuard regions are configured

**Issue:** Server lag
- Disable console logging: `/combat logging disabled`
- Check `logging.console-enabled: false` in config
- Reduce combat duration if needed

---

## 📞 Support

### Getting Help
- **Discord:** [Join our Discord](https://discord.gg/yourserver)
- **Issues:** [GitHub Issues](https://github.com/yourusername/TrueCombatManager/issues)
- **Wiki:** [Documentation](https://github.com/yourusername/TrueCombatManager/wiki)

### Reporting Bugs
1. Check if issue already exists
2. Provide server version and plugin version
3. Include relevant config sections
4. Attach console errors (if any)
5. Describe steps to reproduce

---

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Credits

**Developer:** muzlik  
**Contributors:** [List contributors]  
**Special Thanks:** Paper team, ProtocolLib, WorldGuard

---

## 🔄 Changelog

### v1.0.0 (2025-11-30)
- ✅ Initial release
- ✅ Complete combat management system
- ✅ Newbie protection
- ✅ All restriction systems
- ✅ Visual themes
- ✅ Statistics tracking
- ✅ Performance optimizations
- ✅ Console logging control

---

**Made with ❤️ for the Minecraft community**
