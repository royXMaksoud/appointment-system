package com.care.appointment.application.service;

import com.care.appointment.infrastructure.db.entities.BeneficiaryEntity;
import com.care.appointment.infrastructure.db.repositories.BeneficiaryRepository;
import com.care.appointment.web.dto.BeneficiaryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing beneficiaries (patients/service recipients)
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BeneficiaryService {
    
    private final BeneficiaryRepository beneficiaryRepository;
    
    /**
     * Register a new beneficiary or return existing one
     */
    @Transactional
    public BeneficiaryDTO registerBeneficiary(BeneficiaryDTO dto) {
        log.info("Registering beneficiary: mobile={}", dto.getMobileNumber());
        
        // Check if beneficiary already exists by mobile or email
        Optional<BeneficiaryEntity> existing = beneficiaryRepository.findByMobileNumber(dto.getMobileNumber());
        
        if (existing.isPresent()) {
            log.info("Beneficiary already exists: id={}", existing.get().getBeneficiaryId());
            return mapToDTO(existing.get());
        }
        
        // Create new beneficiary
        BeneficiaryEntity entity = BeneficiaryEntity.builder()
            .nationalId(dto.getNationalId())
            .fullName(dto.getFullName())
            .motherName(dto.getMotherName())
            .mobileNumber(dto.getMobileNumber())
            .email(dto.getEmail())
            .address(dto.getAddress())
            .latitude(dto.getLatitude())
            .longitude(dto.getLongitude())
            .isActive(true)
            .build();
        
        BeneficiaryEntity saved = beneficiaryRepository.save(entity);
        log.info("Beneficiary registered successfully: id={}", saved.getBeneficiaryId());
        
        return mapToDTO(saved);
    }
    
    /**
     * Get beneficiary by ID
     */
    @Transactional(readOnly = true)
    public Optional<BeneficiaryDTO> getBeneficiaryById(UUID beneficiaryId) {
        return beneficiaryRepository.findById(beneficiaryId)
            .map(this::mapToDTO);
    }
    
    /**
     * Get beneficiary by mobile number
     */
    @Transactional(readOnly = true)
    public Optional<BeneficiaryDTO> getBeneficiaryByMobile(String mobileNumber) {
        return beneficiaryRepository.findByMobileNumber(mobileNumber)
            .map(this::mapToDTO);
    }
    
    /**
     * Update beneficiary information
     */
    @Transactional
    public BeneficiaryDTO updateBeneficiary(UUID beneficiaryId, BeneficiaryDTO dto) {
        BeneficiaryEntity entity = beneficiaryRepository.findById(beneficiaryId)
            .orElseThrow(() -> new RuntimeException("Beneficiary not found: " + beneficiaryId));
        
        entity.setFullName(dto.getFullName());
        entity.setMotherName(dto.getMotherName());
        entity.setEmail(dto.getEmail());
        entity.setAddress(dto.getAddress());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        
        BeneficiaryEntity updated = beneficiaryRepository.save(entity);
        log.info("Beneficiary updated: id={}", beneficiaryId);
        
        return mapToDTO(updated);
    }
    
    private BeneficiaryDTO mapToDTO(BeneficiaryEntity entity) {
        return BeneficiaryDTO.builder()
            .beneficiaryId(entity.getBeneficiaryId())
            .nationalId(entity.getNationalId())
            .fullName(entity.getFullName())
            .motherName(entity.getMotherName())
            .mobileNumber(entity.getMobileNumber())
            .email(entity.getEmail())
            .address(entity.getAddress())
            .latitude(entity.getLatitude())
            .longitude(entity.getLongitude())
            .isActive(entity.getIsActive())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}

