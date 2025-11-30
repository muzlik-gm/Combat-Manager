package com.muzlik.pvpcombat.visual;

import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Renders client-side fake block barriers using ProtocolLib.
 * Barriers persist until player moves far away or combat ends.
 */
public class SafeZoneBarrierRenderer {

    private final PvPCombatPlugin plugin;
    private final Map<UUID, Set<Location>> activeBarriers;
    private final Map<UUID, BukkitTask> updateTasks;
    private Object protocolManager;
    private Material barrierMaterial;
    private int barrierHeight;
    private int detectionRadius;
    private boolean protocolLibAvailable;
    
    // Reflection cache
    private Class<?> packetTypeClass;
    private Class<?> blockPositionClass;
    private Class<?> wrappedBlockDataClass;
    private Method createPacketMethod;
    private Method sendPacketMethod;
    private Method createDataMethod;

    public SafeZoneBarrierRenderer(PvPCombatPlugin plugin) {
        this.plugin = plugin;
        this.activeBarriers = new ConcurrentHashMap<>();
        this.updateTasks = new ConcurrentHashMap<>();
        
        // Check if ProtocolLib is available and initialize reflection
        this.protocolLibAvailable = initializeProtocolLib();
        
        if (protocolLibAvailable) {
            plugin.getLogger().info("ProtocolLib detected - using packet-based barrier rendering");
        } else {
            plugin.getLogger().warning("ProtocolLib not found - barrier system will not work! Please install ProtocolLib.");
        }
        
        loadConfiguration();
    }

    /**
     * Initializes ProtocolLib using reflection to avoid compile-time dependency.
     */
    private boolean initializeProtocolLib() {
        try {
            // Check if ProtocolLib plugin is loaded
            if (plugin.getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
                return false;
            }

            // Get ProtocolLibrary class
            Class<?> protocolLibraryClass = Class.forName("com.comphenix.protocol.ProtocolLibrary");
            Method getProtocolManagerMethod = protocolLibraryClass.getMethod("getProtocolManager");
            this.protocolManager = getProtocolManagerMethod.invoke(null);

            // Cache reflection methods
            Class<?> protocolManagerClass = Class.forName("com.comphenix.protocol.ProtocolManager");
            this.packetTypeClass = Class.forName("com.comphenix.protocol.PacketType");
            this.blockPositionClass = Class.forName("com.comphenix.protocol.wrappers.BlockPosition");
            this.wrappedBlockDataClass = Class.forName("com.comphenix.protocol.wrappers.WrappedBlockData");
            
            this.createPacketMethod = protocolManagerClass.getMethod("createPacket", Object.class);
            this.sendPacketMethod = protocolManagerClass.getMethod("sendServerPacket", Player.class, 
                Class.forName("com.comphenix.protocol.events.PacketContainer"));
            this.createDataMethod = wrappedBlockDataClass.getMethod("createData", Material.class);

            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to initialize ProtocolLib: " + e.getMessage());
            return false;
        }
    }

    private void loadConfiguration() {
        String materialName = plugin.getConfig().getString("restrictions.safezone.barrier.material", "GLASS");
        try {
            this.barrierMaterial = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid barrier material: " + materialName + ", using GLASS");
            this.barrierMaterial = Material.GLASS;
        }
        this.barrierHeight = plugin.getConfig().getInt("restrictions.safezone.barrier.height", 4);
        this.detectionRadius = plugin.getConfig().getInt("restrictions.safezone.barrier.width", 5);
        
        plugin.getLogger().info(String.format("SafeZone barrier config: material=%s, height=%d, radius=%d",
            this.barrierMaterial, this.barrierHeight, this.detectionRadius));
    }

    /**
     * Renders persistent barriers around safezone boundaries.
     * Barriers stay until player moves far away or combat ends.
     */
    public void renderBarrier(Player player, Location playerLocation, Location blockedLocation) {
        if (!protocolLibAvailable) {
            // Fallback to Bukkit API
            renderBarrierBukkit(player, playerLocation, blockedLocation);
            return;
        }

        // Calculate barrier positions in radius
        Set<Location> barrierBlocks = calculateBarrierInRadius(playerLocation, detectionRadius);

        if (barrierBlocks.isEmpty()) {
            return;
        }

        // Clear old barriers first
        clearBarrier(player);

        // Send fake blocks using ProtocolLib
        sendFakeBlocksProtocolLib(player, barrierBlocks, barrierMaterial);

        // Store active barriers
        activeBarriers.put(player.getUniqueId(), barrierBlocks);

        // Start continuous update task to keep barriers visible
        startBarrierUpdateTask(player);
    }

