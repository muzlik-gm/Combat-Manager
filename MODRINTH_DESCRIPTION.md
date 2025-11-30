# True Combat Manager

**Professional Combat Management for Minecraft Servers**

Advanced tracking, smart restrictions, and stunning visuals for competitive PvP

## Features

### 🎯 Combat Tracking
- Real-time combat detection with automatic session management
- Lifetime statistics: wins, losses, K/D ratio, damage dealt/received
- Combat logging protection with instant death and inventory drop
- Thread-safe concurrent session handling

### 🛡️ Smart Restrictions
- **Ender Pearl:** Configurable cooldowns (10s base, 20s in combat)
- **Golden Apples:** Smart cooldown system (3s base, 4.5s in combat)
- **Elytra:** Block gliding and boosting during combat
- **Teleport Blocking:** Prevents /tp, /home, /warp, /spawn
- **Safe Zone Protection:** Blocks entry to WorldGuard regions during combat
- **Visual Glass Barriers:** Client-side barriers at safezone boundaries
- **Safezone Attack Prevention:** Cannot attack from or into safe zones
- **Creative Mode Protection:** Auto-switches creative players to survival

### 🎨 Visual Experience
- **BossBar Timer:** Dynamic countdown with smooth animations
- **ActionBar Updates:** Real-time opponent and timer information
- **6 Built-in Themes:** minimal, fire, ice, neon, dark, clean
- **Live Theme Switching:** Change style mid-combat
- **HEX Color Support:** Full RGB customization
- **6 Sound Profiles:** default, subtle, intense, calm, electronic, clean

### ⚡ Performance
- Async processing for combat logging
- Intelligent lag compensation (auto-adjusts timers when TPS drops)
- Advanced caching system
- Thread-safe architecture
- Memory efficient with automatic cleanup

## Commands

**Player Commands:**
- `/combat status` - View current combat status
- `/combat summary` - Display lifetime statistics
- `/combat toggle-style` - Cycle through themes

**Admin Commands:**
- `/combat inspect <player>` - View real-time combat data
- `/combat reload` - Reload configuration
- `/combat debug` - Toggle debug mode

## Configuration

Highly configurable with 100+ options:

```yaml
combat:
  duration: 30
  cooldown: 10

restrictions:
  safezone:
    enabled: true
    barrier:
      material: "GLASS"
  teleport:
    enabled: true
    blocked-commands: ["tp", "home", "spawn"]

visual:
  themes:
    default-theme: "clean"
```

## PlaceholderAPI

Full PlaceholderAPI support:
- `%pvpcombat_in_combat%`
- `%pvpcombat_time_left%`
- `%pvpcombat_opponent%`
- `%pvpcombat_wins%`
- `%pvpcombat_losses%`
- `%pvpcombat_kd_ratio%`
- And more!

## Requirements

- **Server:** Paper or Spigot 1.18-1.21
- **Java:** 17+ (Java 21 recommended)
- **Optional:** PlaceholderAPI, WorldGuard

## Installation

1. Download the plugin
2. Place in `plugins` folder
3. Restart server
4. Configure in `plugins/TrueCombatManager/config.yml`
5. Use `/combat reload` to apply changes

## Tested Versions

✅ 1.21.x (fully tested)
✅ 1.20.x (compatible)
✅ 1.19.x (compatible)
⚠️ 1.18.x (basic features)

## License

All Rights Reserved © 2025 muzlik

## Links

- [Documentation](https://github.com/yourusername/truecombatmanager/wiki)
- [Issue Tracker](https://github.com/yourusername/truecombatmanager/issues)
- [Discord Support](https://discord.gg/yourserver)
