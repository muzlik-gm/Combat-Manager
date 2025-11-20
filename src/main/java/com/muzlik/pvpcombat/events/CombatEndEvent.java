package com.muzlik.pvpcombat.events;

import com.muzlik.pvpcombat.data.CombatSession;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when combat ends between two players.
 */
public class CombatEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final CombatSession session;
    private final Player winner;
    private final Player loser;
    private final CombatEndReason reason;

    public enum CombatEndReason {
        TIMER_EXPIRED,
        PLAYER_DEATH,
        PLAYER_LOGOUT,
        FORCE_END
    }

    public CombatEndEvent(CombatSession session, Player winner, Player loser, CombatEndReason reason) {
        this.session = session;
        this.winner = winner;
        this.loser = loser;
        this.reason = reason;
    }

    public CombatSession getSession() {
        return session;
    }

    public Player getWinner() {
        return winner;
    }

    public Player getLoser() {
        return loser;
    }

    public CombatEndReason getReason() {
        return reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}