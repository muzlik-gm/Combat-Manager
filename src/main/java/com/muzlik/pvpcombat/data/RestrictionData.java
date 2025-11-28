package com.muzlik.pvpcombat.data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Model class for storing player-specific restriction data during combat.
 */
public class RestrictionData {
    private final UUID playerId;
    private final Map<String, LocalDateTime> activeCooldowns;
    private boolean elytraGlideBlocked;
    private LocalDateTime lastEnderPearlUse;
    private LocalDateTime lastElytraBoost;
    private LocalDateTime lastGlideStart;
    private LocalDateTime lastGoldenAppleUse;
    private LocalDateTime lastEnchantedGoldenAppleUse;
    private double altitudeAtGlideStart;

    public RestrictionData(UUID playerId) {
        this.playerId = playerId;
        this.activeCooldowns = new HashMap<>();
        this.elytraGlideBlocked = false;
        this.altitudeAtGlideStart = 0.0;
    }

    // Getters and setters
    public UUID getPlayerId() { return playerId; }

    public Map<String, LocalDateTime> getActiveCooldowns() { return activeCooldowns; }

    public boolean isOnCooldown(String actionType) {
        LocalDateTime expiry = activeCooldowns.get(actionType);
        return expiry != null && LocalDateTime.now().isBefore(expiry);
    }

    public void setCooldown(String actionType, int seconds) {
        activeCooldowns.put(actionType, LocalDateTime.now().plusSeconds(seconds));
    }

    public void removeCooldown(String actionType) {
        activeCooldowns.remove(actionType);
    }

    public boolean isElytraGlideBlocked() { return elytraGlideBlocked; }
    public void setElytraGlideBlocked(boolean blocked) { this.elytraGlideBlocked = blocked; }

    public LocalDateTime getLastEnderPearlUse() { return lastEnderPearlUse; }
    public void setLastEnderPearlUse(LocalDateTime lastUse) { this.lastEnderPearlUse = lastUse; }

    public LocalDateTime getLastElytraBoost() { return lastElytraBoost; }
    public void setLastElytraBoost(LocalDateTime lastBoost) { this.lastElytraBoost = lastBoost; }

    public LocalDateTime getLastGlideStart() { return lastGlideStart; }
    public void setLastGlideStart(LocalDateTime glideStart) { this.lastGlideStart = glideStart; }

    public double getAltitudeAtGlideStart() { return altitudeAtGlideStart; }
    public void setAltitudeAtGlideStart(double altitude) { this.altitudeAtGlideStart = altitude; }

    public LocalDateTime getLastGoldenAppleUse() { return lastGoldenAppleUse; }
    public void setLastGoldenAppleUse(LocalDateTime lastUse) { this.lastGoldenAppleUse = lastUse; }

    public LocalDateTime getLastEnchantedGoldenAppleUse() { return lastEnchantedGoldenAppleUse; }
    public void setLastEnchantedGoldenAppleUse(LocalDateTime lastUse) { this.lastEnchantedGoldenAppleUse = lastUse; }

    public void clearAllRestrictions() {
        activeCooldowns.clear();
        elytraGlideBlocked = false;
        lastEnderPearlUse = null;
        lastElytraBoost = null;
        lastGlideStart = null;
        lastGoldenAppleUse = null;
        lastEnchantedGoldenAppleUse = null;
        altitudeAtGlideStart = 0.0;
    }
}