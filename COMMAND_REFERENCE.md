# PvP Combat System - Command Reference

## Overview

The PvP Combat System provides a comprehensive command interface with both player and administrative functionality. Commands are designed with permission-based access control and include intelligent tab completion.

## Command Structure

### Main Command: `/combat`

The primary command interface that delegates to specific handlers based on permissions and subcommands.

```
/combat <subcommand> [arguments]
```

**Aliases:** `/pvp`, `/combatlog`, `/cl`

### Replay Command: `/replay`

Administrative command for viewing and managing combat replays.

```
/replay <subcommand> [arguments]
```

## Player Commands

### `/combat status`

Shows the player's current combat status and opponent information.

**Permission:** `pvpcombat.command.status` (default: true)

**Usage:**
```
/combat status
```

**Output:**
- Combat state ("In Combat" or "Safe")
- Current opponent (if applicable)
- Time remaining in combat
- Opponent's health (if available)
- Helpful hints for safe logout

**Examples:**
```
Player is not in combat.
✓ You are safe to logout or teleport.
```

```
=== Combat Status ===
Status: In Combat
Opponent: Steve
Opponent Health: 8.5 / 20.0 ❤
Your Health: 12.3 / 20.0 ❤
Time Remaining: Unknown (Enhanced status pending)
Use /combat summary to view detailed fight statistics.
```

### `/combat summary`

Displays detailed statistics from the player's most recent combat session.

**Permission:** `pvpcombat.command.summary` (default: true)

**Usage:**
```
/combat summary
```

**Output:**
- Combat duration
- Hits landed vs missed
- Damage dealt/taken
- Winner determination
- Accuracy percentage

**Example Output:**
```
=== Latest Combat Summary ===
Duration: 45 seconds
Hits Traded: 12 landed, 8 missed
Damage Dealt: 156.7 HP
Damage Taken: 98.3 HP
Accuracy: 60.0%
Winner: You won!
```

### `/combat toggle-style`

Cycles through available visual themes for boss bars and messages.

**Permission:** `pvpcombat.command.toggle-style` (default: true)

**Usage:**
```
/combat toggle-style
```

**Output:**
- Confirmation of theme change
- List of available themes
- Preview of selected theme

**Example Output:**
```
Style changed to: fire
Available themes: minimal, fire, ice, neon, dark, clean
```

## Administrative Commands

### `/combat inspect <player>`

Real-time inspection of a player's combat status and statistics.

**Permission:** `pvpcombat.admin.inspect` (default: op)

**Usage:**
```
/combat inspect <player>
```

**Arguments:**
- `<player>`: Name of the player to inspect (online players only)

**Output:**
- Live combat data for target player
- Current opponent and session statistics
- Real-time health and position info

**Examples:**
```
/combat inspect Steve
```

**Output:**
```
=== Inspection: Steve ===
Status: In Combat
Opponent: Alex
Session Duration: 2m 15s
Health: Steve (12/20), Alex (8/20)
Location: world (123, 64, -456)
Hits: Steve (8), Alex (6)
Damage: Steve (145.2), Alex (98.7)
```

**Error Handling:**
- Player not online: "Player 'Steve' is not online."
- Insufficient permissions: "You don't have permission to use this command."
- Range limit exceeded: "Target player is too far away (max: 50 blocks)."
- Different worlds: "Target player is in a different world."

### `/combat summary <player>`

Administrative version showing any player's last combat summary.

**Permission:** `pvpcombat.admin` (default: op)

**Usage:**
```
/combat summary <player>
```

**Arguments:**
- `<player>`: Name of the player to check (online players only)

**Output:**
- Target player's combat history
- Last fight statistics
- Performance metrics

**Example:**
```
/combat summary Steve
```

### `/combat reload`

Reloads all configuration files with validation.

**Permission:** `pvpcombat.admin` (default: op)

**Usage:**
```
/combat reload
```

**Output:**
- Configuration reload status
- Validation results
- Error notifications if any

**Success Example:**
```
Configuration reloaded successfully!
All combat systems have been updated with new settings.
Validation: 0 errors, 0 warnings
```

**Error Example:**
```
Configuration validation errors found:
ERROR: Invalid combat duration value
ERROR: Unknown theme 'invalid_theme'
Use /combat debug for more details.
```

