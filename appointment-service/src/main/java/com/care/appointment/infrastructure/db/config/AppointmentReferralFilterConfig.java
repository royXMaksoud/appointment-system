package com.care.appointment.infrastructure.db.config;

import java.util.Set;

public final class AppointmentReferralFilterConfig {

    private AppointmentReferralFilterConfig() {
    }

    public static final Set<String> ALLOWED_FIELDS = Set.of(
            "referralId",
            "appointmentId",
            "beneficiaryId",
            "referredToAppointmentId",
            "referredToServiceTypeId",
            "referralType",
            "status",
            "isUrgent",
            "referralDate",
            "referredAppointmentDate",
            "createdById"
    );

    public static final Set<String> SORTABLE = Set.of(
            "referralDate",
            "referredAppointmentDate",
            "status"
    );

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
}

