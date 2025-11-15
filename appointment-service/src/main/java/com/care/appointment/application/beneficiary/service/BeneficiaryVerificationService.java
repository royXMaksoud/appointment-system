package com.care.appointment.application.beneficiary.service;

import com.care.appointment.application.common.exception.UnauthorizedException;
import com.care.appointment.domain.model.Beneficiary;
import com.care.appointment.domain.ports.out.beneficiary.BeneficiarySearchPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Service for verifying beneficiary credentials
 * 
 * Supports multiple verification methods:
 * 1. Mobile + Date of Birth (primary) - for users who remember their DOB
 * 2. Mobile + Mother Name (alternative) - for users who can't remember DOB
 * 3. National ID (for complete registrations) - verified users only
 * 
 * Used by mobile app for simple authentication without JWT tokens.
 * The app stores the beneficiaryId in local storage for subsequent requests.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BeneficiaryVerificationService {

    private final BeneficiarySearchPort beneficiarySearchPort;

    /**
     * Verifies beneficiary using mobile number and date of birth
     * 
     * This is the PRIMARY authentication method for mobile users.
     * Process:
     * 1. Look up beneficiary by mobile + DOB combination
     * 2. Check if beneficiary account is active
     * 3. Return beneficiary with preferred language for localization
     * 
     * @param mobileNumber Mobile in E.164 format (e.g., +963912345678)
     * @param dateOfBirth Date of birth in YYYY-MM-DD format
     * @return Verified beneficiary with all profile data
     * @throws UnauthorizedException if credentials are invalid or account is inactive
     */
    public Beneficiary verifyByMobileAndDOB(String mobileNumber, LocalDate dateOfBirth) {
        log.debug("Verifying beneficiary: {} with DOB: {}", mobileNumber, dateOfBirth);

        return beneficiarySearchPort.findByMobileNumberAndDateOfBirth(mobileNumber, dateOfBirth)
                .filter(b -> Boolean.TRUE.equals(b.getIsActive()) && !Boolean.TRUE.equals(b.getIsDeleted()))
                .orElseThrow(() -> {
                    log.warn("Verification failed for mobile: {} with DOB: {}", mobileNumber, dateOfBirth);
                    return new UnauthorizedException("Invalid credentials or inactive account");
                });
    }

    /**
     * Verifies beneficiary using mobile number and mother's name
     * 
     * This is an ALTERNATIVE authentication method for users who:
     * - Don't remember their date of birth
     * - Prefer using their mother's name
     * 
     * @param mobileNumber Mobile in E.164 format
     * @param motherName Mother's full name (case-insensitive)
     * @return Verified beneficiary
     * @throws UnauthorizedException if credentials are invalid
     */
    public Beneficiary verifyByMobileAndMotherName(String mobileNumber, String motherName) {
        log.debug("Verifying beneficiary: {} with mother name", mobileNumber);

        return beneficiarySearchPort.findByMobileNumberAndMotherName(mobileNumber, motherName)
                .filter(b -> Boolean.TRUE.equals(b.getIsActive()) && !Boolean.TRUE.equals(b.getIsDeleted()))
                .orElseThrow(() -> {
                    log.warn("Verification failed for mobile: {} with mother name", mobileNumber);
                    return new UnauthorizedException("Invalid credentials or inactive account");
                });
    }
    
    /**
     * Verifies beneficiary by national ID
     * 
     * Used for complete registrations where users have provided their national ID.
     * This ensures stronger identity verification for critical operations.
     * 
     * @param nationalId National ID number
     * @return Verified beneficiary
     * @throws UnauthorizedException if national ID is invalid or account is inactive
     */
    public Beneficiary verifyByNationalId(String nationalId) {
        log.debug("Verifying beneficiary by national ID");
        
        return beneficiarySearchPort.findByNationalId(nationalId)
                .filter(b -> Boolean.TRUE.equals(b.getIsActive()) && !Boolean.TRUE.equals(b.getIsDeleted()))
                .orElseThrow(() -> new UnauthorizedException("Invalid national ID or inactive account"));
    }
}
