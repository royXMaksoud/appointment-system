package com.care.appointment.infrastructure.db.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ServiceTypeLanguageFilterConfig {

    public static final Set<String> ALLOWED_FIELDS = new HashSet<>(Arrays.asList(
            "serviceTypeLanguageId",
            "serviceTypeId",
            "languageCode",
            "name",
            "description",
            "isActive",
            "isDeleted",
            "createdAt",
            "updatedAt"
    ));

    public static final Set<String> SORTABLE = new HashSet<>(Arrays.asList(
            "languageCode",
            "name",
            "createdAt",
            "updatedAt"
    ));

    public static final int DEFAULT_PAGE_SIZE = 20;

    private ServiceTypeLanguageFilterConfig() {
        // Utility class
    }
}


