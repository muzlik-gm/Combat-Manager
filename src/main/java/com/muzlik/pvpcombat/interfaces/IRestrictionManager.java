package com.muzlik.pvpcombat.interfaces;

import org.bukkit.entity.Player;

/**
 * Interface for managing movement and ability restrictions during combat.
 */
public interface IRestrictionManager {

    /**
     * Checks if a player can use an ender pearl.
     *
     * @param player The player to check
     * @return true if allowed, false otherwise
     */
    boolean canUseEnderPearl(Player player);

    /**
     * Checks if a player can use elytra.
     *
     * @param player The player to check
     * @return true if allowed, false otherwise
     */
    boolean canUseElytra(Player player);

    /**
     * Checks if a player can teleport.
     *
     * @param player The player to check
     * @return true if allowed, false otherwise
     */
    boolean canTeleport(Player player);

    /**
     * Checks if a player can break blocks.
     *
     * @param player The player to check
     * @return true if allowed, false otherwise
     */
    boolean canBreakBlocks(Player player);

    /**
     * Checks if a player can place blocks.
     *
     * @param player The player to check
     * @return true if allowed, false otherwise
     */
    boolean canPlaceBlocks(Player player);

    /**
     * Applies a cooldown for restricted actions.
     *
     * @param player The player
     * @param actionType The type of action (e.g., "ender_pearl")
     * @param cooldownSeconds The cooldown duration
     */
    void applyCooldown(Player player, String actionType, int cooldownSeconds);
}