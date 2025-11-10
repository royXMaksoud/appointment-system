package com.care.appointment.infrastructure.db.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity for appointment status translations
 */
@DynamicInsert @DynamicUpdate
@Entity
@Table(
    name = "appointment_status_lang",
    schema = "public",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_appointment_status_lang",
        columnNames = {"appointment_status_id", "language_code"}
    ),
    indexes = {
        @Index(name = "ix_appt_status_lang_status", columnList = "appointment_status_id"),
        @Index(name = "ix_appt_status_lang_language", columnList = "language_code")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentStatusLangEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "appointment_status_lang_id", nullable = false, updatable = false)
    private UUID appointmentStatusLangId;

    @NotNull
    @Column(name = "appointment_status_id", nullable = false)
    private UUID appointmentStatusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_status_id", insertable = false, updatable = false)
    private AppointmentStatusEntity appointmentStatus;

    @NotNull
    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode;

    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

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
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "row_version")
    private Long rowVersion;

    @PrePersist
    void prePersist() {
        if (isActive == null) isActive = Boolean.TRUE;
        if (isDeleted == null) isDeleted = Boolean.FALSE;
    }
}

