# TrueCombatManager - Console Logging Control Added

## Build Information
**File:** `TrueCombatManager-1.0.0-WITH-LOGGING-CONTROL.jar`  
**Location:** `_Versions/TrueCombatManager-1.0.0-WITH-LOGGING-CONTROL.jar`  
**Build Date:** 2025-11-30  
**Status:** ✅ CONSOLE LOGGING CONTROL ADDED

---

## NEW FEATURE: Console Logging Control

### Overview
Added a new `/combat logging` command that allows admins to control what gets logged to the console. This keeps your console clean while still allowing you to enable detailed logging when needed for debugging.

---

## Command Usage

### Check Current Status:
```
/combat logging
```

**Output:**
```
▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬
Console Logging Status

Current Status: DISABLED ✗

When enabled, the following will be logged:
  • Combat start/end events
  • Damage dealt/received
  • Newbie protection checks
  • Restriction blocks (trident, ender pearl, etc.)
  • Command blocks

Usage: /combat logging <enabled|disabled>
▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬
```

### Enable Logging:
```
/combat logging enabled
```

**Output:**
```
▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬
Console Logging Enabled

✓ Console logging has been enabled!

The following will now be logged to console:
  • Combat events (start/end)
  • Damage tracking
  • Newbie protection checks
  • Restriction blocks
  • Command blocks

This is useful for debugging and monitoring.

Use /combat logging to check status anytime.
▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬
```

### Disable Logging:
```
/combat logging disabled
```

**Output:**
```
▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬
Console Logging Disabled

✗ Console logging has been disabled!

Combat events will no longer spam the console.
Only errors will be logged.

This keeps your console clean and improves performance.

Use /combat logging to check status anytime.
▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬
```

---

## Configuration

### New Config Section:
```yaml
# ========================================
# LOGGING SETTINGS
# ========================================
logging:
  # Enable/disable console logging for combat events
  # When disabled, only errors will be logged to console
  # Use /combat logging <enabled|disabled> to toggle in-game
  # true = Log combat events to console | false = Silent mode (only errors)
  console-enabled: false
  
  # What to log when console-enabled is true:
  # - Combat start/end events
  # - Damage dealt/received
  # - Newbie protection checks
  # - Restriction blocks (trident, ender pearl, etc.)
  # - Command blocks
  # - Safezone interactions
```

---

## What Gets Logged

### When Enabled (`console-enabled: true`):
- ✅ `[DAMAGE] PlayerA dealt 5.0 to PlayerB (Total: 15.0)`
- ✅ `[NEWBIE PROTECTION] Blocked PlayerA (newbie) from attacking PlayerB`
- ✅ `[NEWBIE CHECK] PlayerA - Level: 2, Threshold: 3`
- ✅ `[ARMOR CHECK] PlayerA - Helmet:false Chest:false Legs:false Boots:false`
- ✅ `[TRIDENT] Blocked PlayerA from using trident in combat`
- ✅ `[ENDERPEARL] Blocked PlayerA from using ender pearl in combat`
- ✅ `[RESPAWN ANCHOR] Blocked PlayerA from using respawn anchor in combat`
- ✅ `[COMMAND BLOCK] Blocked PlayerA from using /tp in combat`
- ✅ `Combat started between PlayerA and PlayerB`
- ✅ `Combat ended - Winner: PlayerA, Loser: PlayerB`

### When Disabled (`console-enabled: false`):
- ❌ No combat event logging
- ❌ No damage tracking
- ❌ No newbie protection logs
- ❌ No restriction block logs
- ✅ **Only errors are logged** (important issues)

---

## Benefits

### Clean Console:
- No more spam from combat events
- Easier to see important errors
- Better performance (less I/O)

### Easy Debugging:
- Enable logging when you need to debug
- See exactly what's happening
- Disable when done

### Persistent Setting:
- Setting is saved to config
- Survives server restarts
- Can be changed in-game or in config file

---

## Permission

**Required Permission:** `pvpcombat.admin`

This is the same permission required for other admin commands like:
- `/combat inspect`
- `/combat reload`
- `/combat debug`

---

## Tab Completion

The command supports tab completion:
```
/combat logging <TAB>
  → enabled
  → disabled
```

---

## Aliases

You can use any of these:
- `/combat logging enabled`
- `/combat logging enable`
- `/combat logging on`
- `/combat logging true`

- `/combat logging disabled`
- `/combat logging disable`
- `/combat logging off`
- `/combat logging false`

---

## Technical Details

### New Classes Added:
1. **LoggingManager.java**
   - Manages console logging state
   - Provides methods to enable/disable logging
   - Saves settings to config

### Modified Classes:
1. **AdminCommand.java**
   - Added `handleLoggingCommand()` method
   - Added tab completion for logging command
   - Enhanced UI/UX with formatted messages

2. **PvPCombatPlugin.java**
   - Added `LoggingManager` instance
   - Added `getLoggingManager()` getter

3. **config.yml**
   - Added `logging` section
   - Added `console-enabled` option

---

## UI/UX Improvements

### Enhanced Visual Feedback:
- ✅ Colored messages (green for enabled, red for disabled)
- ✅ Box borders for better readability
- ✅ Clear status indicators (✓ and ✗)
- ✅ Detailed explanations of what will be logged
- ✅ Usage hints

### Better User Experience:
- ✅ Shows current status when no argument provided
- ✅ Accepts multiple aliases (enabled/enable/on/true)
- ✅ Saves setting automatically
- ✅ Provides helpful feedback messages
- ✅ Tab completion support

---

## Example Usage Scenarios

### Scenario 1: Normal Operation
```
Server is running normally, you don't need combat logs.
→ Keep logging disabled (default)
→ Console stays clean
→ Only errors are shown
```

### Scenario 2: Debugging Newbie Protection
```
Players report newbie protection isn't working.
→ Enable logging: /combat logging enabled
→ Test with players
→ Check console for [NEWBIE CHECK] and [ARMOR CHECK] logs
→ See exactly what's happening
→ Fix the issue
→ Disable logging: /combat logging disabled
```

### Scenario 3: Monitoring Combat
```
You want to see who's fighting and track damage.
→ Enable logging: /combat logging enabled
→ Watch console for combat events
→ See damage dealt/received
→ Monitor combat outcomes
→ Disable when done
```

---

## Migration from Old System

### Before:
- All combat events were always logged
- No way to disable logging except editing code
- Console was always spammed
- `debug-mode: false` didn't help with combat logs

### After:
- Logging is disabled by default
- Can be toggled with `/combat logging`
- Console stays clean
- Enable only when needed

---

## Summary

**New Command:** `/combat logging <enabled|disabled>`  
**Permission:** `pvpcombat.admin`  
**Default State:** Disabled (clean console)  
**Config Option:** `logging.console-enabled`  

**Benefits:**
- ✅ Clean console by default
- ✅ Easy to enable for debugging
- ✅ Enhanced UI/UX with formatted messages
- ✅ Persistent setting (survives restarts)
- ✅ Tab completion support

---

**Build Status:** ✅ SUCCESS  
**Feature:** ✅ WORKING  
**UI/UX:** ✅ ENHANCED  
**Ready for Use:** ✅ YES
