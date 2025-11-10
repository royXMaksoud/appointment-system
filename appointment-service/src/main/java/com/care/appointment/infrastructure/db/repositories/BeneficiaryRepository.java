package com.care.appointment.infrastructure.db.repositories;

import com.care.appointment.infrastructure.db.entities.BeneficiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BeneficiaryRepository extends 
        JpaRepository<BeneficiaryEntity, UUID>,
        JpaSpecificationExecutor<BeneficiaryEntity> {
    
    Optional<BeneficiaryEntity> findByNationalId(String nationalId);
    
    Optional<BeneficiaryEntity> findByMobileNumber(String mobileNumber);
    
    Optional<BeneficiaryEntity> findByEmail(String email);
    
    boolean existsByNationalId(String nationalId);
    
    boolean existsByMobileNumber(String mobileNumber);
    
    /**
     * Finds beneficiary by mobile number and date of birth.
     * Used for mobile app authentication.
     * 
     * @param mobileNumber Mobile in E.164 format (e.g. +963912345678)
     * @param dateOfBirth Date of birth
     * @return Beneficiary if found
     */
    Optional<BeneficiaryEntity> findByMobileNumberAndDateOfBirth(
        String mobileNumber, LocalDate dateOfBirth);
    
    /**
     * Finds beneficiary by mobile number and mother name.
     * Alternative authentication method for users.
     */
    Optional<BeneficiaryEntity> findByMobileNumberAndMotherName(
        String mobileNumber, String motherName);
    
    /**
     * Finds beneficiaries by registration status.
     * Used to track incomplete registrations.
     */
    List<BeneficiaryEntity> findByRegistrationStatusCodeValueId(
        UUID registrationStatusCodeValueId);
    
    /**
     * Finds beneficiaries by preferred language code value ID.
     * Used for bulk messaging in preferred languages.
     */
    List<BeneficiaryEntity> findByPreferredLanguageCodeValueId(
        UUID preferredLanguageCodeValueId);
    
    /**
     * Finds beneficiaries by gender code value ID.
     * Used for targeted messaging by demographic.
     */
    List<BeneficiaryEntity> findByGenderCodeValueId(UUID genderCodeValueId);
    
    /**
     * Counts active beneficiaries.
     * Used for statistics and reporting.
     */
    long countByIsActiveTrueAndIsDeletedFalse();
    
    /**
     * Finds all active beneficiaries ordered by creation date descending.
     * Used for dashboard and bulk operations.
     */
    List<BeneficiaryEntity> findByIsActiveTrueAndIsDeletedFalseOrderByCreatedAtDesc();
}

