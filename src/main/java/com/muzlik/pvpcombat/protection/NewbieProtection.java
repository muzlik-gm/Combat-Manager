package com.muzlik.pvpcombat.protection;

import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Handles newbie protection system.
 * Protects players without armor from PvP combat.
 */
public class NewbieProtection {

    private final PvPCombatPlugin plugin;

    public NewbieProtection(PvPCombatPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks if newbie protection is enabled.
     */
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("newbie-protection.enabled", true);
    }

    /**
     * Checks if a player is considered a newbie (no armor and low XP).
     */
    public boolean isNewbie(Player player) {
        // Check if player has bypass permission
        if (player.hasPermission("pvpcombat.bypass.newbie")) {
            plugin.getLoggingManager().log("[NEWBIE CHECK] " + player.getName() + " has bypass permission");
            return false;
        }

        // Check XP level threshold
        int xpThreshold = plugin.getConfig().getInt("newbie-protection.xp-level-threshold", 3);
        int playerLevel = player.getLevel();
        
        plugin.getLoggingManager().log("[NEWBIE CHECK] " + player.getName() + " - Level: " + playerLevel + ", Threshold: " + xpThreshold);
        
        if (playerLevel > xpThreshold) {
            plugin.getLoggingManager().log("[NEWBIE CHECK] " + player.getName() + " has too much XP, not a newbie");
            return false; // Player has enough XP, not a newbie
        }

        // Check if player has armor
        boolean hasArmorEquipped = hasArmor(player);
        plugin.getLoggingManager().log("[NEWBIE CHECK] " + player.getName() + " has armor: " + hasArmorEquipped);
        
        boolean isNewbie = !hasArmorEquipped;
        plugin.getLoggingManager().log("[NEWBIE CHECK] " + player.getName() + " IS NEWBIE: " + isNewbie);
        
        return isNewbie;
    }

    /**
     * Checks if a player has armor equipped.
     */
    private boolean hasArmor(Player player) {
        boolean requireAnyArmor = plugin.getConfig().getBoolean("newbie-protection.require-any-armor", true);

        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();
        
        // Check if armor pieces are not null AND not AIR
        boolean hasHelmet = helmet != null && helmet.getType() != org.bukkit.Material.AIR;
        boolean hasChestplate = chestplate != null && chestplate.getType() != org.bukkit.Material.AIR;
        boolean hasLeggings = leggings != null && leggings.getType() != org.bukkit.Material.AIR;
        boolean hasBoots = boots != null && boots.getType() != org.bukkit.Material.AIR;
        
        plugin.getLoggingManager().log("[ARMOR CHECK] " + player.getName() + " - Helmet:" + hasHelmet + " Chest:" + hasChestplate + " Legs:" + hasLeggings + " Boots:" + hasBoots);

        if (requireAnyArmor) {
            // Player needs at least ONE armor piece
            return hasHelmet || hasChestplate || hasLeggings || hasBoots;
        } else {
            // Player needs FULL armor set
            return hasHelmet && hasChestplate && hasLeggings && hasBoots;
        }
    }

    /**
     * Checks if a newbie can deal damage.
     * Returns FALSE if newbie should be blocked from dealing damage.
     */
    public boolean canNewbieDealDamage(Player newbie) {
        if (!isEnabled()) {
            plugin.getLoggingManager().log("[NEWBIE DAMAGE] Protection disabled, allowing damage");
            return true; // Protection disabled, allow damage
        }

        if (!isNewbie(newbie)) {
            plugin.getLoggingManager().log("[NEWBIE DAMAGE] " + newbie.getName() + " is not a newbie, allowing damage");
            return true; // Not a newbie, allow damage
        }

        // If prevent-damage-dealing is TRUE, newbie CANNOT deal damage (return FALSE)
        // If prevent-damage-dealing is FALSE, newbie CAN deal damage (return TRUE)
        boolean preventDealing = plugin.getConfig().getBoolean("newbie-protection.prevent-damage-dealing", true);
        boolean canDealDamage = !preventDealing;
        
        plugin.getLoggingManager().log("[NEWBIE DAMAGE] " + newbie.getName() + " prevent-dealing=" + preventDealing + ", can deal damage=" + canDealDamage);
        
        return canDealDamage; // Invert: if prevent=true, return false (block damage)
    }

    /**
     * Checks if a newbie can receive damage.
     * Returns FALSE if newbie should be protected from receiving damage.
     */
    public boolean canNewbieReceiveDamage(Player newbie) {
        if (!isEnabled()) {
            return true; // Protection disabled, allow damage
        }

        if (!isNewbie(newbie)) {
            return true; // Not a newbie, allow damage
        }

        // If prevent-damage-receiving is TRUE, newbie CANNOT receive damage (return FALSE)
        // If prevent-damage-receiving is FALSE, newbie CAN receive damage (return TRUE)
        boolean preventReceiving = plugin.getConfig().getBoolean("newbie-protection.prevent-damage-receiving", true);
        return !preventReceiving; // Invert: if prevent=true, return false (block damage)
    }

    /**
     * Gets the message to send to a newbie trying to attack.
     */
    public String getNewbieAttackMessage() {
        return plugin.getConfig().getString("newbie-protection.newbie-attack-message",
            "&cYou need armor to attack other players!")
            .replace("&", "§");
    }

    /**
     * Gets the message to send when attacking a newbie.
     */
    public String getAttackingNewbieMessage() {
        return plugin.getConfig().getString("newbie-protection.attacking-newbie-message",
            "&cYou cannot attack players without armor!")
            .replace("&", "§");
    }
}
