package com.care.appointment.infrastructure.db.config;

import java.util.Set;

public class HolidayFilterConfig {
    
    public static final Set<String> ALLOWED_FIELDS = Set.of(
            "holidayId",
            "organizationBranchId",
            "holidayDate",
            "name",
            "reason",
            "isRecurringYearly",
            "isActive",
            "isDeleted",
            "createdById",
            "createdAt",
            "updatedAt"
    );

    public static final Set<String> SORTABLE = Set.of(
            "holidayDate",
            "name",
            "isRecurringYearly",
            "createdAt",
            "updatedAt"
    );

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    private HolidayFilterConfig() {
        // Private constructor to prevent instantiation
    }
}

