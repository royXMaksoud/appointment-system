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
 * Entity representing daily capacity for centers (can override weekly schedule)
 */
@Entity
@Table(
    name = "center_daily_capacity",
    schema = "public",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_center_daily_capacity",
        columnNames = {"organization_branch_id", "capacity_date", "service_type_id"}
    ),
    indexes = {
        @Index(name = "ix_daily_capacity_branch", columnList = "organization_branch_id"),
        @Index(name = "ix_daily_capacity_date", columnList = "capacity_date"),
        @Index(name = "ix_daily_capacity_service", columnList = "service_type_id")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class CenterDailyCapacityEntity {

    @Id
    @UuidGenerator
    @Column(name = "capacity_id", nullable = false, updatable = false)
    private UUID capacityId;

    @Column(name = "organization_branch_id", nullable = false)
    private UUID organizationBranchId;

    /** NULL means capacity for all services */
    @Column(name = "service_type_id")
    private UUID serviceTypeId;

    @Column(name = "capacity_date", nullable = false)
    private LocalDate capacityDate;

    @Column(name = "total_slots", nullable = false)
    private Integer totalSlots;

    @Column(name = "available_slots", nullable = false)
    private Integer availableSlots;

    /** If true, overrides weekly schedule for this date */
    @Column(name = "is_override", nullable = false)
    private Boolean isOverride;

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
        if (isOverride == null) isOverride = Boolean.FALSE;
    }
}

