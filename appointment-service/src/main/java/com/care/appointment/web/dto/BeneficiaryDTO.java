package com.care.appointment.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class BeneficiaryDTO {
    
    private UUID beneficiaryId;
    
    private String nationalId;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    private String motherName;
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid mobile number format (E.164)")
    private String mobileNumber;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String address;
    
    private Double latitude;
    
    private Double longitude;
    
    // NEW FIELDS - Mobile app support
    private LocalDate dateOfBirth;
    
    private UUID genderCodeValueId;
    
    private String profilePhotoUrl;
    
    private UUID registrationStatusCodeValueId;
    
    private Instant registrationCompletedAt;
    
    private UUID registrationCompletedByUserId;
    
    private UUID preferredLanguageCodeValueId;
    
    private Boolean isActive;
    
    private Instant createdAt;
    
    private Instant updatedAt;
}

