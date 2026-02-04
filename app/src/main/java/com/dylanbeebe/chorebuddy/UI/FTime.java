package com.dylanbeebe.chorebuddy.UI;

import java.time.Instant;
import java.time.ZoneId;

public class FTime {
    // TODO: this can be a method inside of Chore.class to be called on getFormattedDuration
    // Currently when ChoreDetails.class hydrates, there is a brief moment where the unformatted millis (or something) is present beneath the timer.

    public static String formatDuration(long millis) {
        long totalSeconds = millis / 1000;

        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d:%02d",
                days, hours, minutes, seconds);
    }

    public static String formatDateLocal(long epochMillis) {
        return Instant.ofEpochMilli(epochMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .toString(); // yyyy-MM-dd
    }

}
