package com.muzlik.pvpcombat.events;

import com.muzlik.pvpcombat.data.CombatSession;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when third-party interference is detected in an ongoing combat.
 */
public class InterferenceDetectedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final CombatSession session;
    private final Player interferer;
    private final Player target;
    private final Player legitimateOpponent;
    private boolean cancelled;

    public InterferenceDetectedEvent(CombatSession session, Player interferer,
                                   Player target, Player legitimateOpponent) {
        this.session = session;
        this.interferer = interferer;
        this.target = target;
        this.legitimateOpponent = legitimateOpponent;
        this.cancelled = false;
    }

    public CombatSession getSession() {
        return session;
    }

    public Player getInterferer() {
        return interferer;
    }

    public Player getTarget() {
        return target;
    }

    public Player getLegitimateOpponent() {
        return legitimateOpponent;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}