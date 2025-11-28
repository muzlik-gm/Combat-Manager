package com.muzlik.pvpcombat.events;

import com.muzlik.pvpcombat.combat.AntiInterferenceManager;
import com.muzlik.pvpcombat.combat.CombatManager;
import com.muzlik.pvpcombat.combat.CombatTracker;
import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import com.muzlik.pvpcombat.data.CombatSession;
import com.muzlik.pvpcombat.data.RestrictionData;
import com.muzlik.pvpcombat.performance.PerformanceMonitor;
import com.muzlik.pvpcombat.logging.CombatLogger;
import com.muzlik.pvpcombat.restrictions.RestrictionManager;
import com.muzlik.pvpcombat.utils.AsyncUtils;
import com.muzlik.pvpcombat.utils.CacheManager;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.ChatColor;

/**
 * Main event listener handling all combat-related events.
 */
public class CombatEventListener implements Listener {

    private final PvPCombatPlugin plugin;
    private final CombatManager combatManager;
    private final CombatTracker combatTracker;
    private final AntiInterferenceManager antiInterferenceManager;
    private final RestrictionManager restrictionManager;
    private final CombatLogger combatLogger;
    private final PerformanceMonitor performanceMonitor;
    private final CacheManager cacheManager;

    public CombatEventListener(PvPCombatPlugin plugin, CombatManager combatManager,
                                CombatTracker combatTracker, AntiInterferenceManager antiInterferenceManager,
                                RestrictionManager restrictionManager, CombatLogger combatLogger,
                                PerformanceMonitor performanceMonitor, CacheManager cacheManager) {
        this.plugin = plugin;
        this.combatManager = combatManager;
        this.combatTracker = combatTracker;
        this.antiInterferenceManager = antiInterferenceManager;
        this.restrictionManager = restrictionManager;
        this.combatLogger = combatLogger;
        this.performanceMonitor = performanceMonitor;
        this.cacheManager = cacheManager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        performanceMonitor.startOperation("entity-damage-event");

        try {
            // Only handle player vs player damage
            if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
                return;
            }

            Player attacker = (Player) event.getDamager();
            Player defender = (Player) event.getEntity();

            // Check cache for interference data first
            String interferenceKey = attacker.getUniqueId() + ":" + defender.getUniqueId();
            Boolean cachedInterference = (Boolean) cacheManager.get("restriction-data", interferenceKey);

            boolean hasInterference = cachedInterference != null ? cachedInterference :
                antiInterferenceManager.checkInterference(attacker, defender);

            // Cache the result
            if (cachedInterference == null) {
                cacheManager.put("restriction-data", interferenceKey, hasInterference);
            }

            // Check for interference first
            if (hasInterference) {
                AsyncUtils.runAsync(plugin, () ->
                    antiInterferenceManager.handleInterference(attacker, defender), "combat-processing");

                // Cancel the event if blocking is enabled
                if (antiInterferenceManager.shouldBlockInterference()) {
                    event.setCancelled(true);
                }
                return;
            }

            // Record damage synchronously to ensure it's tracked
            // Use the CombatManager's tracker, not the local one!
            double damage = event.getFinalDamage();
            combatManager.getCombatTracker().recordDamageDealt(attacker, damage);
            combatManager.getCombatTracker().recordDamageReceived(defender, damage);
            
            // Also record damage in the session for per-combat tracking
            CombatSession damageSession = combatManager.getActiveSessions().values().stream()
                .filter(s -> s.involvesPlayer(attacker))
                .findFirst().orElse(null);
            if (damageSession != null) {
                damageSession.recordDamage(attacker, damage);
            }
            
            // Debug logging - use info level so it shows in console
            plugin.getLogger().info(String.format("[DAMAGE] %s dealt %.1f to %s (Total: %.1f)", 
                attacker.getName(), damage, defender.getName(),
                combatManager.getCombatTracker().getPlayerData(attacker.getUniqueId()).getTotalDamageDealt()));

            // Log damage event asynchronously
            if (combatManager.isInCombat(attacker)) {
                Player opponent = combatManager.getOpponent(attacker);
                if (opponent != null && opponent.equals(defender)) {
                    AsyncUtils.runAsync(plugin, () -> {
                        CombatSession logSession = combatManager.getActiveSessions().values().stream()
                            .filter(s -> s.getAttacker().equals(attacker) || s.getDefender().equals(attacker))
                            .findFirst().orElse(null);
                        if (logSession != null) {
                            String weaponType = attacker.getInventory().getItemInMainHand().getType().toString();
                            double distance = attacker.getLocation().distance(defender.getLocation());
                            combatLogger.logDamageDealt(logSession.getSessionId(), attacker, defender, event.getFinalDamage(),
                                                       true, distance, weaponType);
                        }
                    }, "combat-processing");
                }
            }

            // Start or reset combat asynchronously
            if (!combatManager.isInCombat(attacker) && !combatManager.isInCombat(defender)) {
                // Start new combat - run on main thread for thread safety
                AsyncUtils.runSync(plugin, () -> combatManager.startCombat(attacker, defender));
            } else {
                // Reset timer for existing combat - run on main thread
                AsyncUtils.runSync(plugin, () -> {
                    CombatSession timerSession = combatManager.getActiveSessions().values().stream()
                        .filter(s -> s.getAttacker().equals(attacker) || s.getDefender().equals(attacker))
                        .findFirst().orElse(null);
                    if (timerSession != null) {
                        // Reset timer to default duration
                        int defaultDuration = plugin.getConfig().getInt("combat.duration", 30);
                        timerSession.getTimerData().setRemainingSeconds(defaultDuration);
                    }
                });
            }
        } finally {
            performanceMonitor.endOperation("entity-damage-event");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        performanceMonitor.startOperation("player-death-event");

        try {
            Player deceased = event.getEntity();

            if (combatManager.isInCombat(deceased)) {
                // Record stats synchronously to ensure they're saved
                Player opponent = combatManager.getOpponent(deceased);
                if (opponent != null) {
                    // Use the CombatManager's tracker, not the local one!
                    combatManager.getCombatTracker().recordWin(opponent);
                    combatManager.getCombatTracker().recordLoss(deceased);
                    
                    plugin.getLogger().info("Combat ended - Winner: " + opponent.getName() + ", Loser: " + deceased.getName());
                }

                // End combat - keep on main thread for thread safety
                AsyncUtils.runSync(plugin, () -> combatManager.endCombat(deceased.getUniqueId()));
            }
        } finally {
            performanceMonitor.endOperation("player-death-event");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        performanceMonitor.startOperation("player-quit-event");

        try {
            Player player = event.getPlayer();

            if (combatManager.isInCombat(player)) {
                // Record a loss for leaving combat
                combatManager.getCombatTracker().recordLoss(player);
                
                // Get opponent and record a win for them
                Player opponent = combatManager.getOpponent(player);
                String forfeitMessage = ChatColor.RED + player.getName() + ChatColor.YELLOW + " forfeited by logging out during combat and died!";
                
                if (opponent != null) {
                    combatManager.getCombatTracker().recordWin(opponent);
                    plugin.getLogger().info(player.getName() + " forfeited combat. " + opponent.getName() + " wins!");
                    
                    // Notify opponent with better message
                    opponent.sendMessage(ChatColor.GREEN + "You won! " + ChatColor.YELLOW + player.getName() + " forfeited by logging out.");
                }
                
                // Broadcast forfeit to all players
                plugin.getServer().broadcastMessage(forfeitMessage);
                
                // Kill the player immediately (this will trigger death and inventory drop naturally)
                player.setHealth(0.0);
                plugin.getLogger().info(player.getName() + " was killed for combat logging.");
                
                // End combat due to logout - keep on main thread for thread safety
                AsyncUtils.runSync(plugin, () -> combatManager.endCombat(player.getUniqueId()));
            }
        } finally {
            performanceMonitor.endOperation("player-quit-event");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();

        // Check if projectile is an ender pearl
        if (!(projectile instanceof org.bukkit.entity.EnderPearl)) {
            return;
        }

        // Get the shooter (player)
        if (!(projectile.getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) projectile.getShooter();

        // Check if player can use ender pearl
        if (!restrictionManager.canUseEnderPearl(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot use Ender Pearls during combat!");
            return;
        }

        // Apply ender pearl restrictions
        RestrictionData restrictionData = restrictionManager.getOrCreateRestrictionData(player);
        
        // Schedule cooldown application for next tick to override Minecraft's default cooldown
        org.bukkit.scheduler.BukkitRunnable task = new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                restrictionManager.getEnderPearlRestriction().onEnderPearlUsed(player, restrictionData);
            }
        };
        task.runTaskLater(plugin, 1L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerToggleGlide(org.bukkit.event.entity.EntityToggleGlideEvent event) {
        // Check if it's a player
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        
        // Only check when player is starting to glide (not stopping)
        if (!event.isGliding()) {
            return;
        }
        
        // Check if player is in combat and elytra is restricted
        if (combatManager.isInCombat(player)) {
            boolean elytraEnabled = plugin.getConfig().getBoolean("restrictions.elytra.enabled", true);
            boolean blockGlide = plugin.getConfig().getBoolean("restrictions.elytra.block-glide", true);
            
            if (elytraEnabled && blockGlide) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot use Elytra during combat!");
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Additional check - if somehow they're gliding during combat, stop them
        if (player.isGliding() && combatManager.isInCombat(player)) {
            boolean elytraEnabled = plugin.getConfig().getBoolean("restrictions.elytra.enabled", true);
            boolean blockGlide = plugin.getConfig().getBoolean("restrictions.elytra.block-glide", true);
            
            if (elytraEnabled && blockGlide) {
                player.setGliding(false);
                player.sendMessage(ChatColor.RED + "Elytra gliding is restricted during combat!");
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Check for elytra boost (firework damage to self)
        if (event.getEntity() instanceof Player && event.getDamager() instanceof org.bukkit.entity.Firework) {
            Player player = (Player) event.getEntity();
            org.bukkit.entity.Firework firework = (org.bukkit.entity.Firework) event.getDamager();

            // Check if firework was launched by the player (elytra boost)
            if (firework.getShooter() instanceof Player && firework.getShooter().equals(player)) {
                RestrictionData restrictionData = restrictionManager.getOrCreateRestrictionData(player);

                if (!restrictionManager.canUseElytra(player)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Elytra boosts are restricted during combat!");
                    return;
                }

                // Track boost usage
                restrictionManager.getElytraRestriction().onElytraBoost(player, restrictionData);
            }
        }

        // Existing damage handling continues...
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Resume combat state from cache if exists
        Object combatState = cacheManager.get("combat-state", player.getUniqueId().toString());
        if (combatState != null) {
            player.sendMessage(ChatColor.GREEN + "Combat session resumed from previous session!");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerConsume(org.bukkit.event.player.PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Material item = event.getItem().getType();

        // Get restriction data once
        RestrictionData restrictionData = restrictionManager.getOrCreateRestrictionData(player);

        // Check for golden apple consumption
        if (item == Material.GOLDEN_APPLE) {
            // Check if player is on cooldown
            if (restrictionData.isOnCooldown("golden_apple")) {
                event.setCancelled(true);
                
                // Get remaining cooldown
                int remainingSeconds = 0;
                java.time.LocalDateTime expiry = restrictionData.getActiveCooldowns().get("golden_apple");
                if (expiry != null) {
                    remainingSeconds = (int) java.time.Duration.between(java.time.LocalDateTime.now(), expiry).getSeconds();
                }
                
                player.sendMessage(ChatColor.RED + "You cannot use Golden Apples yet! Cooldown: " + remainingSeconds + "s");
                return;
            }

            // Check if usage is blocked during combat
            if (!restrictionManager.canUseGoldenApple(player)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot use Golden Apples during combat!");
                return;
            }

            // Apply golden apple cooldown immediately (not on next tick)
            restrictionManager.getGoldenAppleRestriction().onGoldenAppleUsed(player, restrictionData);
        }
        // Check for enchanted golden apple consumption
        else if (item == Material.ENCHANTED_GOLDEN_APPLE) {
            // Check if player is on cooldown
            if (restrictionData.isOnCooldown("enchanted_golden_apple")) {
                event.setCancelled(true);
                
                // Get remaining cooldown
                int remainingSeconds = 0;
                java.time.LocalDateTime expiry = restrictionData.getActiveCooldowns().get("enchanted_golden_apple");
                if (expiry != null) {
                    remainingSeconds = (int) java.time.Duration.between(java.time.LocalDateTime.now(), expiry).getSeconds();
                }
                
                player.sendMessage(ChatColor.RED + "You cannot use Enchanted Golden Apples yet! Cooldown: " + remainingSeconds + "s");
                return;
            }

            // Check if usage is blocked during combat
            if (!restrictionManager.canUseEnchantedGoldenApple(player)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot use Enchanted Golden Apples during combat!");
                return;
            }

            // Apply enchanted golden apple cooldown immediately (not on next tick)
            restrictionManager.getGoldenAppleRestriction().onEnchantedGoldenAppleUsed(player, restrictionData);
        }
    }

    /**
     * Checks if a player has elytra equipped.
     */
    private boolean hasElytraEquipped(Player player) {
        return player.getInventory().getChestplate() != null &&
               player.getInventory().getChestplate().getType() == Material.ELYTRA;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent event) {
        // Only process if restrictions are explicitly enabled
        if (!plugin.getConfig().getBoolean("restrictions.blocks.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();

        // Check if combat manager is initialized
        if (combatManager == null || restrictionManager == null) {
            return;
        }

        // Only check restrictions if player is in combat
        if (!combatManager.isInCombat(player)) {
            return;
        }

        // Check if block breaking is allowed
        if (!restrictionManager.canBreakBlocks(player)) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot break blocks while in combat!");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent event) {
        // Only process if restrictions are explicitly enabled
        if (!plugin.getConfig().getBoolean("restrictions.blocks.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();

        // Check if combat manager is initialized
        if (combatManager == null || restrictionManager == null) {
            return;
        }

        // Only check restrictions if player is in combat
        if (!combatManager.isInCombat(player)) {
            return;
        }

        // Check if block placing is allowed
        if (!restrictionManager.canPlaceBlocks(player)) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot place blocks while in combat!");
        }
    }
}