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
 * Entity representing appointment requests from mobile app
 */
@Entity
@Table(
    name = "appointment_requests",
    schema = "public",
    indexes = {
        @Index(name = "ix_appt_requests_beneficiary", columnList = "beneficiary_id"),
        @Index(name = "ix_appt_requests_service_type", columnList = "service_type_id"),
        @Index(name = "ix_appt_requests_status", columnList = "status"),
        @Index(name = "ix_appt_requests_priority", columnList = "priority"),
        @Index(name = "ix_appt_requests_created", columnList = "created_at"),
        @Index(name = "ix_appt_requests_preferred_date", columnList = "preferred_date")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentRequestEntity {

    @Id
    @UuidGenerator
    @Column(name = "appointment_request_id", nullable = false, updatable = false)
    private UUID appointmentRequestId;

    @Column(name = "beneficiary_id", nullable = false)
    private UUID beneficiaryId;

    @Column(name = "service_type_id", nullable = false)
    private UUID serviceTypeId;

    @Column(name = "preferred_date")
    private LocalDate preferredDate;

    /** URGENT or NORMAL */
    @Column(name = "priority", nullable = false, length = 20)
    private String priority;

    /** NEAREST_CENTER or EARLIEST_DATE */
    @Column(name = "preference_type", nullable = false, length = 30)
    private String preferenceType;

    @Column(name = "location_latitude")
    private Double locationLatitude;

    @Column(name = "location_longitude")
    private Double locationLongitude;

    @Column(name = "mobile_number", nullable = false, length = 20)
    private String mobileNumber;

    /** PENDING, APPROVED, REJECTED, CANCELLED */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
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
        if (priority == null) priority = "NORMAL";
        if (preferenceType == null) preferenceType = "NEAREST_CENTER";
        if (status == null) status = "PENDING";
    }
}

