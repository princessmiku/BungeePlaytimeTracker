package de.midevelopment.minecraft.bungeePlaytimeTracker.utils;

import java.time.Duration;

public class TimeConverter {

    public static Duration convertSecondsToDuration(int seconds) {
        return Duration.ofSeconds(seconds);
    }

    public static String convertDurationToTimeString(Duration duration) {
        if (duration == null || duration.isNegative()) return LocaleHandler.get("unknown");
        if (duration.getSeconds() < 60)
            return duration.getSeconds() + (duration.getSeconds() == 1 ? " " + LocaleHandler.get("second") : " " + LocaleHandler.get("seconds"));
        if (duration.toMinutes() < 60)
            return duration.toMinutes() + (duration.toMinutes() == 1 ? " " + LocaleHandler.get("minute") : " " + LocaleHandler.get("minutes"));
        if (duration.toHours() < 24)
            return duration.toHours() + (duration.toHours() == 1 ? " " + LocaleHandler.get("hour") : " " + LocaleHandler.get("hours")) + " " + LocaleHandler.get("and") + " " + duration.minusHours(duration.toHours()).toMinutes() + (duration.minusHours(duration.toHours()).toMinutes() == 1 ? " " + LocaleHandler.get("minute") : " " + LocaleHandler.get("minutes"));
        return duration.toDays() + (duration.toDays() == 1 ? " " + LocaleHandler.get("day") : " " + LocaleHandler.get("days")) + " " + LocaleHandler.get("and") + " " + duration.minusDays(duration.toDays()).toHours() + (duration.minusDays(duration.toDays()).toHours() == 1 ? " " + LocaleHandler.get("hour") : " " + LocaleHandler.get("hours"));
    }

    public static String convertDurationToTimeStringShort(Duration duration) {
        if (duration == null || duration.isNegative()) return LocaleHandler.get("unknown");
        if (duration.getSeconds() < 60)
            return duration.getSeconds() + (duration.getSeconds() == 1 ? " " + LocaleHandler.get("second") : " " + LocaleHandler.get("seconds"));
        if (duration.toMinutes() < 60)
            return duration.toMinutes() + (duration.toMinutes() == 1 ? " " + LocaleHandler.get("minute") : " " + LocaleHandler.get("minutes"));
        if (duration.toHours() < 24)
            return duration.toHours() + (duration.toHours() == 1 ? " " + LocaleHandler.get("hour") : " " + LocaleHandler.get("hours")) + ", " + duration.minusHours(duration.toHours()).toMinutes() + (duration.minusHours(duration.toHours()).toMinutes() == 1 ? " " + LocaleHandler.get("minute") : " " + LocaleHandler.get("minutes"));
        return duration.toDays() + (duration.toDays() == 1 ? " " + LocaleHandler.get("day") : " " + LocaleHandler.get("days")) + ", " + duration.minusDays(duration.toDays()).toHours() + (duration.minusDays(duration.toDays()).toHours() == 1 ? " " + LocaleHandler.get("hour") : " " + LocaleHandler.get("hours"));
    }

    public static String getDetailedTimeString(Duration duration) {
        if (duration == null || duration.isNegative()) return LocaleHandler.get("unknown");
        return duration.toDays() + (duration.toDays() == 1 ? " " + LocaleHandler.get("day") : " " + LocaleHandler.get("days")) + ", " +
                duration.minusDays(duration.toDays()).toHours() + (duration.minusDays(duration.toDays()).toHours() == 1 ? " " + LocaleHandler.get("hour") : " " + LocaleHandler.get("hours")) + ", " +
                duration.minusHours(duration.toHours()).toMinutes() + (duration.minusHours(duration.toHours()).toMinutes() == 1 ? " " + LocaleHandler.get("minute") : " " + LocaleHandler.get("minutes")) + " " + LocaleHandler.get("and") + " " +
                duration.minusMinutes(duration.toMinutes()).getSeconds() + (duration.minusMinutes(duration.toMinutes()).getSeconds() == 1 ? " " + LocaleHandler.get("second") : " " + LocaleHandler.get("seconds"));
    }
}
