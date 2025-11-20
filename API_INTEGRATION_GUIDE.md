# PvP Combat System - Developer API Integration Guide

## Overview

The PvP Combat System provides a comprehensive, type-safe API designed for seamless integration with other plugins and custom server features. This guide covers everything developers need to know to integrate with the system.

## Getting Started

### Maven Dependency

Add the PvP Combat API to your project:

```xml
<redependencies>
    <dependency>
        <groupId>com.github.muzlik</groupId>
        <artifactId>pvp-combat</artifactId>
        <version>1.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

For snapshots, add the repository:
```xml
<reepositories>
    <repository>
        <id>jitpack</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

### Gradle Dependency

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.muzlik:pvp-combat:1.0.0'
}
```

### Plugin Instance Access

Get access to the PvP Combat plugin instance:

```java
import com.muzlik.pvpcombat.core.PvPCombatPlugin;

public class YourPlugin extends JavaPlugin {

    private PvPCombatPlugin combatPlugin;

    @Override
    public void onEnable() {
        // Get PvP Combat instance
        combatPlugin = PvPCombatPlugin.getInstance();

        if (combatPlugin == null) {
            getLogger().warning("PvP Combat plugin not found!");
            return;
        }

        // Plugin is available - proceed with integration
        initializeCombatIntegration();
    }
}
```

## Core API Interfaces

### ICombatManager - Combat Session Management

The primary interface for managing combat sessions and player states.

```java
import com.muzlik.pvpcombat.interfaces.ICombatManager;

public class CombatIntegration {

    private final ICombatManager combatManager;

    public CombatIntegration(PvPCombatPlugin plugin) {
        this.combatManager = plugin.getCombatManager();
    }

    // Check if a player is in combat
    public boolean isPlayerInCombat(Player player) {
        return combatManager.isInCombat(player);
    }

    // Get a player's current opponent
    public Player getOpponent(Player player) {
        return combatManager.getOpponent(player);
    }

    // Get detailed combat session information
    public CombatSession getCombatSession(Player player) {
        return combatManager.getCombatSession(player);
    }

    // Force end combat for a player
    public void forceEndCombat(Player player) {
        combatManager.forceEndCombat(player);
    }

    // Force start combat between two players
    public void startCombat(Player attacker, Player defender) {
        combatManager.forceStartCombat(attacker, defender);
    }

    // Get all active combat sessions
    public Collection<CombatSession> getAllActiveSessions() {
        return combatManager.getActiveSessions();
    }
}
```

### IVisualManager - Visual Feedback Control

Control visual feedback elements like boss bars and messages.

```java
import com.muzlik.pvpcombat.interfaces.IVisualManager;

public class VisualIntegration {

    private final IVisualManager visualManager;

    public VisualIntegration(PvPCombatPlugin plugin) {
        this.visualManager = plugin.getVisualManager();
    }

    // Show boss bar with specific theme
    public void showCombatBossBar(Player player, String theme) {
        visualManager.showBossBar(player, theme);
    }

    // Hide boss bar
    public void hideCombatBossBar(Player player) {
        visualManager.hideBossBar(player);
    }

    // Update boss bar display
    public void updateBossBar(Player player) {
        visualManager.updateBossBar(player);
    }

    // Send action bar message
    public void sendActionBarMessage(Player player, String message) {
        visualManager.sendActionBar(player, message);
    }

    // Play combat sound effect
    public void playCombatSound(Player player, SoundEvent sound) {
        visualManager.playSound(player, sound);
    }
}
```

### IRestrictionManager - Movement Restrictions

Manage movement and interaction restrictions during combat.

```java
import com.muzlik.pvpcombat.interfaces.IRestrictionManager;

public class RestrictionIntegration {

    private final IRestrictionManager restrictionManager;

    public RestrictionIntegration(PvPCombatPlugin plugin) {
        this.restrictionManager = plugin.getRestrictionManager();
    }

    // Check if player can use ender pearl
    public boolean canUseEnderPearl(Player player) {
        return restrictionManager.canUseEnderPearl(player);
    }

    // Check if player can glide with elytra
    public boolean canGlide(Player player) {
        return restrictionManager.canGlide(player);
    }

    // Check if player can teleport
    public boolean canTeleport(Player player) {
        return restrictionManager.canTeleport(player);
    }

    // Apply movement restriction
    public void restrictMovement(Player player, RestrictionType type) {
        restrictionManager.applyRestriction(player, type);
    }