    /**
     * Fallback method using Bukkit API if ProtocolLib is not available.
     */
    private void renderBarrierBukkit(Player player, Location playerLocation, Location blockedLocation) {
        Set<Location> barrierBlocks = calculateBarrierInRadius(playerLocation, detectionRadius);

        if (barrierBlocks.isEmpty()) {
            return;
        }

        // Clear old barriers first
        clearBarrier(player);

        // Send fake blocks using Bukkit API
        for (Location loc : barrierBlocks) {
            player.sendBlockChange(loc, barrierMaterial.createBlockData());
        }

        // Store active barriers
        activeBarriers.put(player.getUniqueId(), barrierBlocks);

        // Start continuous update task
        startBarrierUpdateTask(player);
    }

    /**
     * Starts a task that continuously updates barriers and checks distance.
     * Barriers persist until player moves far away (>10 blocks) or combat ends.
     */
    private void startBarrierUpdateTask(Player player) {
        // Cancel existing task
        BukkitTask existingTask = updateTasks.remove(player.getUniqueId());
        if (existingTask != null && !existingTask.isCancelled()) {
            existingTask.cancel();
        }

        // Start new update task (runs every 10 ticks = 0.5 seconds)
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                // Check if player is still online
                if (!player.isOnline()) {
                    clearBarrier(player);
                    cancel();
                    return;
                }

                // Check if player is still in combat
                if (!plugin.getCombatManager().isInCombat(player)) {
                    clearBarrier(player);
                    cancel();
                    return;
                }

                Set<Location> barriers = activeBarriers.get(player.getUniqueId());
                if (barriers == null || barriers.isEmpty()) {
                    cancel();
                    return;
                }

                // Check if player moved far from barriers (>10 blocks)
                Location playerLoc = player.getLocation();
                boolean tooFar = barriers.stream()
                    .allMatch(barrierLoc -> barrierLoc.distance(playerLoc) > 10);

                if (tooFar) {
                    clearBarrier(player);
                    cancel();
                    return;
                }

                // Re-send barrier packets to keep them visible (prevents despawn)
                if (protocolLibAvailable) {
                    sendFakeBlocksProtocolLib(player, barriers, barrierMaterial);
                } else {
                    // Bukkit fallback
                    for (Location loc : barriers) {
                        player.sendBlockChange(loc, barrierMaterial.createBlockData());
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 10L); // Run every 0.5 seconds

        updateTasks.put(player.getUniqueId(), task);
    }

    /**
     * Calculates barrier positions in a radius, showing only safezone boundaries.
     */
    private Set<Location> calculateBarrierInRadius(Location center, int radius) {
        Set<Location> positions = new HashSet<>();
        
        // Check blocks in a radius around the player
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // Only check blocks within circular radius
                if (Math.sqrt(x * x + z * z) > radius) {
                    continue;
                }
                
                Location checkLoc = new Location(center.getWorld(), 
                    center.getBlockX() + x, 
                    center.getBlockY(), 
                    center.getBlockZ() + z);
                
                // Check if this location is at safezone boundary
                if (isLocationInSafeZone(checkLoc)) {
                    // Check if any adjacent block is NOT in safezone (boundary detection)
                    boolean isBoundary = false;
                    
                    Location[] adjacent = {
                        checkLoc.clone().add(1, 0, 0),
                        checkLoc.clone().add(-1, 0, 0),
                        checkLoc.clone().add(0, 0, 1),
                        checkLoc.clone().add(0, 0, -1)
                    };
                    
                    for (Location adj : adjacent) {
                        if (!isLocationInSafeZone(adj)) {
                            isBoundary = true;
                            break;
                        }
                    }
                    
                    // If at boundary, add vertical wall (only on AIR blocks)
                    if (isBoundary) {
                        for (int y = 0; y < barrierHeight; y++) {
                            Location barrierLoc = new Location(center.getWorld(), 
                                checkLoc.getBlockX(), 
                                center.getBlockY() + y, 
                                checkLoc.getBlockZ());
                            
                            // Only add if block is AIR (prevent griefing)
                            if (barrierLoc.getBlock().getType() == Material.AIR) {
                                positions.add(barrierLoc);
                            }
                        }
                    }
                }
            }
        }
        
