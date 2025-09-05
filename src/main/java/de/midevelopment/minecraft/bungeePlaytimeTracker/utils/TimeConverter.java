package de.midevelopment.minecraft.bungeePlaytimeTracker.utils;

import java.time.Duration;

public class TimeConverter {

    public static Duration convertSecondsToDuration(int seconds) {
        return Duration.ofSeconds(seconds);
    }

    public static String convertDurationToTimeString(Duration duration) {
        if (duration == null || duration.isNegative()) return "unknown";
        if (duration.getSeconds() < 60) return duration.getSeconds() + " second(s)";
        if (duration.toMinutes() < 60) return duration.toMinutes() + " minute(s)";
        if (duration.toHours() < 24) return duration.toHours() + " hour(s) and " + duration.minusHours(duration.toHours()).toMinutes() + " minute(s)";
        return duration.toDays() + " day(s) and " + duration.minusDays(duration.toDays()).toHours() + " hour(s)";
    }

    public static String getDetailedTimeString(Duration duration) {
        if (duration == null || duration.isNegative()) return "unknown";
        return duration.toDays() + " day(s), " + duration.minusDays(duration.toDays()).toHours() + " hour(s), " + duration.minusHours(duration.toHours()).toMinutes() + " minute(s) and " + duration.minusMinutes(duration.toMinutes()).getSeconds() + " second(s)";
    }

}
