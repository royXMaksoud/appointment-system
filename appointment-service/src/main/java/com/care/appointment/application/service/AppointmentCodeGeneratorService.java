package com.care.appointment.application.service;

import com.care.appointment.infrastructure.db.entities.AppointmentSequenceEntity;
import com.care.appointment.infrastructure.db.repository.AppointmentSequenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.UUID;

/**
 * Service for generating unique appointment codes
 * Format: BRANCH_CODE-YEAR-SEQUENCE (e.g., HQ-2025-0001)
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentCodeGeneratorService {

    private final AppointmentSequenceRepository sequenceRepository;

    /**
     * Generate unique appointment code for a branch
     *
     * @param organizationBranchId UUID of the branch
     * @param branchCode Code of the branch (e.g., "HQ", "BR01")
     * @return Generated code in format: BRANCH_CODE-YEAR-SEQUENCE
     * @throws IllegalStateException if sequence limit is reached
     */
    @Transactional
    public String generateAppointmentCode(UUID organizationBranchId, String branchCode) {
        int currentYear = Year.now().getValue();

        // Find or create sequence record for this branch and year
        AppointmentSequenceEntity sequence = sequenceRepository
            .findByOrganizationBranchIdAndSequenceYear(organizationBranchId, currentYear)
            .orElseGet(() -> createNewSequence(organizationBranchId, branchCode, currentYear));

        // Check if we've exceeded the max sequence number
        if (sequence.getCurrentSequenceNumber() >= sequence.getMaxSequenceNumber()) {
            throw new IllegalStateException(
                String.format("Appointment sequence limit reached for branch %s in year %d",
                    organizationBranchId, currentYear)
            );
        }

        String code = sequence.getBranchCode();
        Integer nextSequence = sequence.getCurrentSequenceNumber();

        // Increment the sequence for next call
        sequenceRepository.incrementSequence(organizationBranchId, currentYear);

        // Format the code with zero-padded sequence number
        String appointmentCode = String.format("%s-%d-%04d", code, currentYear, nextSequence);

        log.info("Generated appointment code: {} for branch: {}", appointmentCode, organizationBranchId);

        return appointmentCode;
    }

    /**
     * Create a new sequence record for a branch and year
     */
    private AppointmentSequenceEntity createNewSequence(UUID organizationBranchId, String branchCode, Integer year) {
        AppointmentSequenceEntity sequence = AppointmentSequenceEntity.builder()
            .organizationBranchId(organizationBranchId)
            .branchCode(branchCode != null ? branchCode : "UNKNOWN")
            .sequenceYear(year)
            .currentSequenceNumber(1)
            .maxSequenceNumber(9999)
            .totalAppointmentsCreated(0)
            .build();

        log.info("Created new sequence record for branch: {} year: {}", organizationBranchId, year);

        return sequenceRepository.save(sequence);
    }

    /**
     * Get current sequence number without incrementing
     */
    public Integer getCurrentSequenceNumber(UUID organizationBranchId, Integer year) {
        return sequenceRepository.getCurrentSequenceNumber(organizationBranchId, year)
            .orElse(0);
    }

    /**
     * Get sequence statistics for a branch
     */
    public AppointmentSequenceEntity getSequenceStats(UUID organizationBranchId, Integer year) {
        return sequenceRepository.findByOrganizationBranchIdAndSequenceYear(organizationBranchId, year)
            .orElse(null);
    }
}
