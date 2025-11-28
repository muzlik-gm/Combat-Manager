package com.muzlik.pvpcombat.admin;

import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.attribute.Attribute;

/**
 * Admin inspection tools for viewing real-time combat info.
 * Displays time left, hits traded, current status.
 */
public class CombatInspector {

    private final PvPCombatPlugin plugin;

    public CombatInspector(PvPCombatPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Inspects a player's current combat state.
     */
    public void inspectPlayer(Player inspector, Player target) {
        inspector.sendMessage("§6=== Inspecting: " + target.getName() + " ===");

        if (plugin.getCombatManager() == null) {
            inspector.sendMessage("§cCombat system is not available.");
            return;
        }

        if (!plugin.getCombatManager().isInCombat(target)) {
            inspector.sendMessage("§ePlayer is not in combat.");
            return;
        }

        Player opponent = plugin.getCombatManager().getOpponent(target);

        // Show combat details
        inspector.sendMessage("§eStatus: §cIn Active Combat");
        inspector.sendMessage("§eOpponent: §f" + (opponent != null ? opponent.getName() : "Unknown"));
        inspector.sendMessage("§eLocation: §f" + formatLocation(target));
        inspector.sendMessage("§eHealth: §f" + String.format("%.1f/%.1f", target.getHealth(), ((LivingEntity) target).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));

        // Show combat statistics
        showCombatStats(inspector, target);

        // Show debug info if available
        showDebugInfo(inspector, target);
    }

    /**
     * Shows player combat summary.
     */
    public void showPlayerSummary(Player inspector, Player target) {
        try {
            // Get player's combat data from the CombatManager's tracker
            com.muzlik.pvpcombat.data.PlayerCombatData combatData = 
                ((com.muzlik.pvpcombat.combat.CombatManager) plugin.getCombatManager())
                    .getCombatTracker().getPlayerData(target.getUniqueId());

            inspector.sendMessage("§6=== Combat Summary for " + target.getName() + " ===");

            if (combatData == null || (combatData.getTotalCombats() == 0 && combatData.getTotalDamageDealt() == 0)) {
                inspector.sendMessage("§eNo combat statistics yet. Player needs to fight to generate data!");
                return;
            }

            // Display summary
            inspector.sendMessage(String.format("§eTotal Combats: §f%d", combatData.getTotalCombats()));
            
            if (combatData.getTotalCombats() > 0) {
                inspector.sendMessage(String.format("§eWins: §a%d §7| §eLosses: §c%d", 
                    combatData.getWins(), combatData.getLosses()));
                
                // Calculate win rate
                double winRate = (double) combatData.getWins() / combatData.getTotalCombats() * 100.0;
                inspector.sendMessage(String.format("§eWin Rate: §f%.1f%%", winRate));
                
                // Calculate K/D ratio
                double kdRatio = combatData.getLosses() > 0 ? 
                    (double) combatData.getWins() / combatData.getLosses() : combatData.getWins();
                inspector.sendMessage(String.format("§eK/D Ratio: §f%.2f", kdRatio));
            }
            
            inspector.sendMessage(String.format("§eDamage Dealt: §c%.1f ❤", combatData.getTotalDamageDealt()));
            inspector.sendMessage(String.format("§eDamage Received: §c%.1f ❤", combatData.getTotalDamageReceived()));
            
            // Calculate damage ratio
            if (combatData.getTotalDamageReceived() > 0) {
                double damageRatio = combatData.getTotalDamageDealt() / combatData.getTotalDamageReceived();
                inspector.sendMessage(String.format("§eDamage Ratio: §f%.2f", damageRatio));
            }
            
            // Show combat time
            if (combatData.getTotalCombatTime() > 0) {
                long totalMinutes = combatData.getTotalCombatTime() / 60000;
                long totalSeconds = (combatData.getTotalCombatTime() % 60000) / 1000;
                inspector.sendMessage(String.format("§eTotal Combat Time: §f%dm %ds", totalMinutes, totalSeconds));
            }
            
            // Show last combat time
            if (combatData.getLastCombat() != null) {
                inspector.sendMessage("§7Last Combat: §f" + combatData.getLastCombat().toString());
            }
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error showing player summary: " + e.getMessage());
            e.printStackTrace();
            inspector.sendMessage("§cFailed to show player summary.");
        }
    }

    /**
     * Shows real-time combat statistics.
     */
    private void showCombatStats(Player inspector, Player target) {
        try {
            // Get player's combat data from the CombatManager's tracker
            com.muzlik.pvpcombat.data.PlayerCombatData combatData = 
                ((com.muzlik.pvpcombat.combat.CombatManager) plugin.getCombatManager())
                    .getCombatTracker().getPlayerData(target.getUniqueId());

            inspector.sendMessage("§e=== Real-time Stats ===");
            
            if (combatData != null) {
                inspector.sendMessage(String.format("§eDamage Dealt: §c%.1f ❤", combatData.getTotalDamageDealt()));
                inspector.sendMessage(String.format("§eDamage Received: §c%.1f ❤", combatData.getTotalDamageReceived()));
                inspector.sendMessage(String.format("§eWins: §a%d §7| §eLosses: §c%d", 
                    combatData.getWins(), combatData.getLosses()));
            } else {
                inspector.sendMessage("§eNo combat data available yet.");
            }
            
            inspector.sendMessage("§eCombat State: §fActive");
            inspector.sendMessage("§eRestrictions: §fNone §7(Restriction tracking pending)");
        } catch (Exception e) {
            plugin.getLogger().warning("Error showing combat stats: " + e.getMessage());
            inspector.sendMessage("§cFailed to load combat statistics.");
        }
    }

    /**
     * Shows debug information.
     */
    private void showDebugInfo(Player inspector, Player target) {
        // Debug info would be shown here when debug manager is integrated
        // For now, skip - debug info is shown separately

        inspector.sendMessage("§c=== Debug Information ===");
        inspector.sendMessage("§ePlayer UUID: §f" + target.getUniqueId());
        inspector.sendMessage("§eWorld: §f" + target.getWorld().getName());
        inspector.sendMessage("§eCoordinates: §f" + formatLocation(target));
        inspector.sendMessage("§eGameMode: §f" + target.getGameMode());
        inspector.sendMessage("§eFly Speed: §f" + target.getFlySpeed());
        inspector.sendMessage("§eWalk Speed: §f" + target.getWalkSpeed());
    }

    /**
     * Formats player location.
     */
    private String formatLocation(Player player) {
        return String.format("%.1f, %.1f, %.1f",
            player.getLocation().getX(),
            player.getLocation().getY(),
            player.getLocation().getZ());
    }
}