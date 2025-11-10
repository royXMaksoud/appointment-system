package com.care.appointment.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * Request DTO for verifying beneficiary credentials
 * 
 * Used by mobile app for simple authentication without JWT tokens.
 * The mobile app sends mobile + DOB, and receives beneficiary profile data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCredentialsRequest {

    @Schema(
        description = "Mobile number in E.164 format", 
        example = "+963912345678",
        required = true
    )
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid mobile number format (must be E.164)")
    private String mobileNumber;

    @Schema(
        description = "Date of birth for verification", 
        example = "1990-01-15",
        required = true
    )
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
}
