package com.care.appointment.domain.model;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Domain model for Beneficiary (patient/service recipient)
 * 
 * New fields for mobile app support:
 * - dateOfBirth: For authentication (mobile + DOB)
 * - genderCodeValueId: Reference to CodeTable gender values
 * - profilePhotoUrl: User profile picture
 * - registrationStatusCodeValueId: QUICK or COMPLETE registration
 * - preferredLanguageCodeValueId: For multi-language notifications
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary {
    // EXISTING FIELDS
    private UUID beneficiaryId;
    private String nationalId;
    private String fullName;
    private String motherName;
    private String mobileNumber;
    private String email;
    private String address;
    private Double latitude;
    private Double longitude;

    // NEW FIELDS - Using CodeTable UUID references
    private LocalDate dateOfBirth;
    private UUID genderCodeValueId;              // → code_table_values (M/F)
    private String profilePhotoUrl;
    private UUID registrationStatusCodeValueId;  // → code_table_values (QUICK/COMPLETE)
    private Instant registrationCompletedAt;
    private UUID registrationCompletedByUserId;
    private UUID preferredLanguageCodeValueId;   // → code_table_values (AR/EN/TR/KU)

    // Mobile App Tracking & Notifications
    private Boolean hasInstalledMobileApp;       // Whether beneficiary has mobile app installed
    private Instant lastMobileAppSync;           // Last time beneficiary synced with mobile app
    private String deviceId;                     // Unique device identifier for push notifications
    private String preferredNotificationMethod;  // SMS, PUSH, or EMAIL
    private String verificationStatus;           // VERIFIED or UNVERIFIED
    private Integer totalActiveAppointments;     // Count of active appointments

    // AUDIT FIELDS
    private Boolean isActive;
    private Boolean isDeleted;
    private UUID createdById;
    private Instant createdAt;
    private Instant updatedAt;
    private Long rowVersion;
}

