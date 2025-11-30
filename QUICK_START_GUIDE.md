# True Combat Manager - Quick Start Guide

## Installation (30 seconds)

1. Download `TrueCombatManager-1.0.0-FINAL-RELEASE.jar`
2. Put in `plugins` folder
3. Start server
4. Done! ✅

Config auto-generates with all defaults.

---

## What It Does

### Automatically:
- ✅ Tracks all combat (wins, losses, damage, K/D)
- ✅ Blocks combat logging (instant death + inventory drop)
- ✅ Prevents teleport commands during combat
- ✅ Blocks safezone entry during combat
- ✅ Shows glass barriers at safezone boundaries
- ✅ Switches creative players to survival
- ✅ Prevents attacking from/into safezones

### Visual:
- BossBar timer countdown
- ActionBar opponent info
- Glass barriers at safezones
- 6 themes, 6 sound profiles

---

## Key Features

**Combat Timer:** 30 seconds (configurable)
**Cooldowns:** Pearls, gapples, elytra
**Protection:** Safezones, creative mode, combat logging
**Stats:** Lifetime tracking with PlaceholderAPI

---

## Commands

**Players:**
- `/combat status` - Check combat status
- `/combat summary` - View your stats

**Admins:**
- `/combat reload` - Reload config
- `/combat inspect <player>` - View player data

---

## Configuration

Edit `plugins/TrueCombatManager/config.yml`

**Most Important Settings:**
```yaml
combat:
  duration: 30  # Combat timer in seconds

restrictions:
  safezone:
    enabled: true
    protected-regions:
      - "spawn"  # Add your region names
      
  teleport:
    enabled: true
```

After editing: `/combat reload`

---

## Requirements

**Required:**
- Paper or Spigot 1.18-1.21
- Java 17+

**Optional:**
- WorldGuard (for safezone protection)
- PlaceholderAPI (for placeholders)

---

## Testing (2 minutes)

1. **Combat:** Hit another player → Timer starts
2. **Logging:** Logout during combat → Instant death
3. **Commands:** Try `/tp` during combat → Blocked
4. **Safezone:** Walk to spawn during combat → Glass barrier appears
5. **Creative:** Hit someone in creative → Switched to survival

---

## Troubleshooting

**Glass not showing?**
- Check console: "SafeZone barrier material set to: GLASS"
- Install WorldGuard
- Add region names to config

**Commands not blocked?**
- Check config: `restrictions.teleport.enabled: true`
- Check player doesn't have `pvpcombat.bypass.restrictions`

**Config issues?**
- Delete `config.yml`
- Restart server
- New config auto-generates

---

## PlaceholderAPI

Add to scoreboard/tab:
```
%pvpcombat_in_combat%
%pvpcombat_time_left%
%pvpcombat_wins%
%pvpcombat_losses%
%pvpcombat_kd_ratio%
```

---

## Support

- Check console for errors
- Use `/combat debug` for detailed logs
- Contact via marketplace messages

---

## That's It!

Plugin works out of the box with sensible defaults.
Customize as needed in config.yml.

**Enjoy! 🎉**
