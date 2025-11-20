package com.muzlik.pvpcombat.data;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Stores historical combat events for replay and analysis capabilities.
 */
public class CombatEventHistory {

    private final UUID sessionId;
    private final NavigableMap<LocalDateTime, List<CombatEvent>> eventTimeline;
    private final Map<UUID, List<CombatEvent>> playerEvents;
    private final int maxEventsPerSession;

    public CombatEventHistory(UUID sessionId, int maxEventsPerSession) {
        this.sessionId = sessionId;
        this.eventTimeline = new ConcurrentSkipListMap<>();
        this.playerEvents = new ConcurrentHashMap<>();
        this.maxEventsPerSession = maxEventsPerSession;
    }

    public CombatEventHistory(UUID sessionId) {
        this(sessionId, 1000); // Default max events
    }

    /**
     * Adds a combat event to the history.
     */
    public void addEvent(CombatEvent event) {
        LocalDateTime timestamp = LocalDateTime.now();

        // Add to timeline
        eventTimeline.computeIfAbsent(timestamp, k -> new CopyOnWriteArrayList<>()).add(event);

        // Add to player-specific events
        playerEvents.computeIfAbsent(event.getPlayerId(), k -> new CopyOnWriteArrayList<>()).add(event);

        // Cleanup if exceeding max events
        if (getTotalEventCount() > maxEventsPerSession) {
            cleanupOldEvents();
        }
    }

    /**
     * Gets all events for a specific time range.
     */
    public List<CombatEvent> getEventsInRange(LocalDateTime start, LocalDateTime end) {
        List<CombatEvent> events = new ArrayList<>();
        eventTimeline.subMap(start, true, end, true).values()
            .forEach(events::addAll);
        return events;
    }

    /**
     * Gets all events for a specific player.
     */
    public List<CombatEvent> getPlayerEvents(UUID playerId) {
        return new ArrayList<>(playerEvents.getOrDefault(playerId, Collections.emptyList()));
    }

    /**
     * Gets events of a specific type.
     */
    public List<CombatEvent> getEventsByType(String eventType) {
        List<CombatEvent> filteredEvents = new ArrayList<>();
        for (List<CombatEvent> events : eventTimeline.values()) {
            for (CombatEvent event : events) {
                if (event.getEventType().equals(eventType)) {
                    filteredEvents.add(event);
                }
            }
        }
        return filteredEvents;
    }

    /**
     * Gets the most recent events (for replay).
     */
    public List<CombatEvent> getRecentEvents(int count) {
        List<CombatEvent> recentEvents = new ArrayList<>();
        int collected = 0;

        // Start from the most recent entries
        for (Map.Entry<LocalDateTime, List<CombatEvent>> entry : eventTimeline.descendingMap().entrySet()) {
            for (CombatEvent event : entry.getValue()) {
                recentEvents.add(event);
                collected++;
                if (collected >= count) {
                    break;
                }
            }
            if (collected >= count) {
                break;
            }
        }

        return recentEvents;
    }

    /**
     * Creates a replay sequence of events.
     */
    public List<CombatEvent> createReplaySequence() {
        List<CombatEvent> replaySequence = new ArrayList<>();
        for (List<CombatEvent> events : eventTimeline.values()) {
            replaySequence.addAll(events);
        }
        return replaySequence;
    }

    /**
     * Gets the total number of events stored.
     */
    public int getTotalEventCount() {
        return eventTimeline.values().stream().mapToInt(List::size).sum();
    }

    /**
     * Gets the number of events for a specific player.
     */
    public int getPlayerEventCount(UUID playerId) {
        return playerEvents.getOrDefault(playerId, Collections.emptyList()).size();
    }

    /**
     * Cleans up old events to stay within memory limits.
     */
    private void cleanupOldEvents() {
        // Remove oldest 10% of events
        int eventsToRemove = maxEventsPerSession / 10;
        int removed = 0;

        Iterator<Map.Entry<LocalDateTime, List<CombatEvent>>> iterator = eventTimeline.entrySet().iterator();
        while (iterator.hasNext() && removed < eventsToRemove) {
            Map.Entry<LocalDateTime, List<CombatEvent>> entry = iterator.next();
            removed += entry.getValue().size();
            iterator.remove();
        }
    }

    /**
     * Clears all event history.
     */
    public void clearHistory() {
        eventTimeline.clear();
        playerEvents.clear();
    }

    /**
     * Gets timeline metadata.
     */
    public LocalDateTime getStartTime() {
        return eventTimeline.isEmpty() ? null : eventTimeline.firstKey();
    }

    public LocalDateTime getEndTime() {
        return eventTimeline.isEmpty() ? null : eventTimeline.lastKey();
    }

    public long getDurationSeconds() {
        if (eventTimeline.isEmpty()) return 0;
        return java.time.Duration.between(getStartTime(), getEndTime()).getSeconds();
    }

    // Getters
    public UUID getSessionId() { return sessionId; }
    public int getMaxEventsPerSession() { return maxEventsPerSession; }
    public Set<UUID> getPlayersInvolved() { return new HashSet<>(playerEvents.keySet()); }
}