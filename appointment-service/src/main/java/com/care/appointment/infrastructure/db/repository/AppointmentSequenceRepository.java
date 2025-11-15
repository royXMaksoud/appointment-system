package com.care.appointment.infrastructure.db.repository;

import com.care.appointment.infrastructure.db.entities.AppointmentSequenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentSequenceRepository extends JpaRepository<AppointmentSequenceEntity, UUID> {

    /**
     * Find sequence record for a specific branch and year
     */
    Optional<AppointmentSequenceEntity> findByOrganizationBranchIdAndSequenceYear(
        UUID organizationBranchId,
        Integer sequenceYear
    );

    /**
     * Get the next sequence number for a branch in a specific year
     * This increments and returns the next available number
     */
    @Modifying
    @Query("""
        UPDATE AppointmentSequenceEntity a
        SET a.currentSequenceNumber = a.currentSequenceNumber + 1,
            a.totalAppointmentsCreated = COALESCE(a.totalAppointmentsCreated, 0) + 1
        WHERE a.organizationBranchId = :branchId
        AND a.sequenceYear = :year
        AND a.currentSequenceNumber < a.maxSequenceNumber
        """)
    void incrementSequence(@Param("branchId") UUID branchId, @Param("year") Integer year);

    /**
     * Get current sequence value for a branch and year
     */
    @Query("""
        SELECT a.currentSequenceNumber
        FROM AppointmentSequenceEntity a
        WHERE a.organizationBranchId = :branchId
        AND a.sequenceYear = :year
        """)
    Optional<Integer> getCurrentSequenceNumber(@Param("branchId") UUID branchId, @Param("year") Integer year);
}
