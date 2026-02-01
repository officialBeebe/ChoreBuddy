package com.dylanbeebe.chorebuddy.UI;

public class TimeFormatter {
    public static String formatDuration(long millis) {
        long totalSeconds = millis / 1000;

        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d:%02d",
                days, hours, minutes, seconds);
    }
}
