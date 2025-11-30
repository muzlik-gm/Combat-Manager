package com.muzlik.pvpcombat.integration;

import com.muzlik.pvpcombat.combat.CombatManager;
import com.muzlik.pvpcombat.combat.CombatTracker;
import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import com.muzlik.pvpcombat.data.CombatSession;
import com.muzlik.pvpcombat.data.PlayerCombatData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * PlaceholderAPI expansion for PvPCombat plugin.
 * Provides placeholders for combat statistics and session data.
 */
public class PvPCombatExpansion extends PlaceholderExpansion {

    private final PvPCombatPlugin plugin;

    public PvPCombatExpansion(PvPCombatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "pvpcombat";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // Required to keep expansion loaded
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        CombatManager combatManager = (CombatManager) plugin.getCombatManager();
        CombatTracker combatTracker = combatManager.getCombatTracker();

        // Combat status placeholders
        if (identifier.equals("in_combat")) {
            return String.valueOf(combatManager.isInCombat(player));
        }

        if (identifier.equals("time_left")) {
            if (!combatManager.isInCombat(player)) {
                return "0";
            }
            CombatSession session = combatManager.getActiveSessions().values().stream()
                .filter(s -> s.involvesPlayer(player))
                .findFirst()
                .orElse(null);
            return session != null ? String.valueOf(session.getRemainingTime()) : "0";
        }

        if (identifier.equals("opponent")) {
            Player opponent = combatManager.getOpponent(player);
            return opponent != null ? opponent.getName() : "None";
        }

        // Lifetime statistics placeholders
        PlayerCombatData data = combatTracker.getPlayerData(player.getUniqueId());
        if (data == null) {
            return "0";
        }

        switch (identifier) {
            case "wins":
                return String.valueOf(data.getWins());
            case "losses":
                return String.valueOf(data.getLosses());
            case "total_combats":
                return String.valueOf(data.getTotalCombats());
            case "kd_ratio":
                return String.format("%.2f", data.getKDRatio());
            case "win_rate":
                return String.format("%.1f", data.getWinRate());
            case "total_damage_dealt":
                return String.format("%.1f", data.getTotalDamageDealt());
            case "total_damage_received":
                return String.format("%.1f", data.getTotalDamageReceived());
            case "damage_ratio":
                return String.format("%.2f", data.getDamageRatio());
            case "total_combat_time":
                return String.valueOf(data.getTotalCombatTime());
            
            // Session-specific placeholders
            case "session_damage_dealt":
                if (!combatManager.isInCombat(player)) return "0.0";
                CombatSession session = combatManager.getActiveSessions().values().stream()
                    .filter(s -> s.involvesPlayer(player))
                    .findFirst()
                    .orElse(null);
                return session != null ? String.format("%.1f", session.getDamageDealt(player)) : "0.0";
                
            case "session_damage_received":
                if (!combatManager.isInCombat(player)) return "0.0";
                session = combatManager.getActiveSessions().values().stream()
                    .filter(s -> s.involvesPlayer(player))
                    .findFirst()
                    .orElse(null);
                return session != null ? String.format("%.1f", session.getDamageReceived(player)) : "0.0";
                
            case "session_hits_landed":
                if (!combatManager.isInCombat(player)) return "0";
                session = combatManager.getActiveSessions().values().stream()
                    .filter(s -> s.involvesPlayer(player))
                    .findFirst()
                    .orElse(null);
                return session != null ? String.valueOf(session.getHitsLanded(player)) : "0";
                
            default:
                return null;
        }
    }
}
