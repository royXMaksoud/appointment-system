package com.care.appointment.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing service types (hierarchical: general and detailed services)
 */
@Entity
@Table(
    name = "service_types",
    schema = "public",
    indexes = {
        @Index(name = "ux_appt_service_types_code", columnList = "code", unique = true),
        @Index(name = "ix_appt_service_types_parent", columnList = "parent_service_type_id"),
        @Index(name = "ix_appt_service_types_active", columnList = "is_active"),
        @Index(name = "ix_appt_service_types_deleted", columnList = "is_deleted")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class ServiceTypeEntity {

    @Id
    @UuidGenerator
    @Column(name = "service_type_id", nullable = false, updatable = false)
    private UUID serviceTypeId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    /** NULL for general services, references parent for detailed/sub services */
    @Column(name = "parent_service_type_id")
    private UUID parentServiceTypeId;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "is_leaf", nullable = false)
    private Boolean isLeaf;

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
        if (isLeaf == null) isLeaf = Boolean.FALSE;
    }
}

