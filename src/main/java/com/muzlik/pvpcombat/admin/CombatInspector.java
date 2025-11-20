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
        inspector.sendMessage("§6=== Combat Summary: " + target.getName() + " ===");

        // Placeholder for combat statistics
        inspector.sendMessage("§eTotal Combats: §fUnknown §7(Statistics pending)");
        inspector.sendMessage("§eWins/Losses: §fUnknown §7(Statistics pending)");
        inspector.sendMessage("§eTotal Damage Dealt: §fUnknown §7(Statistics pending)");
        inspector.sendMessage("§eLast Combat: §fUnknown §7(Statistics pending)");

        inspector.sendMessage("§7Detailed combat history available in future updates.");
    }

    /**
     * Shows real-time combat statistics.
     */
    private void showCombatStats(Player inspector, Player target) {
        inspector.sendMessage("§e=== Real-time Stats ===");
        inspector.sendMessage("§eHits Traded: §fUnknown §7(Live tracking pending)");
        inspector.sendMessage("§eTime in Combat: §fUnknown §7(Timer tracking pending)");
        inspector.sendMessage("§eCombat State: §fActive §7(State tracking pending)");
        inspector.sendMessage("§eRestrictions: §fNone §7(Restriction tracking pending)");
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