### `/combat debug`

Toggles debug mode for enhanced logging and diagnostics.

**Permission:** `pvpcombat.admin.debug` (default: op)

**Usage:**
```
/combat debug
```

**Output:**
- Debug mode status toggle
- Current state notification
- Usage instructions

**Example Output:**
```
Debug mode enabled!
Debug information will now be displayed in chat.
Use /combat debug again to disable.
```

## Replay System Commands

### `/replay view <session-uuid>`

Loads and displays a combat replay for analysis.

**Permission:** `pvpcombat.admin` (default: op)

**Usage:**
```
/replay view <session-uuid>
```

**Arguments:**
- `<session-uuid>`: The session UUID to replay (from logs or admin summaries)

**Output:**
- Replay data loading confirmation
- Session information
- Timeline availability notice

**Example:**
```
/replay view 123e4567-e89b-12d3-a456-426614174000
```

**Output:**
```
=== Combat Replay ===
Session: 123e4567-e89b-12d3-a456-426614174000
Data loaded successfully. GUI replay viewing not yet implemented.
Raw JSON data available for frontend integration.
```

**Error Handling:**
- Invalid UUID: "Invalid session UUID format."
- Session not found: "No replay data found for session: [UUID]"
- System disabled: "Replay system is not enabled."

### `/replay stats`

Displays system-wide replay statistics and performance metrics.

**Permission:** `pvpcombat.admin` (default: op)

**Usage:**
```
/replay stats
```

**Output:**
- Active combat sessions
- Memory usage statistics
- Cached replay count
- System performance metrics

**Example Output:**
```
=== Replay System Statistics ===
Active Sessions: 3
Total Events: 1,247
Memory Usage: 45.2 MB
Cached Replays: 12
```

### `/replay clear <session-uuid>`

Removes replay data for a specific combat session.

**Permission:** `pvpcombat.admin` (default: op)

**Usage:**
```
/replay clear <session-uuid>
```

**Arguments:**
- `<session-uuid>`: The session UUID to clear

**Output:**
- Data clearing confirmation
- System status update

**Example:**
```
/replay clear 123e4567-e89b-12d3-a456-426614174000
```

**Note:** Implementation pending in current version.

## Command Aliases

| Primary Command | Aliases |
|----------------|---------|
| `/combat` | `/pvp`, `/combatlog`, `/cl` |
| `/replay` | None |

## Tab Completion

All commands support intelligent tab completion:

### Main Command Tab Completion (`/combat`)
- **No arguments:** Shows available subcommands based on permissions
- **First argument:** Filters subcommands by partial input
- **Admin subcommands:** Shows online players for applicable commands

**Examples:**
```
/combat <TAB>     # Shows: status, summary, toggle-style (player commands)
/combat i<TAB>    # Shows: inspect (if admin)
/combat inspect <TAB>  # Shows online players (max 10)
```

### Replay Command Tab Completion (`/replay`)
- **No arguments:** Shows available subcommands
- **First argument:** Filters subcommands by partial input
- **view/clear:** Example UUID format suggestions

**Examples:**
```
/replay <TAB>     # Shows: view, stats, clear
/replay v<TAB>    # Shows: view
/replay view <TAB>  # Shows: example-uuid-here
```

## Permission System

### Player Permissions
```
pvpcombat.command.status          # View combat status
pvpcombat.command.summary         # View combat summaries
pvpcombat.command.toggle-style    # Change visual themes
pvpcombat.use                     # Basic plugin usage (auto-granted)
```

### Administrative Permissions
```
pvpcombat.admin                   # Full admin access
pvpcombat.admin.inspect          # Player inspection tools
pvpcombat.admin.debug            # Debug mode access
pvpcombat.admin.replay           # Replay system access
```

### Bypass Permissions
```
pvpcombat.bypass.combatlog       # Immune to combat logging penalties
pvpcombat.bypass.restrictions    # Ignore movement restrictions
pvpcombat.bypass.timer          # Combat timer doesn't apply
pvpcombat.bypass.all            # Complete immunity (dangerous!)
```

## Error Messages and Handling

### Common Error Messages

