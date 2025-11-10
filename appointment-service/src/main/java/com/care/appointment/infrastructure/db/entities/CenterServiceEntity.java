package com.care.appointment.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity mapping services to centers (which services each center provides)
 */
@Entity
@Table(
    name = "center_services",
    schema = "public",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_center_service",
        columnNames = {"organization_branch_id", "service_type_id"}
    ),
    indexes = {
        @Index(name = "ix_center_services_branch", columnList = "organization_branch_id"),
        @Index(name = "ix_center_services_type", columnList = "service_type_id"),
        @Index(name = "ix_center_services_active", columnList = "is_active")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class CenterServiceEntity {

    @Id
    @UuidGenerator
    @Column(name = "center_service_id", nullable = false, updatable = false)
    private UUID centerServiceId;

    /** References organization_branches.organization_branch_id from access-management-service */
    @Column(name = "organization_branch_id", nullable = false)
    private UUID organizationBranchId;

    @Column(name = "service_type_id", nullable = false)
    private UUID serviceTypeId;

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

