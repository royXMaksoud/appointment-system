package com.care.appointment.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity representing beneficiaries (patients/service recipients)
 */
@Entity
@Table(
    name = "beneficiaries",
    schema = "public",
    indexes = {
        @Index(name = "ux_appt_beneficiaries_national_id", columnList = "national_id", unique = true),
        @Index(name = "ix_appt_beneficiaries_mobile", columnList = "mobile_number"),
        @Index(name = "ix_appt_beneficiaries_email", columnList = "email"),
        @Index(name = "ix_appt_beneficiaries_active", columnList = "is_active"),
        @Index(name = "ix_appt_beneficiaries_deleted", columnList = "is_deleted"),
        @Index(name = "ix_appt_beneficiaries_mobile_dob", columnList = "mobile_number, date_of_birth"),
        @Index(name = "ix_appt_beneficiaries_gender", columnList = "gender_code_value_id"),
        @Index(name = "ix_appt_beneficiaries_reg_status", columnList = "registration_status_code_value_id"),
        @Index(name = "ix_appt_beneficiaries_pref_lang", columnList = "preferred_language_code_value_id"),
        @Index(name = "ix_appt_beneficiaries_mobile_app", columnList = "has_installed_mobile_app"),
        @Index(name = "ix_appt_beneficiaries_device", columnList = "device_id"),
        @Index(name = "ix_appt_beneficiaries_verification", columnList = "verification_status"),
        @Index(name = "ix_appt_beneficiaries_active_appointments", columnList = "total_active_appointments")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class BeneficiaryEntity {

    @Id
    @UuidGenerator
    @Column(name = "beneficiary_id", nullable = false, updatable = false)
    private UUID beneficiaryId;

    @Column(name = "national_id", unique = true, length = 50)
    private String nationalId;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "mother_name", length = 200)
    private String motherName;

    /** Mobile number in E.164 format (e.g., +963912345678) */
    @Column(name = "mobile_number", nullable = false, length = 20)
    private String mobileNumber;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // NEW FIELDS - Mobile app support with CodeTable references
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender_code_value_id")
    private UUID genderCodeValueId;

    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;

    @Column(name = "registration_status_code_value_id")
    private UUID registrationStatusCodeValueId;

    @Column(name = "registration_completed_at")
    private Instant registrationCompletedAt;

    @Column(name = "registration_completed_by_user_id")
    private UUID registrationCompletedByUserId;

    @Column(name = "preferred_language_code_value_id")
    private UUID preferredLanguageCodeValueId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "created_by_user_id")
    private UUID createdById;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "updated_by_user_id")
    private UUID updatedById;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(name = "row_version")
    private Long rowVersion;

    // Mobile App Tracking & Notifications
    @Column(name = "has_installed_mobile_app")
    private Boolean hasInstalledMobileApp;

    @Column(name = "last_mobile_app_sync")
    private Instant lastMobileAppSync;

    @Column(name = "device_id", length = 500)
    private String deviceId;

    @Column(name = "preferred_notification_method", length = 20)
    private String preferredNotificationMethod; // SMS, PUSH, EMAIL

    @Column(name = "verification_status", length = 20)
    private String verificationStatus; // VERIFIED, UNVERIFIED

    @Column(name = "total_active_appointments")
    private Integer totalActiveAppointments;

    @PrePersist
    void prePersist() {
        if (isActive == null) isActive = Boolean.TRUE;
        if (isDeleted == null) isDeleted = Boolean.FALSE;
        if (hasInstalledMobileApp == null) hasInstalledMobileApp = Boolean.FALSE;
        if (verificationStatus == null) verificationStatus = "UNVERIFIED";
        if (totalActiveAppointments == null) totalActiveAppointments = 0;
    }
}