    // Remove movement restriction
    public void allowMovement(Player player, RestrictionType type) {
        restrictionManager.removeRestriction(player, type);
    }
}
```

## Data Access APIs

### Combat Statistics

Access detailed combat statistics and performance data:

```java
import com.muzlik.pvpcombat.data.PlayerStatistics;
import com.muzlik.pvpcombat.data.CombatStatistics;
import com.muzlik.pvpcombat.logging.CombatLogger;

public class StatisticsIntegration {

    private final CombatLogger combatLogger;

    public StatisticsIntegration(PvPCombatPlugin plugin) {
        this.combatLogger = plugin.getCombatLogger();
    }

    // Get player statistics
    public PlayerStatistics getPlayerStats(Player player) {
        return combatLogger.getPlayerStatistics(player.getUniqueId());
    }

    // Get recent combat summary
    public CombatSummary getLastCombat(Player player) {
        return combatLogger.getLastCombatSummary(player.getUniqueId());
    }

    // Calculate player performance metrics
    public double getWinRate(Player player) {
        PlayerStatistics stats = getPlayerStats(player);
        return stats != null ? stats.getWinRate() : 0.0;
    }

    public String getFavoriteWeapon(Player player) {
        PlayerStatistics stats = getPlayerStats(player);
        return stats != null ? stats.getFavoriteWeapon() : "Unknown";
    }

    public double getAccuracy(Player player) {
        PlayerStatistics stats = getPlayerStats(player);
        return stats != null ? stats.getOverallAccuracy() : 0.0;
    }
}
```

### Real-Time Combat Data

Access live combat session data:

```java
import com.muzlik.pvpcombat.data.CombatSession;
import com.muzlik.pvpcombat.data.CombatStatistics;

public class LiveDataIntegration {

    private final ICombatManager combatManager;

    public LiveDataIntegration(PvPCombatPlugin plugin) {
        this.combatManager = plugin.getCombatManager();
    }

    // Get live session data
    public CombatSessionData getLiveSessionData(Player player) {
        CombatSession session = combatManager.getCombatSession(player);
        if (session == null) return null;

        return new CombatSessionData(
            session.getSessionId(),
            session.getAttacker(),
            session.getDefender(),
            session.getStartTime(),
            session.getStatistics()
        );
    }

    // Get combat duration
    public long getCombatDuration(Player player) {
        CombatSession session = combatManager.getCombatSession(player);
        return session != null ? session.getDurationSeconds() : 0;
    }

    // Get real-time statistics
    public LiveStats getLiveStats(Player player) {
        CombatSession session = combatManager.getCombatSession(player);
        if (session == null) return null;

        CombatStatistics stats = session.getStatistics();
        return new LiveStats(
            stats.getHitsLanded(),
            stats.getDamageDealt(),
            stats.getAccuracy()
        );
    }
}
```

## Event System

### Combat Events

Listen to combat lifecycle events:

```java
import com.muzlik.pvpcombat.events.CombatStartEvent;
import com.muzlik.pvpcombat.events.CombatEndEvent;
import com.muzlik.pvpcombat.events.CombatTickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CombatEventListener implements Listener {

    // Combat starts
    @EventHandler
    public void onCombatStart(CombatStartEvent event) {
        Player attacker = event.getAttacker();
        Player defender = event.getDefender();
        UUID sessionId = event.getSessionId();

        // Log combat start
        getLogger().info(String.format("Combat started: %s vs %s (Session: %s)",
            attacker.getName(), defender.getName(), sessionId));

        // Custom logic
        onCombatStart(attacker, defender, sessionId);
    }

    // Combat ends
    @EventHandler
    public void onCombatEnd(CombatEndEvent event) {
        Player winner = event.getWinner();
        Player loser = event.getLoser();
        CombatEndEvent.CombatEndReason reason = event.getReason();
        CombatSession session = event.getCombatSession();

        // Process results
        if (winner != null) {
            awardCombatReward(winner, session.getStatistics());
        }

        // Log detailed results
        logCombatResults(session);
    }

    // Combat timer ticks (every second)
    @EventHandler
    public void onCombatTick(CombatTickEvent event) {
        Player player = event.getPlayer();
        int secondsLeft = event.getSecondsLeft();

        // Real-time processing
        if (secondsLeft <= 5) {
            // Warning near end of combat
            sendWarning(player, secondsLeft);
        }

        // Update external systems
        updateLiveStats(player, event.getSession());
    }
}
```

### Advanced Event Handling

```java
import com.muzlik.pvpcombat.events.InterferenceDetectedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class AdvancedCombatListener implements Listener {

    // High priority interference handling
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInterference(InterferenceDetectedEvent event) {
        Player interferer = event.getInterferer();
        Player target = event.getTarget();
        Player opponent = event.getOpponent();

        // Custom interference logic
        if (shouldAllowInterference(interferer, target)) {
            // Allow the interference
            return;
        }

        // Cancel the interference
        event.setCancelled(true);

        // Notify players
        notifyInterferenceBlocked(interferer, target, opponent);
    }

    // Low priority cleanup
    @EventHandler(priority = EventPriority.MONITOR)
    public void onCombatEndMonitor(CombatEndEvent event) {
        // Cleanup external data
        cleanupCombatData(event.getCombatSession());
    }
}
```

## Custom Implementation Interfaces

### Custom Combat Handler

Implement your own combat detection and management logic:

```java
import com.muzlik.pvpcombat.interfaces.ICombatHandler;

