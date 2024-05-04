package com.example.appxemphim.util;

import java.time.Duration;

public class ConvertISO8601Time {
    public static int toIntSecond(String durationString){
        Duration duration = Duration.parse(durationString);
        return (int) duration.getSeconds();
    }
    public static String toStringMinute(String durationString){
        Duration duration = Duration.parse(durationString);
        int totalSeconds = (int) duration.getSeconds();
        return formatSeconds(totalSeconds);
    }
    public static String formatSeconds(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
