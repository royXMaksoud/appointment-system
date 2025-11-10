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
 * Entity representing center holidays and off days
 */
@Entity
@Table(
    name = "center_holidays",
    schema = "public",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_center_holiday",
        columnNames = {"organization_branch_id", "holiday_date"}
    ),
    indexes = {
        @Index(name = "ix_holidays_branch", columnList = "organization_branch_id"),
        @Index(name = "ix_holidays_date", columnList = "holiday_date"),
        @Index(name = "ix_holidays_active", columnList = "is_active"),
        @Index(name = "ix_holidays_deleted", columnList = "is_deleted")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class CenterHolidayEntity {

    @Id
    @UuidGenerator
    @Column(name = "holiday_id", nullable = false, updatable = false)
    private UUID holidayId;

    @Column(name = "organization_branch_id", nullable = false)
    private UUID organizationBranchId;

    @Column(name = "holiday_date", nullable = false)
    private LocalDate holidayDate;

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "is_recurring_yearly", nullable = false)
    private Boolean isRecurringYearly;

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
        if (isRecurringYearly == null) isRecurringYearly = Boolean.FALSE;
        if (isActive == null) isActive = Boolean.TRUE;
        if (isDeleted == null) isDeleted = Boolean.FALSE;
    }
}

