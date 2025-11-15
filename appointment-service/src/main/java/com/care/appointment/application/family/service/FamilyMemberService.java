package com.care.appointment.application.family.service;

import com.care.appointment.application.family.command.CreateFamilyMemberCommand;
import com.care.appointment.application.family.command.UpdateFamilyMemberCommand;
import com.care.appointment.application.family.mapper.FamilyMemberDomainMapper;
import com.care.appointment.application.common.exception.NotFoundException;
import com.care.appointment.domain.model.FamilyMember;
import com.care.appointment.domain.ports.in.family.FamilyMemberCrudPort;
import com.care.appointment.domain.ports.in.family.FamilyMemberSearchPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing family members
 * 
 * Provides CRUD operations for family member management.
 * Family members can be linked to beneficiaries for appointment booking
 * and emergency contacts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FamilyMemberService {

    private final FamilyMemberCrudPort crudPort;
    private final FamilyMemberSearchPort searchPort;
    private final FamilyMemberDomainMapper domainMapper;

    /**
     * Create a new family member
     */
    public FamilyMember create(CreateFamilyMemberCommand command) {
        log.debug("Creating family member for beneficiary: {}", command.getBeneficiaryId());

        // Validate national ID uniqueness if provided
        if (command.getNationalId() != null && searchPort.existsByNationalId(command.getNationalId())) {
            throw new IllegalArgumentException("National ID already exists");
        }

        FamilyMember domain = domainMapper.toDomain(command);
        FamilyMember saved = crudPort.save(domain);
        
        log.info("Created family member: {} for beneficiary: {}", saved.getFamilyMemberId(), command.getBeneficiaryId());
        return saved;
    }

    /**
     * Update an existing family member
     */
    public FamilyMember update(UUID familyMemberId, UpdateFamilyMemberCommand command) {
        log.debug("Updating family member: {}", familyMemberId);

        FamilyMember existing = crudPort.findById(familyMemberId)
                .orElseThrow(() -> new NotFoundException("Family member not found"));

        domainMapper.updateFromCommand(existing, command);
        FamilyMember updated = crudPort.update(existing);
        
        log.info("Updated family member: {}", familyMemberId);
        return updated;
    }

    /**
     * Get family member by ID
     */
    @Transactional(readOnly = true)
    public FamilyMember getById(UUID familyMemberId) {
        return crudPort.findById(familyMemberId)
                .orElseThrow(() -> new NotFoundException("Family member not found"));
    }

    /**
     * Get all family members for a beneficiary
     */
    @Transactional(readOnly = true)
    public List<FamilyMember> getByBeneficiaryId(UUID beneficiaryId) {
        log.debug("Getting family members for beneficiary: {}", beneficiaryId);
        return searchPort.findActiveByBeneficiaryId(beneficiaryId);
    }

    /**
     * Get emergency contacts for a beneficiary
     */
    @Transactional(readOnly = true)
    public List<FamilyMember> getEmergencyContacts(UUID beneficiaryId) {
        log.debug("Getting emergency contacts for beneficiary: {}", beneficiaryId);
        return searchPort.findEmergencyContactsByBeneficiaryId(beneficiaryId);
    }

    /**
     * Delete (soft delete) a family member
     */
    public void delete(UUID familyMemberId) {
        log.debug("Deleting family member: {}", familyMemberId);

        FamilyMember existing = crudPort.findById(familyMemberId)
                .orElseThrow(() -> new NotFoundException("Family member not found"));

        existing.setIsDeleted(true);
        crudPort.update(existing);
        
        log.info("Deleted family member: {}", familyMemberId);
    }

    /**
     * Get count of family members for a beneficiary
     */
    @Transactional(readOnly = true)
    public long countByBeneficiaryId(UUID beneficiaryId) {
        return searchPort.countByBeneficiaryId(beneficiaryId);
    }
}