public class CustomCombatHandler implements ICombatHandler {

    @Override
    public boolean shouldStartCombat(Player attacker, Player defender, double damage) {
        // Custom combat detection logic
        // Example: Don't start combat in safe zones
        if (isInSafeZone(attacker.getLocation()) || isInSafeZone(defender.getLocation())) {
            return false;
        }

        // Check faction relationships
        if (isSameFaction(attacker, defender)) {
            return false;
        }

        // Minimum damage threshold
        return damage >= getMinimumDamageThreshold();
    }

    @Override
    public int getCombatDuration(Player player) {
        // Dynamic duration based on player stats
        int baseDuration = 30; // seconds

        // VIP players get longer combat
        if (player.hasPermission("combat.vip")) {
            baseDuration += 15;
        }

        // High-level players get shorter combat
        if (player.getLevel() > 50) {
            baseDuration -= 5;
        }

        return Math.max(10, baseDuration); // Minimum 10 seconds
    }

    @Override
    public boolean shouldPreventLogout(Player player) {
        // Custom logout prevention logic
        CombatSession session = getCombatSession(player);
        if (session == null) return false;

        // Allow logout if player is winning significantly
        CombatStatistics stats = session.getStatistics();
        double healthPercentage = player.getHealth() / player.getMaxHealth();
        double opponentHealth = getOpponent(player).getHealth() / getOpponent(player).getMaxHealth();

        return healthPercentage < 0.2 || opponentHealth > healthPercentage;
    }
}
```

### Custom Visual System

Create custom visual feedback implementations:

```java
import com.muzlik.pvpcombat.interfaces.IVisualSystem;

public class CustomVisualSystem implements IVisualSystem {

    @Override
    public void showCombatTimer(Player player, int secondsLeft) {
        // Custom timer display (e.g., title, holograms, etc.)
        String title = getTimerTitle(secondsLeft);
        String subtitle = getTimerSubtitle(secondsLeft);

        player.sendTitle(title, subtitle, 0, 20, 10);
    }

    @Override
    public void hideCombatTimer(Player player) {
        player.resetTitle();
    }

    @Override
    public void showCombatStart(Player attacker, Player defender) {
        // Custom combat start effects
        playCustomSound(attacker, SoundType.COMBAT_START);
        playCustomSound(defender, SoundType.COMBAT_START);

        showCustomParticles(attacker.getLocation(), ParticleType.SWORD_CLASH);
        showCustomParticles(defender.getLocation(), ParticleType.SWORD_CLASH);
    }

    @Override
    public void showCombatEnd(Player winner, Player loser, CombatEndEvent.CombatEndReason reason) {
        // Custom combat end effects
        if (winner != null) {
            showVictoryEffects(winner);
            if (loser != null) {
                showDefeatEffects(loser);
            }
        }

        // Broadcast results
        broadcastCombatResult(winner, loser, reason);
    }

    private String getTimerTitle(int secondsLeft) {
        if (secondsLeft <= 5) {
            return "§c⚠ " + secondsLeft + " §c⚠";
        } else if (secondsLeft <= 10) {
            return "§e" + secondsLeft;
        } else {
            return "§a" + secondsLeft;
        }
    }

