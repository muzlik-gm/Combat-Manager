# True Combat Manager

**Professional Combat Management for Minecraft Servers**

Advanced tracking, smart restrictions, and stunning visuals for competitive PvP

✅ Paper & Spigot 1.18-1.21 | Java 17+

---

## What is True Combat Manager?

True Combat Manager is a comprehensive combat management plugin for competitive Minecraft servers. It tracks every aspect of player fights with detailed statistics, enforces intelligent restrictions to prevent unfair advantages, protects against combat logging, and presents everything through a beautiful, customizable interface.

Built for performance with async processing, intelligent lag compensation, and thread-safe architecture. Perfect for practice servers, factions, KitPvP, or any PvP-focused gameplay.

### Why Choose This Plugin?

- **Stop combat logging** with instant-death protection and inventory drops
- **Prevent unfair escapes** with smart cooldowns (pearls, gapples, elytra, teleports)
- **Track player performance** with lifetime statistics (wins, losses, K/D, damage)
- **Safe zone protection** prevents players from entering protected areas during combat
- **Visual glass barriers** show at safezone boundaries
- **Creative mode protection** automatically switches creative players to survival
- **Customize everything** - 6 themes, 6 sound profiles, 100+ config options
- **Lag compensation** automatically adjusts timers during server performance issues

---

## Core Features

### Combat Tracking & Statistics
- Real-time combat detection with automatic session management
- Lifetime statistics tracked per player:
  - Total combats, wins, and losses
  - Win rate percentage and K/D ratio
  - Total damage dealt and received
  - Damage ratio (dealt/received)
  - Total combat time
  - Last combat timestamp
- Combat logging protection: instant death + full inventory drop
- Thread-safe concurrent session handling

### Smart Restrictions
- **Ender Pearl:** 10s base cooldown, 20s during combat (2.0x multiplier)
- **Golden Apple:** 3s base cooldown, 4.5s in combat (1.5x multiplier)
- **Enchanted Golden Apple:** 8s base cooldown, 16s in combat (2.0x multiplier)
- **Elytra:** Block gliding, boosting, and takeoff during combat
- **Teleport Blocking:** Prevents /tp, /home, /warp, /spawn commands
- **Safe Zone Protection:** Blocks entry to WorldGuard protected regions during combat
- **Visual Glass Barrier System:** Client-side glass barriers appear when attempting safezone entry
- **Safezone Attack Prevention:** Cannot attack from or into safe zones
- **Creative Mode Protection:** Automatically switches creative players to survival

### Visual Experience
- **BossBar Timer:** Dynamic countdown with smooth animations
- **ActionBar Updates:** Real-time opponent and timer information
- **6 Built-in Themes:** minimal, fire, ice, neon, dark, clean
- **Live Theme Switching:** Change visual style mid-combat with /combat toggle-style
- **HEX Color Support:** Full RGB color customization
- **6 Sound Profiles:** default, subtle, intense, calm, electronic, clean
- **Event Sounds:** Combat start/end, timer warning, timer reset, interference

### Performance & Optimization
- **Async Processing:** Combat logging runs off main thread with queue system
- **Intelligent Lag Compensation:** Auto-adjusts timers when TPS drops below 18.0
- **Advanced Caching:** TTL-based cache reduces database queries
- **Thread-Safe Architecture:** ConcurrentHashMap for session management
- **Memory Efficient:** Configurable max entries (default: 10,000) with automatic cleanup
- **Ping Tracking:** Per-player latency monitoring

---

## Commands

### Player Commands:
- `/combat status` - View current combat status, opponent, and health
- `/combat summary` - Display lifetime combat statistics
- `/combat toggle-style` - Cycle through visual themes

### Admin Commands:
- `/combat inspect <player>` - View real-time combat data
- `/combat summary <player>` - Access any player's statistics
- `/combat reload` - Reload configuration without restart
- `/combat debug` - Toggle debug mode with performance metrics

### Permissions:
- `pvpcombat.command.status`, `pvpcombat.command.summary`, `pvpcombat.command.toggle-style`
- `pvpcombat.admin`, `pvpcombat.admin.inspect`, `pvpcombat.admin.debug`
- `pvpcombat.bypass.combatlog`, `pvpcombat.bypass.restrictions`

---

## Configuration

Highly configurable with 100+ options:

```yaml
combat:
  duration: 30              # Combat timer (seconds)
  cooldown: 10              # Cooldown after combat

restrictions:
  enderpearl:
    cooldown: 10
    cooldown-outside-combat: false
    combat-cooldown-multiplier: 2.0
  safezone:
    enabled: true
    block-entry: true
    protected-regions:
      - "spawn"
      - "safezone"
    barrier:
      material: "GLASS"
      height: 4
      width: 5

visual:
  themes:
    default-theme: "clean"
    available: ["minimal", "fire", "ice", "neon", "dark", "clean"]
```

---

## Installation

1. Download TrueCombatManager-1.0.0.jar
2. Place in your server's `plugins` folder
3. Restart your server
4. Edit `plugins/TrueCombatManager/config.yml` to customize
5. Use `/combat reload` to apply changes

**Requirements:**
- Server: Paper or Spigot 1.18-1.21 (fully tested on 1.21.x)
- Java: 17+ (Java 21 recommended)
- Optional: PlaceholderAPI for placeholder support
- Optional: WorldGuard for safe zone protection

Works out of the box with default settings!

---

## PlaceholderAPI Support

Full PlaceholderAPI integration for scoreboard, tab list, and chat plugins:

- `%pvpcombat_in_combat%` - Combat status (true/false)
- `%pvpcombat_time_left%` - Remaining time in seconds
- `%pvpcombat_opponent%` - Current opponent's name
- `%pvpcombat_wins%` - Total wins
- `%pvpcombat_losses%` - Total losses
- `%pvpcombat_kd_ratio%` - K/D ratio
- `%pvpcombat_win_rate%` - Win rate percentage
- `%pvpcombat_total_damage_dealt%` - Total damage dealt
- `%pvpcombat_total_damage_received%` - Total damage received

---

## Support

- Contact via Hangar messages
- Report bugs through issue tracker
- Feature requests welcome

**Tested Versions:**
✅ 1.21.x series (fully tested)
✅ 1.20.x series (compatible)
✅ 1.19.x series (compatible)
⚠️ 1.18.x series (basic features)

---

**True Combat Manager v1.0.0**
Made with ❤️ for the Minecraft community
© 2025 muzlik - All Rights Reserved
