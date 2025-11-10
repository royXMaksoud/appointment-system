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
 * Entity for service type translations
 */
@DynamicInsert @DynamicUpdate
@Entity
@Table(
    name = "service_type_lang",
    schema = "public",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_service_type_lang",
        columnNames = {"service_type_id", "language_code"}
    ),
    indexes = {
        @Index(name = "ix_service_type_lang_type", columnList = "service_type_id"),
        @Index(name = "ix_service_type_lang_language", columnList = "language_code")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class ServiceTypeLangEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "service_type_lang_id", nullable = false, updatable = false)
    private UUID serviceTypeLangId;

    @NotNull
    @Column(name = "service_type_id", nullable = false)
    private UUID serviceTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_type_id", insertable = false, updatable = false)
    private ServiceTypeEntity serviceType;

    @NotNull
    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode;

    @NotNull
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

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