    private String getTimerSubtitle(int secondsLeft) {
        if (secondsLeft <= 5) {
            return "§cCombat ending soon!";
        }
        return "";
    }
}
```

### Custom Event Logger

Implement specialized combat logging:

```java
import com.muzlik.pvpcombat.logging.CombatLogEntry;
import com.muzlik.pvpcombat.interfaces.IEventLogger;

public class DatabaseLogger implements IEventLogger {

    private final Connection database;

    @Override
    public void logEvent(CombatLogEntry entry) {
        // Save to custom database
        try (PreparedStatement stmt = database.prepareStatement(
            "INSERT INTO combat_logs (session_id, player_id, event_type, timestamp, data) VALUES (?, ?, ?, ?, ?)")) {

            stmt.setString(1, entry.getSessionId().toString());
            stmt.setString(2, entry.getPlayerId().toString());
            stmt.setString(3, entry.getEventType());
            stmt.setTimestamp(4, Timestamp.valueOf(entry.getTimestamp()));
            stmt.setString(5, serializeEventData(entry.getData()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            getLogger().severe("Failed to log combat event: " + e.getMessage());
        }
    }

    @Override
    public List<CombatLogEntry> getRecentEvents(Player player, int limit) {
        List<CombatLogEntry> events = new ArrayList<>();

        try (PreparedStatement stmt = database.prepareStatement(
            "SELECT * FROM combat_logs WHERE player_id = ? ORDER BY timestamp DESC LIMIT ?")) {

            stmt.setString(1, player.getUniqueId().toString());
            stmt.setInt(2, limit);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(deserializeEvent(rs));
            }
        } catch (SQLException e) {
            getLogger().severe("Failed to retrieve combat events: " + e.getMessage());
        }

        return events;
    }

    @Override
    public List<CombatLogEntry> getEventsInRange(UUID sessionId, LocalDateTime start, LocalDateTime end) {
        // Implement range queries
        return queryEvents("session_id = ? AND timestamp BETWEEN ? AND ?",
            sessionId.toString(), start, end);
    }

    @Override
    public void cleanupOldEvents(int daysToKeep) {
        // Implement cleanup logic
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysToKeep);
        executeUpdate("DELETE FROM combat_logs WHERE timestamp < ?", cutoff);
    }
}
```

## Advanced Integration Examples

### Tournament Plugin Integration

```java
public class TournamentPlugin extends JavaPlugin implements Listener {

    private PvPCombatPlugin combatPlugin;
    private Map<UUID, TournamentMatch> activeMatches;

    @Override
    public void onEnable() {
        combatPlugin = PvPCombatPlugin.getInstance();
        activeMatches = new ConcurrentHashMap<>();

        if (combatPlugin != null) {
            getServer().getPluginManager().registerEvents(this, this);
            getLogger().info("Tournament integration enabled!");
        }
    }

    // Start a tournament match
    public void startTournamentMatch(Player player1, Player player2, TournamentBracket bracket) {
        // Create match record
        TournamentMatch match = new TournamentMatch(player1, player2, bracket);
        activeMatches.put(match.getMatchId(), match);

        // Disable restrictions for clean tournament play
        combatPlugin.getRestrictionManager().removeRestriction(player1, RestrictionType.ENDERPEARL);
        combatPlugin.getRestrictionManager().removeRestriction(player2, RestrictionType.ENDERPEARL);

        // Start combat
        combatPlugin.getCombatManager().forceStartCombat(player1, player2);

        // Announce match
        broadcastMatchStart(match);
    }

    // Handle combat end for tournament
    @EventHandler
    public void onTournamentCombatEnd(CombatEndEvent event) {
        // Find tournament match
        TournamentMatch match = findTournamentMatch(event.getCombatSession());
        if (match == null) return;

        Player winner = event.getWinner();
        Player loser = event.getLoser();

        // Record results
        match.setWinner(winner);
        match.setLoser(loser);
        match.setEndTime(LocalDateTime.now());
        match.setCombatStatistics(event.getCombatSession().getStatistics());

        // Award tournament points
        if (winner != null) {
            awardTournamentPoints(winner, match);
            updateBracket(match);
        }

        // Clean up
        activeMatches.remove(match.getMatchId());

        // Broadcast results
        broadcastMatchResult(match);
    }

    // Handle interference in tournaments
    @EventHandler
    public void onTournamentInterference(InterferenceDetectedEvent event) {
        // Cancel interference in tournament matches
        if (isTournamentPlayer(event.getTarget())) {
            event.setCancelled(true);
            warnPlayer(event.getInterferer(), "Tournament matches are protected!");
        }
    }
}
```

### Economy Integration with Vault

```java
public class EconomyIntegration extends JavaPlugin implements Listener {

