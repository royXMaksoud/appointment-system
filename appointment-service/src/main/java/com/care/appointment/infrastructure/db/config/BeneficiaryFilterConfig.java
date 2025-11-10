package com.care.appointment.infrastructure.db.config;

import java.util.Set;

public class BeneficiaryFilterConfig {
    
    public static final Set<String> ALLOWED_FIELDS = Set.of(
            "beneficiaryId",
            "nationalId",
            "fullName",
            "motherName",
            "mobileNumber",
            "email",
            "address",
            "latitude",
            "longitude",
            "isActive",
            "isDeleted",
            "createdById",
            "createdAt",
            "updatedAt"
    );

    public static final Set<String> SORTABLE = Set.of(
            "fullName",
            "nationalId",
            "mobileNumber",
            "email",
            "createdAt",
            "updatedAt"
    );

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    private BeneficiaryFilterConfig() {
        // Private constructor to prevent instantiation
    }
}

