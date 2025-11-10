package com.sharedlib.core.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date formatting and parsing.
 * Thread-safe and reusable across microservices.
 */
public final class DateUtil {
    private DateUtil() {}

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Formats a LocalDate to yyyy-MM-dd string.
     */
    public static String format(LocalDate date) {
        return date != null ? date.format(DATE_FORMAT) : null;
    }

    /**
     * Formats a LocalDateTime to yyyy-MM-dd HH:mm:ss string.
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMAT) : null;
    }

    /**
     * Parses a string to LocalDate using yyyy-MM-dd format.
     */
    public static LocalDate parseDate(String dateStr) {
        return dateStr != null ? LocalDate.parse(dateStr, DATE_FORMAT) : null;
    }

    /**
     * Parses a string to LocalDateTime using yyyy-MM-dd HH:mm:ss format.
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr, DATETIME_FORMAT) : null;
    }
}
