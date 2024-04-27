package com.example.appxemphim.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class ConvertDateToDayAgo {
    public static String convert(LocalDateTime pastDateTime, String dayAgoText, String monthAgoText, String yearAgoText) {
        // Thời điểm hiện tại
        LocalDateTime now = LocalDateTime.now();

        // Tính khoảng cách thời gian
        Duration duration = Duration.between(pastDateTime, now);

        // Tính số ngày, tháng và năm
        long days = duration.toDays();
        long months = days / 30;
        long years = days / 365;

        if (years > 0) {
            return years + " " + yearAgoText;
        } else if (months > 0) {
            return months + " " + monthAgoText;
        } else {
            return  days + " " + dayAgoText;
        }
    }
}
