package com.care.appointment.infrastructure.db.config;

import java.util.Set;

public final class AppointmentStatusHistoryFilterConfig {

    private AppointmentStatusHistoryFilterConfig() {
    }

    public static final Set<String> ALLOWED_FIELDS = Set.of(
            "historyId",
            "appointmentId",
            "appointmentStatusId",
            "changedByUserId",
            "reason",
            "changedAt"
    );

    public static final Set<String> SORTABLE = Set.of(
            "changedAt"
    );

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
}

