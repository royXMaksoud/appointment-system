package com.care.appointment.infrastructure.db.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ActionTypeFilterConfig {
    
    public static final Set<String> ALLOWED_FIELDS = new HashSet<>(Arrays.asList(
            "actionTypeId",
            "name",
            "code",
            "description",
            "isActive",
            "isDeleted",
            "requiresTransfer",
            "completesAppointment",
            "displayOrder",
            "createdAt",
            "updatedAt"
    ));
    
    public static final Set<String> SORTABLE = new HashSet<>(Arrays.asList(
            "name",
            "code",
            "displayOrder",
            "createdAt",
            "updatedAt"
    ));
    
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    private ActionTypeFilterConfig() {
        // Utility class
    }
}

