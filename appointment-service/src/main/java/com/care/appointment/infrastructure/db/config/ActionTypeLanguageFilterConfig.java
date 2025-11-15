package com.care.appointment.infrastructure.db.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ActionTypeLanguageFilterConfig {

    public static final Set<String> ALLOWED_FIELDS = new HashSet<>(Arrays.asList(
            "actionTypeLanguageId",
            "actionTypeId",
            "languageCode",
            "name",
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

    private ActionTypeLanguageFilterConfig() {
        // Utility class
    }
}


