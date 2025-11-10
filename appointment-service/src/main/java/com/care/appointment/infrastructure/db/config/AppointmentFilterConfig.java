package com.care.appointment.infrastructure.db.config;

import java.util.Set;

public class AppointmentFilterConfig {
    
    public static final Set<String> ALLOWED_FIELDS = Set.of(
            "appointmentId",
            "appointmentRequestId",
            "beneficiaryId",
            "organizationBranchId",
            "serviceTypeId",
            "appointmentDate",
            "appointmentTime",
            "appointmentStatusId",
            "priority",
            "actionTypeId",
            "attendedAt",
            "completedAt",
            "cancelledAt",
            "createdById",
            "createdAt",
            "updatedAt"
    );

    public static final Set<String> SORTABLE = Set.of(
            "appointmentDate",
            "appointmentTime",
            "priority",
            "createdAt",
            "updatedAt",
            "attendedAt",
            "completedAt"
    );

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    private AppointmentFilterConfig() {
        // Private constructor to prevent instantiation
    }
}

