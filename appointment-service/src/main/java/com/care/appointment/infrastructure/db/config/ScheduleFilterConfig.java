package com.care.appointment.infrastructure.db.config;

import java.util.Set;

public class ScheduleFilterConfig {
    
    public static final Set<String> ALLOWED_FIELDS = Set.of(
            "scheduleId",
            "organizationBranchId",
            "dayOfWeek",
            "startTime",
            "endTime",
            "slotDurationMinutes",
            "maxCapacityPerSlot",
            "isActive",
            "isDeleted",
            "createdById",
            "createdAt",
            "updatedAt"
    );

    public static final Set<String> SORTABLE = Set.of(
            "dayOfWeek",
            "startTime",
            "endTime",
            "slotDurationMinutes",
            "createdAt",
            "updatedAt"
    );

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    private ScheduleFilterConfig() {
        // Private constructor to prevent instantiation
    }
}

