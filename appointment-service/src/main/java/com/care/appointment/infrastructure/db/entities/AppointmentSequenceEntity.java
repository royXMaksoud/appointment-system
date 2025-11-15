package com.care.appointment.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.Year;
import java.util.UUID;

/**
 * Entity for tracking appointment code sequences per branch and year
 * Format: BRANCH_CODE-YEAR-SEQUENCE (e.g., HQ-2025-0001)
 *
 * Each branch has its own sequence that resets annually.
 */
@Entity
@Table(
    name = "appointment_sequences",
    schema = "public",
    uniqueConstraints = {
        @UniqueConstraint(name = "uc_appt_seq_branch_year", columnNames = {"organization_branch_id", "sequence_year"})
    },
    indexes = {
        @Index(name = "ix_appt_seq_branch", columnList = "organization_branch_id"),
        @Index(name = "ix_appt_seq_year", columnList = "sequence_year"),
        @Index(name = "ix_appt_seq_branch_year", columnList = "organization_branch_id, sequence_year")
    }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentSequenceEntity {

    @Id
    @UuidGenerator
    @Column(name = "sequence_id", nullable = false, updatable = false)
    private UUID sequenceId;

    /** Reference to the organization branch */
    @Column(name = "organization_branch_id", nullable = false, updatable = false)
    private UUID organizationBranchId;

    /** Branch code (e.g., "HQ", "BR01", "DAMASCUS-CENTER") */
    @Column(name = "branch_code", nullable = false, length = 50, updatable = false)
    private String branchCode;

    /** Year for this sequence (e.g., 2025) */
    @Column(name = "sequence_year", nullable = false, updatable = false)
    private Integer sequenceYear;

    /** Current sequence number (incremented for each appointment) */
    @Column(name = "current_sequence_number", nullable = false)
    private Integer currentSequenceNumber;

    /** Maximum sequence number before reset (usually 9999) */
    @Column(name = "max_sequence_number", nullable = false)
    private Integer maxSequenceNumber;

    /** Total appointments created this year for this branch */
    @Column(name = "total_appointments_created")
    private Integer totalAppointmentsCreated;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        if (currentSequenceNumber == null) currentSequenceNumber = 1;
        if (maxSequenceNumber == null) maxSequenceNumber = 9999;
        if (totalAppointmentsCreated == null) totalAppointmentsCreated = 0;
        if (sequenceYear == null) sequenceYear = Year.now().getValue();
    }
}
