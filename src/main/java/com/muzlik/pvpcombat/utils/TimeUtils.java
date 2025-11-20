package com.muzlik.pvpcombat.utils;

/**
 * Utility class for time-related operations.
 */
public class TimeUtils {

    /**
     * Converts seconds to ticks (20 ticks per second in Minecraft).
     */
    public static long secondsToTicks(long seconds) {
        return seconds * 20L;
    }

    /**
     * Converts ticks to seconds.
     */
    public static long ticksToSeconds(long ticks) {
        return ticks / 20L;
    }

    /**
     * Formats seconds into MM:SS format.
     */
    public static String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    /**
     * Gets the current time in milliseconds.
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Checks if a timestamp has expired based on a duration.
     */
    public static boolean isExpired(long startTime, long durationMillis) {
        return (currentTimeMillis() - startTime) >= durationMillis;
    }
}