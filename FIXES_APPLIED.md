# Bug Fixes Applied - v1.0.0-FIXED

## Issues Fixed

### 1. Command Blocking During Combat ✅
**Problem:** Players could use teleport commands during combat, allowing them to escape unfairly.

**Solution:**
- Added `PlayerCommandPreprocessEvent` handler in `CombatEventListener.java`
- Blocks all teleport-related commands during combat
- Configurable list of blocked commands in `config.yml`
- Respects bypass permission: `pvpcombat.bypass.restrictions`
- Customizable blocked message

**Blocked Commands (default):**
- `/tp`, `/teleport`
- `/home`, `/spawn`, `/warp`
- `/tpa`, `/tpaccept`
- `/back`, `/wild`, `/rtp`

**Configuration:**
```yaml
restrictions:
  teleport:
    enabled: true
    blocked-commands:
      - "tp"
      - "teleport"
      - "home"
      # ... more commands
    blocked-message: "&cYou cannot use teleport commands during combat!"
```

---

### 2. Safe Zone Entry During Combat ✅
**Problem:** Players could enter safe zones (WorldGuard regions) while in combat, escaping fights.

**Solution:**
- Registered `SafeZoneManager` as an event listener in `PluginManager.java`
- Now properly listens to `PlayerMoveEvent` and blocks entry to protected regions
- Shows visual barrier blocks when attempting to enter
- Plays sound effect and sends message
- Fully integrated with WorldGuard

**Features:**
- Blocks entry to configured WorldGuard regions during combat
- Client-side visual barriers (no world modification)
- Configurable barrier material, height, width, and duration
- Customizable blocked message

**Configuration:**
```yaml
restrictions:
  safezone:
    enabled: true
    block-entry: true
    protected-regions:
      - "spawn"
      - "safezone"
      - "safe"
    blocked-message: "&cYou cannot enter a safe zone while in combat!"
    barrier:
      enabled: true
      material: "BARRIER"
      height: 4
      width: 5
      duration-ticks: 6
```

---

### 3. Knockback Exchanges Tracking ✅
**Problem:** Knockback exchanges (back-and-forth hits) were not being tracked or exposed via PlaceholderAPI.

**Solution:**
- Added knockback exchange tracking to `CombatSession.java`
- Tracks when damage dealer switches between players within 3 seconds
- Created full PlaceholderAPI expansion: `PvPCombatExpansion.java`
- Registered expansion in `PvPCombatPlugin.java`
- Added calculation methods to `PlayerCombatData.java`

**New PlaceholderAPI Placeholders:**

**Combat Status:**
- `%pvpcombat_in_combat%` - true/false
- `%pvpcombat_time_left%` - Remaining seconds
- `%pvpcombat_opponent%` - Opponent name

**Lifetime Statistics:**
- `%pvpcombat_wins%` - Total wins
- `%pvpcombat_losses%` - Total losses
- `%pvpcombat_total_combats%` - Total combats
- `%pvpcombat_kd_ratio%` - K/D ratio (formatted)
- `%pvpcombat_win_rate%` - Win rate percentage
- `%pvpcombat_total_damage_dealt%` - Total damage dealt
- `%pvpcombat_total_damage_received%` - Total damage received
- `%pvpcombat_damage_ratio%` - Damage ratio (dealt/received)
- `%pvpcombat_total_combat_time%` - Total time in combat

**Session-Specific (Current Combat):**
- `%pvpcombat_session_damage_dealt%` - Damage dealt in current fight
- `%pvpcombat_session_damage_received%` - Damage received in current fight
- `%pvpcombat_session_hits_landed%` - Hits landed in current fight
- `%pvpcombat_knockback_exchanges%` - **NEW!** Knockback exchanges in current fight

---

## Files Modified

### Core Files:
1. `src/main/java/com/muzlik/pvpcombat/events/CombatEventListener.java`
   - Added command blocking handler
   - Added imports for command event

2. `src/main/java/com/muzlik/pvpcombat/core/PluginManager.java`
   - Registered SafeZoneManager as listener

3. `src/main/java/com/muzlik/pvpcombat/core/PvPCombatPlugin.java`
   - Registered PlaceholderAPI expansion

4. `src/main/java/com/muzlik/pvpcombat/data/CombatSession.java`
   - Added knockback exchange tracking
   - Added fields: `knockbackExchanges`, `lastDamager`, `lastDamageTime`
   - Modified `recordDamage()` to track exchanges
   - Added `getKnockbackExchanges()` method

5. `src/main/java/com/muzlik/pvpcombat/data/PlayerCombatData.java`
   - Added `getKDRatio()` method
   - Added `getWinRate()` method
   - Added `getDamageRatio()` method

### New Files:
6. `src/main/java/com/muzlik/pvpcombat/integration/PvPCombatExpansion.java`
   - **NEW!** Complete PlaceholderAPI expansion
   - Exposes all combat statistics and session data

### Configuration:
7. `src/main/resources/config.yml`
   - Updated teleport restrictions section
   - Added `blocked-message` option
   - Fixed command list format (removed leading slashes)

---

## Testing Checklist

### Command Blocking:
- [ ] Try `/tp` during combat - should be blocked
- [ ] Try `/home` during combat - should be blocked
- [ ] Try `/spawn` during combat - should be blocked
- [ ] Test with bypass permission - should work
- [ ] Verify custom message appears

### Safe Zone Protection:
- [ ] Create WorldGuard region named "spawn" or "safezone"
- [ ] Enter combat with another player
- [ ] Try to enter the safe zone - should be blocked
- [ ] Verify visual barrier appears
- [ ] Verify sound plays
- [ ] Verify message appears
- [ ] After combat ends, verify you can enter normally

### Knockback Exchanges:
- [ ] Install PlaceholderAPI
- [ ] Use `/papi parse me %pvpcombat_knockback_exchanges%`
- [ ] Start combat and trade hits back and forth
- [ ] Verify counter increases when hits alternate within 3 seconds
- [ ] Test in scoreboard or hologram display

---

## Installation

1. Stop your server
2. Replace the old plugin JAR with `TrueCombatManager-1.0.0-FIXED.jar`
3. Start your server
4. Test all three fixes above
5. If using PlaceholderAPI, run `/papi reload` to register new placeholders

---

## Notes

- All fixes are backward compatible with existing configurations
- No database changes required
- PlaceholderAPI is optional but recommended for full functionality
- WorldGuard is optional but required for safe zone protection
- Command blocking works with any teleport plugin (Essentials, CMI, etc.)

---

## Support

If you encounter any issues with these fixes:
1. Check server console for errors
2. Verify config.yml syntax is correct
3. Test with debug mode enabled: `/combat debug`
4. Report bugs with full error logs
