package com.care.appointment.domain.ports.out.beneficiary;

import com.care.appointment.domain.model.Beneficiary;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeneficiarySearchPort {
    Page<Beneficiary> search(FilterRequest filter, Pageable pageable);
    boolean existsByNationalId(String nationalId);
    boolean existsByMobileNumber(String mobileNumber);
    Optional<Beneficiary> findByMobileNumber(String mobileNumber);
    
    /**
     * Finds beneficiary by national ID.
     */
    Optional<Beneficiary> findByNationalId(String nationalId);
    
    /**
     * Finds beneficiary by mobile number and date of birth.
     * Used for mobile app authentication.
     */
    Optional<Beneficiary> findByMobileNumberAndDateOfBirth(String mobileNumber, LocalDate dateOfBirth);
    
    /**
     * Finds beneficiary by mobile number and mother name.
     * Alternative authentication method.
     */
    Optional<Beneficiary> findByMobileNumberAndMotherName(String mobileNumber, String motherName);
    
    /**
     * Finds beneficiaries by registration status code value ID.
     */
    List<Beneficiary> findByRegistrationStatusCodeValueId(UUID registrationStatusCodeValueId);
    
    /**
     * Finds beneficiaries by preferred language code value ID.
     * Used for bulk messaging in preferred languages.
     */
    List<Beneficiary> findByPreferredLanguageCodeValueId(UUID preferredLanguageCodeValueId);
    
    /**
     * Finds beneficiaries by gender code value ID.
     * Used for targeted messaging by demographic.
     */
    List<Beneficiary> findByGenderCodeValueId(UUID genderCodeValueId);
    
    /**
     * Counts active beneficiaries.
     * Used for statistics and reporting.
     */
    long countByIsActiveTrueAndIsDeletedFalse();
    
    /**
     * Finds all active beneficiaries ordered by creation date descending.
     * Used for dashboard and bulk operations.
     */
    List<Beneficiary> findByIsActiveTrueAndIsDeletedFalseOrderByCreatedAtDesc();
}

