package com.care.appointment.infrastructure.db.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class AppointmentStatusFilterConfig {

    public static final Set<String> ALLOWED_FIELDS = new HashSet<>(Arrays.asList(
            "appointmentStatusId",
            "code",
            "name",
            "isActive",
            "isDeleted",
            "createdAt",
            "updatedAt"
    ));

    public static final Set<String> SORTABLE = new HashSet<>(Arrays.asList(
            "code",
            "name",
            "createdAt",
            "updatedAt"
    ));

    public static final int DEFAULT_PAGE_SIZE = 20;

    private AppointmentStatusFilterConfig() {
        // Utility class
    }
}


