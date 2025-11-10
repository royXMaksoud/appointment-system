package com.care.appointment.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity for tracking appointment status changes history
 */
@Entity
@Table(
    name = "appointment_status_history",
    schema = "public",
    indexes = {
        @Index(name = "ix_status_history_appointment", columnList = "appointment_id"),
        @Index(name = "ix_status_history_status", columnList = "appointment_status_id"),
        @Index(name = "ix_status_history_changed_at", columnList = "changed_at")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentStatusHistoryEntity {

    @Id
    @UuidGenerator
    @Column(name = "history_id", nullable = false, updatable = false)
    private UUID historyId;

    @Column(name = "appointment_id", nullable = false)
    private UUID appointmentId;

    @Column(name = "appointment_status_id", nullable = false)
    private UUID appointmentStatusId;

    @Column(name = "changed_by_user_id")
    private UUID changedByUserId;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @CreationTimestamp
    @Column(name = "changed_at", updatable = false, nullable = false)
    private Instant changedAt;
}