    private Economy economy;
    private PvPCombatPlugin combatPlugin;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Vault economy not found!");
            return;
        }

        combatPlugin = PvPCombatPlugin.getInstance();
        if (combatPlugin != null) {
            getServer().getPluginManager().registerEvents(this, this);
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager()
            .getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    @EventHandler
    public void onCombatEnd(CombatEndEvent event) {
        Player winner = event.getWinner();
        Player loser = event.getLoser();

        if (winner == null || loser == null) return;

        CombatStatistics stats = event.getCombatSession().getStatistics();

        // Calculate rewards based on performance
        double baseReward = 50.0;
        double performanceMultiplier = calculatePerformanceMultiplier(stats);

        double winnerReward = baseReward * performanceMultiplier;
        double loserPenalty = baseReward * 0.5; // Half the reward as penalty

        // Apply economy changes
        economy.depositPlayer(winner, winnerReward);
        economy.withdrawPlayer(loser, loserPenalty);

        // Notify players
        winner.sendMessage(String.format("§aYou earned $%.2f for winning!", winnerReward));
        loser.sendMessage(String.format("§cYou lost $%.2f for losing!", loserPenalty));

        // Log transaction
        logEconomyTransaction(winner, winnerReward, "combat_win");
        logEconomyTransaction(loser, -loserPenalty, "combat_loss");
    }

    @EventHandler
    public void onCombatStart(CombatStartEvent event) {
        Player attacker = event.getAttacker();
        Player defender = event.getDefender();

        // Optional: Entry fee
        double entryFee = 10.0;
        if (economy.getBalance(attacker) >= entryFee) {
            economy.withdrawPlayer(attacker, entryFee);
            attacker.sendMessage("§e$" + entryFee + " entry fee deducted!");
        }

        if (economy.getBalance(defender) >= entryFee) {
            economy.withdrawPlayer(defender, entryFee);
            defender.sendMessage("§e$" + entryFee + " entry fee deducted!");
        }
    }

    private double calculatePerformanceMultiplier(CombatStatistics stats) {
        double accuracy = stats.getAccuracy();
        double damageRatio = stats.getDamageDealt() / Math.max(stats.getDamageTaken(), 1);

        // Bonus for high accuracy and damage ratio
        double multiplier = 1.0;
        if (accuracy > 0.8) multiplier += 0.2;
        if (accuracy > 0.9) multiplier += 0.3;
        if (damageRatio > 2.0) multiplier += 0.5;

        return Math.min(multiplier, 3.0); // Cap at 3x
    }
}
```

### Custom Damage Tracking System

```java
public class DamageTracker extends JavaPlugin implements Listener {

    private final Map<UUID, CustomDamageData> sessionDamageData = new ConcurrentHashMap<>();
    private PvPCombatPlugin combatPlugin;

    @Override
    public void onEnable() {
        combatPlugin = PvPCombatPlugin.getInstance();
        if (combatPlugin != null) {
            getServer().getPluginManager().registerEvents(this, this);
        }
    }

    // Track custom damage types
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        // Check if this is tracked combat
        CombatSession session = combatPlugin.getCombatManager().getCombatSession(attacker);
        if (session == null) return;

        // Record custom damage data
        UUID sessionId = session.getSessionId();
        CustomDamageData damageData = sessionDamageData.computeIfAbsent(sessionId,
            k -> new CustomDamageData());

        DamageInstance damage = new DamageInstance(
            attacker.getUniqueId(),
            victim.getUniqueId(),
            event.getDamage(),
            event.getCause(),
            attacker.getLocation(),
            attacker.getInventory().getItemInMainHand().getType(),
            System.currentTimeMillis()
        );

        damageData.addDamage(damage);

