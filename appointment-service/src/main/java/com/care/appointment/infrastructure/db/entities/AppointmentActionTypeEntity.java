package com.care.appointment.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing appointment action/outcome types (result of appointment)
 */
@Entity
@Table(
    name = "appointment_action_types",
    schema = "public",
    indexes = {
        @Index(name = "ux_appt_action_types_code", columnList = "code", unique = true),
        @Index(name = "ix_appt_action_types_active", columnList = "is_active"),
        @Index(name = "ix_appt_action_types_deleted", columnList = "is_deleted")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentActionTypeEntity {

    @Id
    @UuidGenerator
    @Column(name = "action_type_id", nullable = false, updatable = false)
    private UUID actionTypeId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "requires_transfer", nullable = false)
    private Boolean requiresTransfer;

    @Column(name = "completes_appointment", nullable = false)
    private Boolean completesAppointment;

    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "display_order")
    private Integer displayOrder;

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
    private Integer rowVersion;

    @PrePersist
    void prePersist() {
        if (isActive == null) isActive = Boolean.TRUE;
        if (isDeleted == null) isDeleted = Boolean.FALSE;
        if (requiresTransfer == null) requiresTransfer = Boolean.FALSE;
        if (completesAppointment == null) completesAppointment = Boolean.FALSE;
    }
}

