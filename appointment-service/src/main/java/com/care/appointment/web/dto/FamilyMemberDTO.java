package com.care.appointment.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Family member information")
public class FamilyMemberDTO {
    
    @Schema(description = "Family member ID")
    private UUID familyMemberId;
    
    @Schema(description = "Parent beneficiary ID")
    private UUID beneficiaryId;
    
    @Schema(description = "National ID")
    private String nationalId;
    
    @Schema(description = "Full name", required = true)
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @Schema(description = "Mother's name")
    private String motherName;
    
    @Schema(description = "Date of birth")
    private LocalDate dateOfBirth;
    
    @Schema(description = "Relation type: SPOUSE, CHILD, PARENT, SIBLING, OTHER", required = true)
    @NotBlank(message = "Relation type is required")
    private String relationType;
    
    @Schema(description = "Custom relation description if OTHER")
    private String relationDescription;
    
    @Schema(description = "Mobile number")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid mobile number format")
    private String mobileNumber;
    
    @Schema(description = "Email address")
    @Email(message = "Invalid email format")
    private String email;
    
    @Schema(description = "Gender code value ID")
    private UUID genderCodeValueId;
    
    @Schema(description = "Is emergency contact")
    private Boolean isEmergencyContact;
    
    @Schema(description = "Can book appointments for this member")
    private Boolean canBookAppointments;
    
    @Schema(description = "Is active")
    private Boolean isActive;
    
    @Schema(description = "Created at")
    private Instant createdAt;
    
    @Schema(description = "Updated at")
    private Instant updatedAt;
}