        // Analyze damage patterns
        analyzeDamagePatterns(damageData, session);
    }

    @EventHandler
    public void onCombatEnd(CombatEndEvent event) {
        UUID sessionId = event.getCombatSession().getSessionId();
        CustomDamageData damageData = sessionDamageData.remove(sessionId);

        if (damageData != null) {
            // Generate damage report
            DamageReport report = generateDamageReport(damageData);

            // Save or broadcast report
            saveDamageReport(sessionId, report);

            // Send to players if requested
            if (shouldSendReport(event.getWinner())) {
                sendDamageReport(event.getWinner(), report);
            }
        }
    }

    private void analyzeDamagePatterns(CustomDamageData damageData, CombatSession session) {
        // Implement custom analysis logic
        // Example: Detect combo attacks, defensive patterns, etc.

        List<DamageInstance> recentDamage = damageData.getRecentDamage(5000); // Last 5 seconds

        if (recentDamage.size() >= 3) {
            // Potential combo detected
            double totalDamage = recentDamage.stream().mapToDouble(DamageInstance::getDamage).sum();

            if (totalDamage > 15.0) {
                // High-damage combo - could be a special attack
                triggerComboEffect(recentDamage.get(0).getAttacker(), session);
            }
        }
    }
}
```

## API Versioning and Compatibility

### Semantic Versioning

The API follows semantic versioning (MAJOR.MINOR.PATCH):

- **MAJOR**: Breaking changes (interface changes, removed methods)
- **MINOR**: New features (added methods, new interfaces)
- **PATCH**: Bug fixes (no API changes)

### Version Checking

Always check API version compatibility:

```java
public class ApiVersionChecker {

    public static boolean isCompatible(String requiredVersion) {
        PvPCombatPlugin plugin = PvPCombatPlugin.getInstance();
        if (plugin == null) return false;

        String apiVersion = plugin.getApiVersion();
        return isVersionCompatible(apiVersion, requiredVersion);
    }

    private static boolean isVersionCompatible(String current, String required) {
        // Implement version comparison logic
        // Example: "1.2.3" is compatible with "1.2.x" but not "2.0.x"
        String[] currentParts = current.split("\\.");
        String[] requiredParts = required.split("\\.");

        // Major version must match
        if (!currentParts[0].equals(requiredParts[0])) {
            return false;
        }

        // Minor version must be >= required
        int currentMinor = Integer.parseInt(currentParts[1]);
        int requiredMinor = Integer.parseInt(requiredParts[1]);

        return currentMinor >= requiredMinor;
    }
}
```

### Migration Strategies

#### Migrating from v0.x to v1.0

```java
public class MigrationHelper {

    public static void migrateFromLegacy(PvPCombatPlugin plugin) {
        // Update interface usage
        ICombatManager combatManager = plugin.getCombatManager();
        IVisualManager visualManager = plugin.getVisualManager();

        // Replace deprecated methods
        // Old: plugin.getCombatTracker()
        // New: combatManager.getCombatSession(player)

        // Update event handlers
        // Old: CombatStartEvent (old signature)
        // New: CombatStartEvent (new signature with session ID)

        // Update configuration access
        // Old: plugin.getConfig().getString("combat.duration")
        // New: plugin.getConfigManager().getConfigValue("combat.duration")
    }
}
```

## Best Practices

### Error Handling

Always wrap API calls in proper error handling:

```java
public class SafeApiUsage {

    public boolean safeIsInCombat(Player player) {
        try {
            PvPCombatPlugin plugin = PvPCombatPlugin.getInstance();
            if (plugin == null) return false;

            ICombatManager combatManager = plugin.getCombatManager();
            if (combatManager == null) return false;

            return combatManager.isInCombat(player);
        } catch (Exception e) {
            getLogger().warning("Failed to check combat status: " + e.getMessage());
            return false;
        }
    }

    public Player safeGetOpponent(Player player) {
        try {
            PvPCombatPlugin plugin = PvPCombatPlugin.getInstance();
            if (plugin == null) return null;

            ICombatManager combatManager = plugin.getCombatManager();
            if (combatManager == null) return null;

            return combatManager.getOpponent(player);
        } catch (Exception e) {
            getLogger().warning("Failed to get opponent: " + e.getMessage());
            return null;
        }
    }
}
```

### Performance Considerations

Follow these guidelines for optimal performance:

```java
public class PerformanceOptimizedIntegration {

