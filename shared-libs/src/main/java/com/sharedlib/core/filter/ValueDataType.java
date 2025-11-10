package com.sharedlib.core.filter;

// core-shared-lib/src/main/java/com/sharedlib/filter/ValueDataType.java


public enum ValueDataType {
    STRING,
    NUMBER,
    UUID,
    DATE,                // LocalDate yyyy-MM-dd
    INSTANT,             // 2025-08-07T12:00:00Z
    OFFSET_DATE_TIME,    // 2025-08-07T12:00:00+02:00
    BOOLEAN,
    ENUM
}
