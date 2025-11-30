package com.muzlik.pvpcombat.restrictions;

import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import com.muzlik.pvpcombat.interfaces.ICombatManager;
import com.muzlik.pvpcombat.visual.SafeZoneBarrierRenderer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages safe zone restrictions during combat.
 * Prevents players from entering designated safe zones while in combat.
 * Includes visual barrier rendering system.
 */
public class SafeZoneManager implements Listener {

    private final PvPCombatPlugin plugin;
    private final ICombatManager combatManager;
    private final SafeZoneBarrierRenderer barrierRenderer;
    private final Map<UUID, Long> lastBarrierRender;
    private boolean enabled;
    private boolean blockEntry;
    private boolean showVisualBarrier;
    private List<String> protectedRegions;
    private String blockedMessage;
    private long barrierRenderCooldown;

    public SafeZoneManager(PvPCombatPlugin plugin, ICombatManager combatManager) {
        this.plugin = plugin;
        this.combatManager = combatManager;
        this.barrierRenderer = new SafeZoneBarrierRenderer(plugin);
        this.lastBarrierRender = new ConcurrentHashMap<>();
        loadConfiguration();
    }

    /**
     * Loads configuration from config.yml
     */
    public void loadConfiguration() {
        this.enabled = plugin.getConfig().getBoolean("restrictions.safezone.enabled", true);
        this.blockEntry = plugin.getConfig().getBoolean("restrictions.safezone.block-entry", true);
        this.showVisualBarrier = plugin.getConfig().getBoolean("restrictions.safezone.barrier.enabled", true);
        this.protectedRegions = plugin.getConfig().getStringList("restrictions.safezone.protected-regions");
        this.blockedMessage = plugin.getConfig().getString("restrictions.safezone.blocked-message",
            "&cYou cannot enter a safe zone while in combat!");
        this.barrierRenderCooldown = plugin.getConfig().getLong("restrictions.safezone.barrier.render-cooldown-ms", 50L);
        
        // Reload barrier renderer configuration
        barrierRenderer.reloadConfiguration();
    }

    /**
     * Checks if a location is in a safe zone.
     */
    public boolean isInSafeZone(Location location) {
        if (!enabled || !blockEntry) {
            return false;
        }

        // Check if WorldGuard is available
        if (plugin.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            return checkWorldGuardRegion(location);
        }

        return false;
    }

