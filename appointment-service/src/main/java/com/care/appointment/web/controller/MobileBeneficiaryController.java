package com.care.appointment.web.controller;

import com.care.appointment.application.beneficiary.service.BeneficiaryVerificationService;
import com.care.appointment.domain.model.Beneficiary;
import com.care.appointment.web.dto.BeneficiaryDTO;
import com.care.appointment.web.dto.VerifyCredentialsRequest;
import com.care.appointment.web.mapper.BeneficiaryWebMapper;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for mobile app beneficiary operations
 * 
 * This controller provides simple authentication endpoints for mobile users.
 * Unlike admin APIs, these endpoints don't require JWT tokens and use basic
 * credentials (mobile + DOB) for verification.
 * 
 * Endpoints:
 * - POST /auth/verify - Verify credentials (mobile + DOB)
 * 
 * Security:
 * - No JWT required - simpler for mobile users
 * - Rate limiting should be applied to prevent brute force
 * - Failed attempts should be logged for security monitoring
 */
@RestController
@RequestMapping("/api/mobile/beneficiaries")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mobile - Beneficiary Authentication", description = "Mobile app beneficiary authentication and verification")
public class MobileBeneficiaryController {

    private final BeneficiaryVerificationService verificationService;
    private final BeneficiaryWebMapper beneficiaryWebMapper;

    /**
     * Verify beneficiary credentials for mobile app login
     * 
     * This is the PRIMARY authentication method for the mobile app.
     * 
     * Authentication flow:
     * 1. User enters mobile number and date of birth
     * 2. System looks up beneficiary by mobile + DOB combination
     * 3. If found and active, returns beneficiary profile with preferred language
     * 4. Mobile app stores beneficiaryId for subsequent requests
     * 
     * Benefits:
     * - No complex JWT management for mobile users
     * - Simple for non-tech-savvy users
     * - Language preference returned for app localization
     * 
     * Security:
     * - Rate limited to 5 requests per minute to prevent brute force attacks
     * - Failed attempts are logged for security monitoring
     * 
     * @param request Credentials containing mobile number and date of birth
     * @return Beneficiary data including:
     *         - Beneficiary ID (to use in subsequent requests)
     *         - Full name and profile information
     *         - Preferred language code (for app localization)
     *         - Registration status (QUICK or COMPLETE)
     * @throws 401 Unauthorized if credentials are invalid or account is inactive
     * @throws 429 Too Many Requests if rate limit exceeded
     */
    @PostMapping("/auth/verify")
    @RateLimiter(name = "mobileBeneficiaryAuth")
    @Operation(
        summary = "Verify beneficiary credentials",
        description = "Authenticate beneficiary using mobile number and date of birth. " +
                      "Returns beneficiary profile including preferred language for localization. " +
                      "No JWT token required - simple verification for mobile users. " +
                      "Rate limited to 5 requests per 60 seconds."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Credentials valid, beneficiary found and active"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials or inactive account"),
        @ApiResponse(responseCode = "400", description = "Validation error in request"),
        @ApiResponse(responseCode = "429", description = "Too many requests - rate limit exceeded")
    })
    public ResponseEntity<BeneficiaryDTO> verifyCredentials(
            @Valid @RequestBody VerifyCredentialsRequest request) {

        log.info("Verifying beneficiary credentials for mobile: {}", request.getMobileNumber());

        Beneficiary verified = verificationService.verifyByMobileAndDOB(
                request.getMobileNumber(),
                request.getDateOfBirth()
        );

        BeneficiaryDTO response = beneficiaryWebMapper.toDTO(verified);
        
        log.info("Successfully verified beneficiary: {} - {}", verified.getBeneficiaryId(), verified.getFullName());
        
        return ResponseEntity.ok(response);
    }
}
