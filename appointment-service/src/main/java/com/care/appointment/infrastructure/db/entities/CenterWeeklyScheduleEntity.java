package com.care.appointment.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Entity representing weekly schedule for centers
 */
@Entity
@Table(
    name = "center_weekly_schedule",
    schema = "public",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_center_weekly_schedule",
        columnNames = {"organization_branch_id", "day_of_week"}
    ),
    indexes = {
        @Index(name = "ix_weekly_schedule_branch", columnList = "organization_branch_id"),
        @Index(name = "ix_weekly_schedule_day", columnList = "day_of_week"),
        @Index(name = "ix_weekly_schedule_active", columnList = "is_active"),
        @Index(name = "ix_weekly_schedule_deleted", columnList = "is_deleted")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class CenterWeeklyScheduleEntity {

    @Id
    @UuidGenerator
    @Column(name = "schedule_id", nullable = false, updatable = false)
    private UUID scheduleId;

    @Column(name = "organization_branch_id", nullable = false)
    private UUID organizationBranchId;

    /** 0=Sunday, 1=Monday, ..., 6=Saturday */
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "slot_duration_minutes", nullable = false)
    private Integer slotDurationMinutes;

    @Column(name = "max_capacity_per_slot")
    private Integer maxCapacityPerSlot;

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
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(name = "row_version")
    private Integer rowVersion;

    @PrePersist
    void prePersist() {
        if (isActive == null) isActive = Boolean.TRUE;
        if (isDeleted == null) isDeleted = Boolean.FALSE;
        if (slotDurationMinutes == null) slotDurationMinutes = 30;
        if (maxCapacityPerSlot == null) maxCapacityPerSlot = 10;
    }
}