    // Cache frequently accessed data
    private final Map<UUID, Player> opponentCache = new ConcurrentHashMap<>();
    private final Cache<UUID, Boolean> combatStatusCache = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.SECONDS)
        .build();

    public boolean isInCombatOptimized(Player player) {
        UUID playerId = player.getUniqueId();

        // Check cache first
        Boolean cached = combatStatusCache.getIfPresent(playerId);
        if (cached != null) {
            return cached;
        }

        // Compute and cache
        boolean inCombat = combatManager.isInCombat(player);
        combatStatusCache.put(playerId, inCombat);
        return inCombat;
    }

    // Use asynchronous operations for heavy processing
    public void processCombatDataAsync(CombatSession session) {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            // Heavy processing here
            analyzeCombatStatistics(session);
            updateLeaderboards(session);
            saveToDatabase(session);
        });
    }

    // Avoid blocking main thread
    @EventHandler
    public void onCombatEndAsync(CombatEndEvent event) {
        // Schedule for next tick to avoid blocking
        getServer().getScheduler().runTask(this, () -> {
            processCombatEnd(event);
        });
    }
}
```

### Thread Safety

Ensure thread-safe operations:

```java
public class ThreadSafeIntegration {

    private final ConcurrentMap<UUID, CombatData> combatData = new ConcurrentHashMap<>();

    // Thread-safe data access
    public void updateCombatData(UUID sessionId, CombatData data) {
        combatData.put(sessionId, data);
    }

    public CombatData getCombatData(UUID sessionId) {
        return combatData.get(sessionId);
    }

    public void removeCombatData(UUID sessionId) {
        combatData.remove(sessionId);
    }

    // Safe iteration
    public void processAllCombatData() {
        combatData.forEach((sessionId, data) -> {
            // Process each item - snapshot prevents ConcurrentModificationException
            processCombatData(sessionId, data);
        });
    }

    // Atomic operations
    public boolean addDamage(UUID sessionId, double damage) {
        return combatData.computeIfPresent(sessionId, (id, data) -> {
            data.addDamage(damage);
            return data;
        }) != null;
    }
}
```

### Memory Management

Proper cleanup prevents memory leaks:

```java
public class MemorySafeIntegration implements Listener {

    private final Map<UUID, CustomData> sessionData = new HashMap<>();
    private final Set<UUID> activeSessions = ConcurrentHashMap.newKeySet();

    @Override
    public void onDisable() {
        // Clean up all data on plugin disable
        sessionData.clear();
        activeSessions.clear();
    }

    @EventHandler
    public void onCombatStart(CombatStartEvent event) {
        UUID sessionId = event.getSessionId();
        activeSessions.add(sessionId);
        sessionData.put(sessionId, new CustomData());
    }

    @EventHandler
    public void onCombatEnd(CombatEndEvent event) {
        UUID sessionId = event.getCombatSession().getSessionId();
        activeSessions.remove(sessionId);
        CustomData data = sessionData.remove(sessionId);

        // Process final data if needed
        if (data != null) {
            processFinalData(data);
        }
    }

    // Periodic cleanup for safety
    public void cleanupInactiveData() {
        // Remove data for sessions that ended without proper event
        sessionData.keySet().removeIf(sessionId -> !activeSessions.contains(sessionId));
    }
}
```

## Troubleshooting Integration Issues

### Common Issues

1. **Plugin Not Found**: Ensure PvP Combat loads before your plugin
2. **Null Pointer Exceptions**: Always null-check API objects
3. **Version Incompatibility**: Check API version before using new features
4. **Performance Issues**: Cache data and use async operations
5. **Memory Leaks**: Implement proper cleanup in event handlers

### Debug Mode

Enable debug logging for integration troubleshooting:

```java
// Enable debug mode
/combat debug

// Check console for detailed API call logs
[15:30:42] [Server thread/INFO]: [PvPCombat] API Call: isInCombat(player=Steve) -> true
[15:30:42] [Server thread/INFO]: [PvPCombat] API Call: getOpponent(player=Steve) -> Alex
```

### Logging Best Practices

```java
public class IntegrationLogger {

    private static final Logger logger = Logger.getLogger("PvPCombatIntegration");

    public static void logApiCall(String method, Object... params) {
        if (isDebugEnabled()) {
            logger.info("API Call: " + method + "(" + Arrays.toString(params) + ")");
        }
    }

    public static void logApiResult(String method, Object result) {
        if (isDebugEnabled()) {
            logger.info("API Result: " + method + " -> " + result);
        }
    }

    public static void logError(String context, Exception e) {
        logger.severe("Integration error in " + context + ": " + e.getMessage());
        if (isDebugEnabled()) {
            e.printStackTrace();
        }
    }
}
```

This comprehensive API integration guide covers everything needed to successfully integrate with the PvP Combat System. Remember to always check for plugin availability, handle errors gracefully, and follow performance best practices for optimal server performance.