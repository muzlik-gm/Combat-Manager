package com.muzlik.pvpcombat.events;

import com.muzlik.pvpcombat.data.CombatSession;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when combat begins between two players.
 */
public class CombatStartEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final CombatSession session;
    private final Player attacker;
    private final Player defender;

    public CombatStartEvent(CombatSession session, Player attacker, Player defender) {
        this.session = session;
        this.attacker = attacker;
        this.defender = defender;
    }

    public CombatSession getSession() {
        return session;
    }

    public Player getAttacker() {
        return attacker;
    }

    public Player getDefender() {
        return defender;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}