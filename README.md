# True Combat Manager v1.0.0

**Professional Combat Management for Minecraft Servers**

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/yourusername/truecombatmanager)
[![Minecraft](https://img.shields.io/badge/minecraft-1.18--1.21-green.svg)](https://www.spigotmc.org/)
[![Java](https://img.shields.io/badge/java-17%2B-orange.svg)](https://www.oracle.com/java/)

---

## Quick Start

1. Download `TrueCombatManager-1.0.0.jar`
2. Place in `plugins` folder
3. Start server
4. Done! ✅

Config auto-generates with all defaults.

---

## Features

### ✅ Combat Management
- Real-time combat detection
- Automatic session management
- 30-second combat timer (configurable)
- Lag compensation system

### ✅ Combat Logging Protection
- Instant death on logout
- Full inventory drop
- Opponent receives win
- Server broadcast

### ✅ Statistics Tracking
- Wins, losses, K/D ratio
- Damage dealt/received
- Win rate percentage
- Total combat time
- PlaceholderAPI support

### ✅ Smart Restrictions
- **Ender Pearl:** 10s base, 20s in combat
- **Golden Apple:** 3s base, 4.5s in combat
- **Enchanted Golden Apple:** 8s base, 16s in combat
- **Elytra:** Blocked during combat
- **Teleport Commands:** Blocked during combat

### ✅ Safezone Protection
- Cannot attack from safezone
- Cannot attack players in safezone
- Cannot enter safezone during combat
- **Glass barriers appear in 4-block radius**
- WorldGuard integration

### ✅ Creative Mode Protection
- Auto-switches creative to survival
- Prevents creative mode exploits

### ✅ Visual System
- 6 themes: minimal, fire, ice, neon, dark, clean
- BossBar timer with animations
- ActionBar opponent info
- 6 sound profiles
- HEX color support

---

## Commands

### Player Commands
```
/combat status          - View combat status
/combat summary         - View statistics
/combat toggle-style    - Change theme
```

### Admin Commands
```
/combat inspect <player>  - View player data
/combat reload            - Reload config
/combat debug             - Toggle debug mode
```

---

## Permissions

```
pvpcombat.command.status
pvpcombat.command.summary
pvpcombat.admin
pvpcombat.bypass.restrictions
```

---

## Configuration

Edit `plugins/TrueCombatManager/config.yml`

```yaml
config-version: 2

combat:
  duration: 30
  cooldown: 10

restrictions:
  safezone:
    enabled: true
    protected-regions:
      - "spawn"
      - "safezone"
    barrier:
      material: "GLASS"
      
  teleport:
    enabled: true
    blocked-commands:
      - "tp"
      - "home"
      - "spawn"
```

After editing: `/combat reload`

---

## PlaceholderAPI

```
%pvpcombat_in_combat%
%pvpcombat_time_left%
%pvpcombat_opponent%
%pvpcombat_wins%
%pvpcombat_losses%
%pvpcombat_kd_ratio%
%pvpcombat_win_rate%
%pvpcombat_total_damage_dealt%
%pvpcombat_total_damage_received%
```

---

## Requirements

### Required
- Paper or Spigot 1.18-1.21
- Java 17+ (Java 21 recommended)

### Optional
- WorldGuard (for safezone protection)
- PlaceholderAPI (for placeholders)

---

## How It Works

### Glass Barrier System
```
Player in combat walks toward safezone
         ↓
Plugin checks 4-block radius
         ↓
Safezone detected within radius
         ↓
Glass barriers appear at boundaries
         ↓
Player sees barriers early
         ↓
If player continues: Movement blocked
```

### Combat Flow
```
Player A hits Player B
         ↓
Combat starts (30s timer)
         ↓
Restrictions applied:
  - Teleport commands blocked
  - Cannot enter safezone
  - Item cooldowns active
         ↓
Timer expires or player dies
         ↓
Combat ends
         ↓
Statistics saved
```

---

## Testing

### Test 1: Glass Barriers
1. Start combat
2. Walk toward spawn
3. Glass appears when within 4 blocks
4. Movement blocked at boundary

### Test 2: Combat Logging
1. Start combat
2. Logout
3. Instant death + inventory drop
4. Opponent gets win

### Test 3: Command Blocking
1. Start combat
2. Try `/tp`, `/home`
3. Commands blocked
4. Combat ends → Commands work

---

## Troubleshooting

**Glass not showing?**
- Install WorldGuard
- Check config: `material: "GLASS"`
- Add region names to config
- Check console for errors

**Commands not blocked?**
- Check config: `teleport.enabled: true`
- Verify blocked-commands list
- Check bypass permission

**Config issues?**
- Plugin auto-updates old configs
- Check console for: "Created new config.yml with version 2.0"
- Old config backed up to `config.yml.backup`

---

## Support

- Check console for errors
- Use `/combat debug` for detailed logs
- Contact via marketplace messages

---

## License

All Rights Reserved © 2025 muzlik

This is proprietary software. Modification or redistribution is prohibited.

---

## Links

- [BuiltByBit](https://builtbybit.com/)
- [SpigotMC](https://www.spigotmc.org/)
- [Hangar](https://hangar.papermc.io/)
- [Modrinth](https://modrinth.com/)

---

**Made with ❤️ for the Minecraft community**

v1.0.0 | 2025-11-29