        return positions;
    }

    /**
     * Checks if a specific location is in a safezone.
     */
    private boolean isLocationInSafeZone(Location location) {
        try {
            List<String> protectedRegions = plugin.getConfig().getStringList("restrictions.safezone.protected-regions");
            if (protectedRegions.isEmpty()) {
                return false;
            }
            
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
        } catch (Exception e) {
            // Silently fail
        }
        
        return false;
    }

    /**
     * Sends fake blocks to the player using ProtocolLib packets via reflection.
     */
    private void sendFakeBlocksProtocolLib(Player player, Set<Location> locations, Material material) {
        if (!protocolLibAvailable || protocolManager == null) {
            return;
        }

        try {
            // Get PacketType.Play.Server.BLOCK_CHANGE
            Class<?> playClass = Class.forName("com.comphenix.protocol.PacketType$Play");
            Class<?> serverClass = Class.forName("com.comphenix.protocol.PacketType$Play$Server");
            Object blockChangeType = serverClass.getField("BLOCK_CHANGE").get(null);

            // Create block data
            Object blockData = createDataMethod.invoke(null, material);

            for (Location loc : locations) {
                // Create packet
                Object packet = createPacketMethod.invoke(protocolManager, blockChangeType);
                
                // Get packet class
                Class<?> packetContainerClass = Class.forName("com.comphenix.protocol.events.PacketContainer");
                
                // Get modifiers
                Method getBlockPositionModifier = packetContainerClass.getMethod("getBlockPositionModifier");
                Object blockPosModifier = getBlockPositionModifier.invoke(packet);
                
                Method getBlockDataModifier = packetContainerClass.getMethod("getBlockData");
                Object blockDataModifier = getBlockDataModifier.invoke(packet);
                
                // Create BlockPosition
                Object blockPosition = blockPositionClass.getConstructor(int.class, int.class, int.class)
                    .newInstance(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                
                // Set values
                Method writeBlockPos = blockPosModifier.getClass().getMethod("write", int.class, Object.class);
                writeBlockPos.invoke(blockPosModifier, 0, blockPosition);
                
                Method writeBlockData = blockDataModifier.getClass().getMethod("write", int.class, Object.class);
                writeBlockData.invoke(blockDataModifier, 0, blockData);
                
                // Send packet
                sendPacketMethod.invoke(protocolManager, player, packet);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send ProtocolLib packets: " + e.getMessage());
        }
    }

    /**
     * Restores real blocks by sending the actual block data to the player.
     */
    private void restoreRealBlocks(Player player, Set<Location> locations) {
        if (protocolLibAvailable) {
            restoreRealBlocksProtocolLib(player, locations);
        } else {
            // Bukkit fallback
            for (Location loc : locations) {
                Material realMaterial = loc.getBlock().getType();
                player.sendBlockChange(loc, realMaterial.createBlockData());
            }
        }
    }

    /**
     * Restores real blocks using ProtocolLib.
     */
    private void restoreRealBlocksProtocolLib(Player player, Set<Location> locations) {
        if (!protocolLibAvailable || protocolManager == null) {
            return;
        }

        try {
            // Get PacketType.Play.Server.BLOCK_CHANGE
            Class<?> serverClass = Class.forName("com.comphenix.protocol.PacketType$Play$Server");
            Object blockChangeType = serverClass.getField("BLOCK_CHANGE").get(null);

            for (Location loc : locations) {
                // Get real block
                Material realMaterial = loc.getBlock().getType();
                Object blockData = createDataMethod.invoke(null, realMaterial);

                // Create packet
                Object packet = createPacketMethod.invoke(protocolManager, blockChangeType);
                
                // Get packet class
                Class<?> packetContainerClass = Class.forName("com.comphenix.protocol.events.PacketContainer");
                
                // Get modifiers
                Method getBlockPositionModifier = packetContainerClass.getMethod("getBlockPositionModifier");
                Object blockPosModifier = getBlockPositionModifier.invoke(packet);
                
                Method getBlockDataModifier = packetContainerClass.getMethod("getBlockData");
                Object blockDataModifier = getBlockDataModifier.invoke(packet);
                
                // Create BlockPosition
                Object blockPosition = blockPositionClass.getConstructor(int.class, int.class, int.class)
                    .newInstance(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                
                // Set values
                Method writeBlockPos = blockPosModifier.getClass().getMethod("write", int.class, Object.class);
                writeBlockPos.invoke(blockPosModifier, 0, blockPosition);
                
                Method writeBlockData = blockDataModifier.getClass().getMethod("write", int.class, Object.class);
                writeBlockData.invoke(blockDataModifier, 0, blockData);
                
                // Send packet
                sendPacketMethod.invoke(protocolManager, player, packet);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to restore blocks: " + e.getMessage());
        }
    }

    /**
     * Clears all barriers for a player.
     */
    public void clearBarrier(Player player) {
        Set<Location> barriers = activeBarriers.remove(player.getUniqueId());
        if (barriers != null && !barriers.isEmpty()) {
            restoreRealBlocks(player, barriers);
        }

        // Cancel update task
        BukkitTask task = updateTasks.remove(player.getUniqueId());
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    /**
     * Clears all barriers for all players (used on plugin disable).
     */
    public void clearAllBarriers() {
        for (UUID playerId : new HashSet<>(activeBarriers.keySet())) {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player != null && player.isOnline()) {
                clearBarrier(player);
            }
        }
        activeBarriers.clear();
        
        // Cancel all update tasks
        for (BukkitTask task : updateTasks.values()) {
            if (!task.isCancelled()) {
                task.cancel();
            }
        }
        updateTasks.clear();
    }

    /**
     * Checks if a player has active barriers.
     */
    public boolean hasActiveBarrier(Player player) {
        return activeBarriers.containsKey(player.getUniqueId());
    }

    /**
     * Updates barrier configuration from config.
     */
    public void reloadConfiguration() {
        loadConfiguration();
    }
}