    /**
     * Checks if location is in a WorldGuard protected region.
     * Uses reflection to avoid hard dependency on WorldGuard.
     */
    private boolean checkWorldGuardRegion(Location location) {
        try {
            // Use reflection to check WorldGuard regions
            Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
            Object worldGuard = worldGuardClass.getMethod("getInstance").invoke(null);
            Object platform = worldGuardClass.getMethod("getPlatform").invoke(worldGuard);
            Object regionContainer = platform.getClass().getMethod("getRegionContainer").invoke(platform);
            
            // Get BukkitAdapter
            Class<?> adapterClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
            Object adaptedWorld = adapterClass.getMethod("adapt", org.bukkit.World.class).invoke(null, location.getWorld());
            
            // Get RegionManager
            Object regionManager = regionContainer.getClass().getMethod("get", 
                Class.forName("com.sk89q.worldedit.world.World")).invoke(regionContainer, adaptedWorld);
            
            if (regionManager != null) {
                // Check each protected region
                for (String regionName : protectedRegions) {
                    Object region = regionManager.getClass().getMethod("getRegion", String.class)
                        .invoke(regionManager, regionName);
                    
                    if (region != null) {
                        // Check if location is in region
                        Boolean contains = (Boolean) region.getClass().getMethod("contains", int.class, int.class, int.class)
                            .invoke(region, location.getBlockX(), location.getBlockY(), location.getBlockZ());
                        
                        if (contains != null && contains) {
                            return true;
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            // WorldGuard not installed - silently ignore
            plugin.getLogger().fine("WorldGuard not found, safe zone protection disabled");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to check WorldGuard region: " + e.getMessage());
        }

        return false;
    }

    /**
     * Handles player movement to prevent safe zone entry during combat.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!enabled || !blockEntry) {
            return;
        }

        Player player = event.getPlayer();

        // Check if player is in combat
        if (!combatManager.isInCombat(player)) {
            // Clear any existing barriers if player is no longer in combat
            if (barrierRenderer.hasActiveBarrier(player)) {
                barrierRenderer.clearBarrier(player);
            }
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();

        // Check if player is moving to a different block
        if (to == null || (from.getBlockX() == to.getBlockX() && 
            from.getBlockY() == to.getBlockY() && 
            from.getBlockZ() == to.getBlockZ())) {
            return;
        }

        // Check if destination is in safe zone
        boolean destinationInSafezone = isInSafeZone(to);
        
        // Check for safezone in 4-block radius around player
        boolean safezoneNearby = checkSafezoneInRadius(from, 4);
        
        if (destinationInSafezone) {
            // Cancel movement - teleport player back
            event.setCancelled(true);
            
            // Render visual barrier if enabled and cooldown has passed
            if (showVisualBarrier && shouldRenderBarrier(player)) {
                barrierRenderer.renderBarrier(player, from, to);
                lastBarrierRender.put(player.getUniqueId(), System.currentTimeMillis());
            }
            
            // Send message (only once per second to avoid spam)
            if (shouldSendMessage(player)) {
                String message = blockedMessage.replace("&", "§");
                player.sendMessage(message);
            }
            
            // Play sound
            player.playSound(player.getLocation(), 
                org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        } else if (safezoneNearby) {
            // Show barrier when safezone is within 4 blocks
            if (showVisualBarrier && shouldRenderBarrier(player)) {
                barrierRenderer.renderBarrier(player, from, to);
                lastBarrierRender.put(player.getUniqueId(), System.currentTimeMillis());
            }
        } else {
            // Player moved away from safezone - clear barriers
            if (barrierRenderer.hasActiveBarrier(player)) {
                barrierRenderer.clearBarrier(player);
            }
        }
    }
    
    /**
     * Checks if there is a safezone within the specified radius of a location.
     */
    private boolean checkSafezoneInRadius(Location center, int radius) {
        // Check blocks in a cube around the center
        for (int x = -radius; x <= radius; x++) {
            for (int y = -2; y <= 2; y++) {  // Check 2 blocks up and down
                for (int z = -radius; z <= radius; z++) {
                    Location checkLoc = center.clone().add(x, y, z);
                    if (isInSafeZone(checkLoc)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if enough time has passed to render another barrier.
     */
    private boolean shouldRenderBarrier(Player player) {
        Long lastRender = lastBarrierRender.get(player.getUniqueId());
        if (lastRender == null) {
            return true;
        }
        return System.currentTimeMillis() - lastRender >= barrierRenderCooldown;
    }

    /**
     * Checks if enough time has passed to send another message (1 second cooldown).
     */
    private boolean shouldSendMessage(Player player) {
        Long lastRender = lastBarrierRender.get(player.getUniqueId());
        if (lastRender == null) {
            return true;
        }
        return System.currentTimeMillis() - lastRender >= 1000L;
    }

    /**
     * Handles player quit to clean up barriers.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        barrierRenderer.clearBarrier(player);
        lastBarrierRender.remove(player.getUniqueId());
    }

    /**
     * Checks if safe zone restrictions are enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether safe zone restrictions are enabled.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the barrier renderer.
     */
    public SafeZoneBarrierRenderer getBarrierRenderer() {
        return barrierRenderer;
    }

    /**
     * Cleans up all barriers (called on plugin disable).
     */
    public void cleanup() {
        barrierRenderer.clearAllBarriers();
        lastBarrierRender.clear();
    }
}
