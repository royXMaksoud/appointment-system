package com.care.appointment.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing appointment transfers between centers
 */
@Entity
@Table(
    name = "appointment_transfers",
    schema = "public",
    indexes = {
        @Index(name = "ix_transfers_appointment", columnList = "appointment_id"),
        @Index(name = "ix_transfers_from_branch", columnList = "from_organization_branch_id"),
        @Index(name = "ix_transfers_to_branch", columnList = "to_organization_branch_id"),
        @Index(name = "ix_transfers_status", columnList = "status"),
        @Index(name = "ix_transfers_transferred_at", columnList = "transferred_at")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentTransferEntity {

    @Id
    @UuidGenerator
    @Column(name = "transfer_id", nullable = false, updatable = false)
    private UUID transferId;

    @Column(name = "appointment_id", nullable = false)
    private UUID appointmentId;

    @Column(name = "from_organization_branch_id", nullable = false)
    private UUID fromOrganizationBranchId;

    @Column(name = "to_organization_branch_id", nullable = false)
    private UUID toOrganizationBranchId;

    /** The new appointment created at the target center after acceptance */
    @Column(name = "new_appointment_id")
    private UUID newAppointmentId;

    @Column(name = "transfer_reason", columnDefinition = "TEXT")
    private String transferReason;

    @Column(name = "transferred_by_user_id")
    private UUID transferredByUserId;

    @CreationTimestamp
    @Column(name = "transferred_at", updatable = false, nullable = false)
    private Instant transferredAt;

    /** PENDING, ACCEPTED, REJECTED */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "response_notes", columnDefinition = "TEXT")
    private String responseNotes;

    @Column(name = "responded_by_user_id")
    private UUID respondedByUserId;

    @Column(name = "responded_at")
    private Instant respondedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(name = "row_version")
    private Long rowVersion;

    @PrePersist
    void prePersist() {
        if (status == null) status = "PENDING";
    }
}

