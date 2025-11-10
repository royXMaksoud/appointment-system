package com.care.appointment.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing appointment statuses (REQUESTED, CONFIRMED, COMPLETED, CANCELLED, etc.)
 */
@Entity
@Table(
    name = "appointment_statuses",
    schema = "public",
    indexes = {
        @Index(name = "ux_appt_statuses_code", columnList = "code", unique = true),
        @Index(name = "ix_appt_statuses_active", columnList = "is_active")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentStatusEntity {

    @Id
    @UuidGenerator
    @Column(name = "appointment_status_id", nullable = false, updatable = false)
    private UUID appointmentStatusId;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

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
        if (isActive == null) isActive = Boolean.TRUE;
    }
}

