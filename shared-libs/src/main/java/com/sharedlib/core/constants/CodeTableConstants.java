package com.sharedlib.core.constants;

import java.util.UUID;

/**
 * Constants for code table IDs to avoid hardcoding UUIDs throughout the application.
 * These IDs should match the actual UUIDs in the reference-data-service database.
 * 
 * This class is shared across all services to maintain consistency.
 * 
 * @author CARE Team
 * @version 1.0
 * @since 2025-08-05
 */
public class CodeTableConstants {

    // Appointment Type table ID
    public static final UUID APPOINTMENT_TYPE_TABLE_ID = UUID.fromString("6e67ff62-820b-402b-a2b6-d073d13728ba");
    

    // Appointment Type table ID
    public static final UUID ALL_CURRENCY_TYPE_TABLE_ID = UUID.fromString("6e67ff62-820b-402b-a2b6-d073d13728ba");
    


    // Gender table ID
    public static final UUID GENDER_TABLE_ID = UUID.fromString("12345678-1234-1234-1234-123456789012"); // Replace with actual UUID
    
    // Language table ID
    public static final UUID LANGUAGE_TABLE_ID = UUID.fromString("87654321-4321-4321-4321-210987654321"); // Replace with actual UUID
    
    // Status table ID
    public static final UUID STATUS_TABLE_ID = UUID.fromString("11111111-2222-3333-4444-555555555555"); // Replace with actual UUID

    private CodeTableConstants() {
        // Utility class - prevent instantiation
    }
} 