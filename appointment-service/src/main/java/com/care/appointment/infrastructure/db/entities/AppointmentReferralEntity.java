package com.care.appointment.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing appointment referrals
 */
@Entity
@Table(
    name = "appointment_referrals",
    schema = "public",
    indexes = {
        @Index(name = "ix_referrals_appointment", columnList = "appointment_id"),
        @Index(name = "ix_referrals_beneficiary", columnList = "beneficiary_id"),
        @Index(name = "ix_referrals_referred_to_appt", columnList = "referred_to_appointment_id"),
        @Index(name = "ix_referrals_status", columnList = "status"),
        @Index(name = "ix_referrals_date", columnList = "referral_date")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentReferralEntity {

    @Id
    @UuidGenerator
    @Column(name = "referral_id", nullable = false, updatable = false)
    private UUID referralId;

    @Column(name = "appointment_id", nullable = false)
    private UUID appointmentId;

    @Column(name = "beneficiary_id", nullable = false)
    private UUID beneficiaryId;

    @Column(name = "referred_to_appointment_id")
    private UUID referredToAppointmentId;

    @Column(name = "referred_to_service_type_id", nullable = false)
    private UUID referredToServiceTypeId;

    @Column(name = "referral_type", nullable = false, length = 50)
    private String referralType;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "clinical_notes", columnDefinition = "TEXT")
    private String clinicalNotes;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "referral_date", nullable = false)
    private Instant referralDate;

    @Column(name = "referred_appointment_date")
    private Instant referredAppointmentDate;

    @Column(name = "is_urgent", nullable = false)
    private Boolean isUrgent;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

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

    @PrePersist
    void prePersist() {
        if (status == null) status = "PENDING";
        if (isUrgent == null) isUrgent = Boolean.FALSE;
        if (referralDate == null) referralDate = Instant.now();
    }
}

