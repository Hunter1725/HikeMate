package com.example.hikemate.WeatherForecast;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CalendarUtils {
    public static String formattedFullWeek(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE");
        return date.format(formatter);
    }

    public static String formattedWeek(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE");
        return date.format(formatter);
    }

    public static String formattedDayOfWeek(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMMM");
        return date.format(formatter);
    }

    public static String formattedTime(long timestamp){
        Instant instant = Instant.ofEpochSecond(timestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        // Format LocalDateTime in a specific format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateTime.format(formatter);
    }

    public static String formattedMonthDay(long timestamp){
        Instant instant = Instant.ofEpochSecond(timestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        // Format LocalDateTime in a specific format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMMM");
        return dateTime.format(formatter);
    }
}
