package com.muzlik.pvpcombat.combat;

import com.muzlik.pvpcombat.core.PvPCombatPlugin;
import com.muzlik.pvpcombat.data.CombatSession;
import com.muzlik.pvpcombat.events.InterferenceDetectedEvent;
import com.muzlik.pvpcombat.interfaces.ICombatManager;
import com.muzlik.pvpcombat.visual.ActionBarManager;
import com.muzlik.pvpcombat.visual.SoundManager;
import org.bukkit.entity.Player;

/**
 * Detects and handles third-party interference in ongoing combats.
 */
public class AntiInterferenceManager {

    private final PvPCombatPlugin plugin;
    private final ICombatManager combatManager;

    public AntiInterferenceManager(PvPCombatPlugin plugin, ICombatManager combatManager) {
        this.plugin = plugin;
        this.combatManager = combatManager;
    }

    /**
     * Checks if interference is detected when a player hits another.
     */
    public boolean checkInterference(Player hitter, Player target) {
        // Check if target is in combat
        if (!combatManager.isInCombat(target)) {
            return false; // No combat, no interference
        }

        // Check if hitter is the opponent in the combat
        Player opponent = combatManager.getOpponent(target);
        if (hitter.equals(opponent)) {
            return false; // This is the legitimate opponent
        }

        // Interference detected - someone else is hitting a player in combat
        return true;
    }

    /**
     * Handles detected interference.
     */
    public void handleInterference(Player interferer, Player target) {
        Player opponent = combatManager.getOpponent(target);

        // Get the combat session
        CombatSession session = ((CombatManager) combatManager).getActiveSessions().get(target.getUniqueId());

        // Record the interference in the session
        if (session != null) {
            session.recordInterference(interferer, target);
        }

        // Log the interference
        plugin.getLogger().info("Interference detected: " + interferer.getName() +
            " tried to hit " + target.getName() + " who is in combat with " + opponent.getName());

        // Send configurable message to interferer
        if (interferer != null && isInterferenceEnabled()) {
            String message = getInterferenceMessage(interferer.getName(), target.getName(), opponent.getName());
            getActionBarManager().sendActionBar(interferer, message);

            // Play interference sound
            if (isInterferenceSoundEnabled()) {
                getSoundManager().playInterferenceSound(interferer);
            }
        }

        // Fire InterferenceDetectedEvent
        if (session != null) {
            InterferenceDetectedEvent event = new InterferenceDetectedEvent(session, interferer, target, opponent);
            plugin.getServer().getPluginManager().callEvent(event);
        }
    }

    /**
     * Checks if interference detection is enabled.
     */
    public boolean isInterferenceEnabled() {
        return plugin.getConfig().getBoolean("anticheat.interference.enabled", true);
    }

    /**
     * Checks if interference should block the hit.
     */
    public boolean shouldBlockInterference() {
        return plugin.getConfig().getBoolean("anticheat.interference.block-hits", false);
    }

    /**
     * Checks if interference sound is enabled.
     */
    public boolean isInterferenceSoundEnabled() {
        return plugin.getConfig().getBoolean("anticheat.interference.sound.enabled", true);
    }

    /**
     * Gets the formatted interference message.
     */
    public String getInterferenceMessage(String interferer, String target, String opponent) {
        String format = plugin.getConfig().getString("anticheat.interference.message",
            "&c{interferer} cannot interfere: &f{target} &cis already in combat with &f{opponent}!");

        return format.replace("{interferer}", interferer)
                    .replace("{target}", target)
                    .replace("{opponent}", opponent);
    }

    /**
     * Gets the ActionBarManager instance.
     */
    private ActionBarManager getActionBarManager() {
        // Access through VisualManager since CombatManager has VisualManager
        if (combatManager instanceof CombatManager) {
            return ((CombatManager) combatManager).getVisualManager().getActionBarManager();
        }
        return null;
    }

    /**
     * Gets the SoundManager instance.
     */
    private SoundManager getSoundManager() {
        // Access through VisualManager since CombatManager has VisualManager
        if (combatManager instanceof CombatManager) {
            return ((CombatManager) combatManager).getVisualManager().getSoundManager();
        }
        return null;
    }
}