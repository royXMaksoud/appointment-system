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
 * Entity for appointment action type translations
 */
@DynamicInsert @DynamicUpdate
@Entity
@Table(
    name = "appointment_action_type_lang",
    schema = "public",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_action_type_lang",
        columnNames = {"action_type_id", "language_code"}
    ),
    indexes = {
        @Index(name = "ix_action_type_lang_type", columnList = "action_type_id"),
        @Index(name = "ix_action_type_lang_language", columnList = "language_code")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentActionTypeLangEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "action_type_lang_id", nullable = false, updatable = false)
    private UUID actionTypeLangId;

    @NotNull
    @Column(name = "action_type_id", nullable = false)
    private UUID actionTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_type_id", insertable = false, updatable = false)
    private AppointmentActionTypeEntity actionType;

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

