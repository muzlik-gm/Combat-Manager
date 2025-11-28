# PvP Combat Plugin - Complete Information

## ✅ All Fixed Issues

### 1. Combat Logging Death ✅
- Player is **killed immediately** when logging out during combat
- Inventory drops naturally through Minecraft's death system
- Opponent receives win, logger receives loss
- Opponent is notified

### 2. Ender Pearl Cooldown ✅
- **Base cooldown**: 10 seconds
- **Combat cooldown**: 20 seconds (2x multiplier)
- Configured in `config.yml` under `restrictions.enderpearl.combat-cooldown-multiplier`

### 3. Hits Display ✅
- Shows **total hits exchanged** by both players
- Format: `Your Hits / Total Hits (Percentage)`
- Example: `15/30 (50.0%)` means you landed 15 hits out of 30 total hits exchanged

### 4. Combat Duration ✅
- Shows actual combat time in seconds
- Calculated from session start to end

### 5. Damage Tracking ✅
- Accurate per-session damage dealt and received
- Separate from cumulative statistics

## 🎮 Supported Minecraft Versions

This plugin is compatible with **Paper/Spigot** servers running:

### ✅ Fully Supported (Tested)
- **1.21.10** (Latest - Primary development version)
- **1.21.9**
- **1.21.8**
- **1.21.7**
- **1.21.6**
- **1.21.5**
- **1.21.4**
- **1.21.3**
- **1.21.2**
- **1.21.1**
- **1.21.0**

### ✅ Should Work (API Compatible)
- **1.20.6**
- **1.20.5**
- **1.20.4**
- **1.20.3**
- **1.20.2**
- **1.20.1**

### ⚠️ May Work (Older Versions)
- **1.19.x** series (some features may not work)
- **1.18.x** series (limited support)

### ❌ Not Supported
- **1.17.x and below** (API changes, not compatible)
- **Bukkit/CraftBukkit** (Paper/Spigot only)

## 📋 Requirements

- **Server Type**: Paper or Spigot
- **Java Version**: Java 17 or higher (Java 21 recommended)
- **Dependencies**: None (standalone plugin)
- **Optional**: PlaceholderAPI for placeholder support

## 🔧 Configuration

### Ender Pearl Settings
```yaml
restrictions:
  enderpearl:
    enabled: true
    cooldown: 10  # Base cooldown in seconds
    combat-cooldown-multiplier: 2.0  # 2x during combat = 20 seconds
    block-usage: false  # Set to true to completely block pearls in combat
```

### Combat Duration
```yaml
combat:
  duration: 30  # Combat timer in seconds
  cooldown: 10  # Cooldown after combat ends
```

## 📊 Features

1. **Combat Tracking**
   - Per-session damage tracking
   - Hit counting
   - Combat duration
   - Win/Loss statistics

2. **Combat Logging Protection**
   - Instant death on logout
   - Inventory drop
   - Opponent notification

3. **Restrictions**
   - Ender pearl cooldown (2x during combat)
   - Elytra restrictions
   - Teleport blocking
   - Optional block placement/breaking restrictions

4. **Visual Features**
   - Boss bar timer
   - Action bar updates
   - Multiple themes (minimal, fire, ice, neon, dark, clean)
   - Theme switching during combat

5. **Commands**
   - `/combat status` - Check combat status
   - `/combat summary` - View your statistics
   - `/combat summary <player>` - View player statistics (admin)
   - `/combat toggle-style` - Change visual theme
   - `/combat reload` - Reload configuration (admin)

## 🎯 100% Working Features

✅ Combat detection and tracking
✅ Damage tracking (per-session and cumulative)
✅ Hit counting with percentage
✅ Combat duration display
✅ Combat logging death
✅ Inventory drop on death
✅ Ender pearl cooldown (2x in combat)
✅ Win/Loss recording
✅ Boss bar timer
✅ Action bar updates
✅ Theme system
✅ Statistics persistence
✅ Admin commands
✅ Player commands

## 📝 Notes

- The plugin uses Paper/Spigot API features
- Ender pearl cooldown is already implemented and working
- Combat logging kills the player immediately (not on rejoin)
- All statistics are tracked per-session and cumulatively
- Themes can be changed during combat with `/combat toggle-style`

## 🐛 Known Limitations

- Knockback exchanges are not tracked (would require additional packet listening)
- Replay system saves events but serialization needs fixing
- Some visual features require Paper (not Spigot)

## 📦 Installation

1. Download `pvpcombat-1.0.0-SNAPSHOT.jar`
2. Place in your server's `plugins` folder
3. Restart server
4. Configure `plugins/PvPCombat/config.yml` as needed
5. Reload with `/combat reload` or restart server

---

**Version**: 1.0.0-SNAPSHOT
**Author**: muzlik
**Last Updated**: 2025-11-21
