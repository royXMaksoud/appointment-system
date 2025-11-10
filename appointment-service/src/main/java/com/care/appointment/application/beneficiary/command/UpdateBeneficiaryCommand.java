package com.care.appointment.application.beneficiary.command;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBeneficiaryCommand {
    private UUID beneficiaryId;
    private String nationalId;
    private String fullName;
    private String motherName;
    private String mobileNumber;
    private String email;
    private String address;
    private Double latitude;
    private Double longitude;
    
    // NEW FIELDS - Mobile app support
    private LocalDate dateOfBirth;
    private UUID genderCodeValueId;
    private String profilePhotoUrl;
    private UUID registrationStatusCodeValueId;
    private UUID preferredLanguageCodeValueId;
    
    private Boolean isActive;
}

