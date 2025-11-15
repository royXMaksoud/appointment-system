package com.care.appointment.application.beneficiary.service;

import com.care.appointment.application.beneficiary.command.CreateBeneficiaryCommand;
import com.care.appointment.application.beneficiary.command.UpdateBeneficiaryCommand;
import com.care.appointment.application.beneficiary.command.BulkBeneficiaryUpdateCommand;
import com.care.appointment.domain.model.Beneficiary;
import com.care.appointment.domain.ports.in.beneficiary.*;
import com.care.appointment.domain.ports.out.beneficiary.BeneficiaryCrudPort;
import com.care.appointment.domain.ports.out.beneficiary.BeneficiarySearchPort;
import com.sharedlib.core.filter.FilterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BeneficiaryAdminService implements SaveUseCase, UpdateUseCase, LoadUseCase, DeleteUseCase, LoadAllUseCase {

    private final BeneficiaryCrudPort beneficiaryCrudPort;
    private final BeneficiarySearchPort beneficiarySearchPort;

    @Override
    public Beneficiary saveBeneficiary(CreateBeneficiaryCommand command) {
        log.info("Creating new beneficiary with national ID: {}", command.getNationalId());

        String normalizedNationalId = normalize(command.getNationalId());
        String normalizedMobile = normalize(command.getMobileNumber());
        String normalizedAddress = normalize(command.getAddress());

        command.setNationalId(normalizedNationalId);
        command.setMobileNumber(normalizedMobile);
        command.setAddress(normalizedAddress);

        // Validate no duplicate national ID
        if (StringUtils.hasText(normalizedNationalId) && beneficiarySearchPort.existsByNationalId(normalizedNationalId)) {
            throw new IllegalArgumentException("Beneficiary already exists with national ID: " + normalizedNationalId);
        }

        // Validate no duplicate mobile number
        if (beneficiarySearchPort.existsByMobileNumber(normalizedMobile)) {
            throw new IllegalArgumentException("Beneficiary already exists with mobile number: " + normalizedMobile);
        }

        Beneficiary beneficiary = Beneficiary.builder()
                .nationalId(normalizedNationalId)
                .fullName(command.getFullName())
                .motherName(command.getMotherName())
                .mobileNumber(normalizedMobile)
                .email(command.getEmail())
                .address(normalizedAddress)
                .latitude(command.getLatitude())
                .longitude(command.getLongitude())
                .dateOfBirth(command.getDateOfBirth())
                .genderCodeValueId(command.getGenderCodeValueId())
                .profilePhotoUrl(command.getProfilePhotoUrl())
                .registrationStatusCodeValueId(command.getRegistrationStatusCodeValueId())
                .preferredLanguageCodeValueId(command.getPreferredLanguageCodeValueId())
                .isActive(command.getIsActive() != null ? command.getIsActive() : true)
                .isDeleted(false)
                .createdById(command.getCreatedById())
                .build();

        Beneficiary saved = beneficiaryCrudPort.save(beneficiary);
        log.info("Beneficiary created successfully with ID: {}", saved.getBeneficiaryId());
        return saved;
    }

    @Override
    public Beneficiary updateBeneficiary(UpdateBeneficiaryCommand command) {
        log.info("Updating beneficiary: {}", command.getBeneficiaryId());

        Beneficiary existing = beneficiaryCrudPort.findById(command.getBeneficiaryId())
                .orElseThrow(() -> new IllegalArgumentException("Beneficiary not found with ID: " + command.getBeneficiaryId()));

        if (existing.getIsDeleted()) {
            throw new IllegalArgumentException("Cannot update deleted beneficiary");
        }

        String normalizedNationalId = normalize(command.getNationalId());
        String normalizedMobile = normalize(command.getMobileNumber());
        String normalizedAddress = normalize(command.getAddress());

        if (StringUtils.hasText(normalizedNationalId)) {
            beneficiarySearchPort.findByNationalId(normalizedNationalId)
                    .filter(dup -> !dup.getBeneficiaryId().equals(existing.getBeneficiaryId()))
                    .ifPresent(dup -> {
                        throw new IllegalArgumentException("Another beneficiary already uses national ID: " + normalizedNationalId);
                    });
        }

        beneficiarySearchPort.findByMobileNumber(normalizedMobile)
                .filter(dup -> !dup.getBeneficiaryId().equals(existing.getBeneficiaryId()))
                .ifPresent(dup -> {
                    throw new IllegalArgumentException("Another beneficiary already uses mobile number: " + normalizedMobile);
                });

        existing.setNationalId(normalizedNationalId);
        existing.setFullName(command.getFullName());
        existing.setMotherName(command.getMotherName());
        existing.setMobileNumber(normalizedMobile);
        existing.setEmail(command.getEmail());
        existing.setAddress(normalizedAddress);
        existing.setLatitude(command.getLatitude());
        existing.setLongitude(command.getLongitude());
        existing.setDateOfBirth(command.getDateOfBirth());
        existing.setGenderCodeValueId(command.getGenderCodeValueId());
        if (command.getProfilePhotoUrl() != null) existing.setProfilePhotoUrl(command.getProfilePhotoUrl());
        if (command.getRegistrationStatusCodeValueId() != null) existing.setRegistrationStatusCodeValueId(command.getRegistrationStatusCodeValueId());
        if (command.getPreferredLanguageCodeValueId() != null) existing.setPreferredLanguageCodeValueId(command.getPreferredLanguageCodeValueId());
        if (command.getIsActive() != null) {
            existing.setIsActive(command.getIsActive());
        }

        Beneficiary updated = beneficiaryCrudPort.update(existing);
        log.info("Beneficiary updated successfully: {}", updated.getBeneficiaryId());
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Beneficiary> getBeneficiaryById(UUID beneficiaryId) {
        log.debug("Loading beneficiary by ID: {}", beneficiaryId);
        return beneficiaryCrudPort.findById(beneficiaryId);
    }

    public Beneficiary updateProfilePhoto(UUID beneficiaryId, String storagePath) {
        log.info("Updating profile photo for beneficiary: {}", beneficiaryId);
        Beneficiary existing = beneficiaryCrudPort.findById(beneficiaryId)
                .orElseThrow(() -> new IllegalArgumentException("Beneficiary not found with ID: " + beneficiaryId));

        existing.setProfilePhotoUrl(storagePath);
        Beneficiary updated = beneficiaryCrudPort.update(existing);
        log.info("Profile photo updated for beneficiary: {}", beneficiaryId);
        return updated;
    }

    @Override
    public void deleteBeneficiary(UUID beneficiaryId) {
        log.info("Deleting beneficiary: {}", beneficiaryId);
        
        Beneficiary beneficiary = beneficiaryCrudPort.findById(beneficiaryId)
                .orElseThrow(() -> new IllegalArgumentException("Beneficiary not found with ID: " + beneficiaryId));

        beneficiary.setIsDeleted(true);
        beneficiary.setIsActive(false);
        beneficiaryCrudPort.update(beneficiary);
        
        log.info("Beneficiary deleted successfully (soft delete): {}", beneficiaryId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Beneficiary> loadAll(FilterRequest filter, Pageable pageable) {
        log.debug("Loading all beneficiaries with filter and pagination");
        return beneficiarySearchPort.search(filter, pageable);
    }
    
    /**
     * Bulk update multiple beneficiaries
     * Used for updating language preferences, registration status, etc. in bulk
     */
    @Transactional
    public List<Beneficiary> bulkUpdateBeneficiaries(BulkBeneficiaryUpdateCommand command) {
        log.info("Bulk updating {} beneficiaries: {}", command.getBeneficiaryIds().size(), command.getDescription());
        
        List<Beneficiary> updated = command.getBeneficiaryIds().stream()
                .map(beneficiaryId -> {
                    try {
                        Beneficiary beneficiary = beneficiaryCrudPort.findById(beneficiaryId)
                                .orElseThrow(() -> new IllegalArgumentException("Beneficiary not found: " + beneficiaryId));
                        
                        if (beneficiary.getIsDeleted()) {
                            log.warn("Skipping deleted beneficiary: {}", beneficiaryId);
                            return null;
                        }
                        
                        // Update fields from map
                        if (command.getUpdateFields().containsKey("preferredLanguageCodeValueId")) {
                            beneficiary.setPreferredLanguageCodeValueId((UUID) command.getUpdateFields().get("preferredLanguageCodeValueId"));
                        }
                        if (command.getUpdateFields().containsKey("genderCodeValueId")) {
                            beneficiary.setGenderCodeValueId((UUID) command.getUpdateFields().get("genderCodeValueId"));
                        }
                        if (command.getUpdateFields().containsKey("registrationStatusCodeValueId")) {
                            beneficiary.setRegistrationStatusCodeValueId((UUID) command.getUpdateFields().get("registrationStatusCodeValueId"));
                        }
                        if (command.getUpdateFields().containsKey("profilePhotoUrl")) {
                            beneficiary.setProfilePhotoUrl((String) command.getUpdateFields().get("profilePhotoUrl"));
                        }
                        if (command.getUpdateFields().containsKey("isActive")) {
                            beneficiary.setIsActive((Boolean) command.getUpdateFields().get("isActive"));
                        }
                        
                        beneficiary.setUpdatedAt(Instant.now());
                        if (command.getUpdatedById() != null) {
                            // Note: We'll need to track updatedById if available
                        }
                        
                        return beneficiaryCrudPort.update(beneficiary);
                    } catch (Exception e) {
                        log.error("Failed to update beneficiary {}: {}", beneficiaryId, e.getMessage());
                        return null;
                    }
                })
                .filter(b -> b != null)
                .collect(Collectors.toList());
        
        log.info("Bulk update completed: {} beneficiaries updated successfully", updated.size());
        return updated;
    }
    
    /**
     * Get statistics about beneficiaries
     */
    @Transactional(readOnly = true)
    public BeneficiaryStatistics getBeneficiaryStatistics() {
        long totalCount = beneficiarySearchPort.search(new FilterRequest(), Pageable.unpaged()).getTotalElements();
        long activeCount = beneficiarySearchPort.countByIsActiveTrueAndIsDeletedFalse();
        
        return BeneficiaryStatistics.builder()
                .totalBeneficiaries(totalCount)
                .activeBeneficiaries(activeCount)
                .lastUpdatedAt(Instant.now())
                .build();
    }
    
    /**
     * Inner class for statistics
     */
    @lombok.Data
    @lombok.Builder
    public static class BeneficiaryStatistics {
        private long totalBeneficiaries;
        private long activeBeneficiaries;
        private Instant lastUpdatedAt;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

