package com.care.appointment.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Entity representing appointments (main table)
 */
@Entity
@Table(
    name = "appointments",
    schema = "public",
    indexes = {
        @Index(name = "ix_appointments_request", columnList = "appointment_request_id"),
        @Index(name = "ix_appointments_beneficiary", columnList = "beneficiary_id"),
        @Index(name = "ix_appointments_branch", columnList = "organization_branch_id"),
        @Index(name = "ix_appointments_service_type", columnList = "service_type_id"),
        @Index(name = "ix_appointments_status", columnList = "appointment_status_id"),
        @Index(name = "ix_appointments_date", columnList = "appointment_date"),
        @Index(name = "ix_appointments_date_time", columnList = "appointment_date, appointment_time"),
        @Index(name = "ix_appointments_branch_date", columnList = "organization_branch_id, appointment_date"),
        @Index(name = "ix_appointments_priority", columnList = "priority"),
        @Index(name = "ix_appointments_created_by", columnList = "created_by_user_id")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentEntity {

    @Id
    @UuidGenerator
    @Column(name = "appointment_id", nullable = false, updatable = false)
    private UUID appointmentId;

    /** NULL if direct booking, otherwise references the request */
    @Column(name = "appointment_request_id")
    private UUID appointmentRequestId;

    @Column(name = "beneficiary_id", nullable = false)
    private UUID beneficiaryId;

    @Column(name = "organization_branch_id", nullable = false)
    private UUID organizationBranchId;

    @Column(name = "service_type_id", nullable = false)
    private UUID serviceTypeId;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    @Column(name = "slot_duration_minutes", nullable = false)
    private Integer slotDurationMinutes;

    @Column(name = "appointment_status_id", nullable = false)
    private UUID appointmentStatusId;

    /** URGENT or NORMAL */
    @Column(name = "priority", nullable = false, length = 20)
    private String priority;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /** Result/outcome of the appointment */
    @Column(name = "action_type_id")
    private UUID actionTypeId;

    @Column(name = "action_notes", columnDefinition = "TEXT")
    private String actionNotes;

    @Column(name = "attended_at")
    private Instant attendedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

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
        if (slotDurationMinutes == null) slotDurationMinutes = 30;
    }
}