| Error Message | Cause | Solution |
|---------------|-------|----------|
| "This command can only be used by players." | Used from console | Use player account |
| "You don't have permission to use this command." | Missing permission | Grant appropriate permission |
| "Player 'name' is not online." | Target player offline | Check player name spelling |
| "Invalid session UUID format." | Malformed UUID | Check UUID format |
| "Configuration validation errors found" | Config file errors | Check config.yml syntax |
| "Replay system is not enabled." | Replay disabled | Enable in config.yml |

### Error Response Format

All commands follow consistent error response formatting:
- **Permission errors:** Red text with clear permission name
- **Validation errors:** Yellow text with specific issue
- **System errors:** Red text with suggestion to check console
- **Usage errors:** Yellow text with correct syntax

## Command Integration

### PlaceholderAPI Integration

Commands work seamlessly with PlaceholderAPI expansions:

**Examples:**
```
/papi parse <player> %pvpcombat_status%
/papi parse <player> %pvpcombat_time_left%
/papi parse <player> %pvpcombat_opponent%
```

### Plugin Hook Support

Commands integrate with popular plugins:

**LuckPerms/Vault:** Permission checking
**Essentials:** Teleport command blocking
**WorldGuard:** Region-based restrictions
**ProtocolLib:** Enhanced packet handling

## Performance Considerations

### Command Processing
- All commands are processed asynchronously where possible
- Tab completion is limited to prevent lag (max 10 suggestions)
- Large result sets are paginated automatically
- Memory-intensive operations have configurable limits

### Administrative Limits
- Player inspection range limits (default: 50 blocks)
- Session listing limits (configurable)
- Replay data size limits
- Concurrent admin operations throttled

## Help System

### Built-in Help

Commands include contextual help:

```
/combat     # Shows available subcommands based on permissions
/replay     # Shows replay system help
```

### Help Output Examples

**Player Help:**
```
=== Combat Commands ===
/combat status - Shows your combat state
/combat summary - Shows your latest PvP fight summary
/combat toggle-style - Choose message and theme styles
```

**Admin Help:**
```
=== Combat Commands ===
/combat status - Shows your combat state
/combat summary - Shows your latest PvP fight summary
/combat toggle-style - Choose message and theme styles
=== Admin Commands ===
/combat inspect <player> - View real-time combat info
/combat summary <player> - Access last combat stats
/combat reload - Reload configuration
/combat debug - Toggle debug mode
```

**Replay Help:**
```
=== Combat Replay Commands ===
/replay view <session-uuid> - View replay for session
/replay stats - Show replay system statistics
/replay clear <session-uuid> - Clear replay data for session
Note: Session UUIDs can be found in combat logs
```

## Configuration Integration

Commands respect all configuration settings:

- **Language settings:** Error messages in configured language
- **Permission settings:** Custom permission nodes
- **Visual settings:** Theme-aware help formatting
- **Performance settings:** Rate limiting and caching

## Troubleshooting Command Issues

### Commands Not Working
1. **Check permissions:** Use `/lp user <player> permission info` (LuckPerms)
2. **Verify plugin load:** Check `/plugins` for green status
3. **Check console errors:** Look for command registration failures
4. **Test basic command:** Try `/combat status` first

### Tab Completion Issues
1. **Permission check:** Ensure player has command permissions
2. **Plugin conflicts:** Disable other chat/command plugins temporarily
3. **Client issues:** Some clients disable tab completion

### Performance Issues
1. **High latency:** Check server TPS with `/tps`
2. **Memory usage:** Monitor with `/replay stats`
3. **Concurrent users:** Reduce max sessions in config

### Integration Problems
1. **PlaceholderAPI:** Verify PAPI is loaded with `/papi`
2. **Permission plugins:** Check permission plugin compatibility
3. **Other plugins:** Test with minimal plugin set

## Future Enhancements

### Planned Command Features
- **Pagination:** For large result sets
- **Filtering:** Advanced search and filter options
- **Export:** Data export functionality
- **GUI Interface:** Web-based admin interface
- **API Endpoints:** REST API for external tools
- **Scheduled Tasks:** Automated maintenance commands

### Command Extensions
- **Custom commands:** Plugin API for custom command registration
- **Macro system:** Command sequences and automation
- **Remote access:** Console access with enhanced features
- **Audit logging:** Command usage tracking and analytics