package com.care.appointment.domain.model;

import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Domain model for Beneficiary Family Members
 * 
 * Represents family members of a beneficiary (spouse, children, parents, etc.)
 * Used for:
 * - Booking appointments on behalf of family members
 * - Managing family health records
 * - Emergency contact information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMember {
    
    // EXISTING FIELDS
    private UUID familyMemberId;
    private UUID beneficiaryId; // Parent beneficiary
    private String nationalId;
    private String fullName;
    private String motherName;
    private LocalDate dateOfBirth;
    
    // NEW FIELDS
    private String relationType; // SPOUSE, CHILD, PARENT, SIBLING, OTHER
    private String relationDescription; // If OTHER, provide description
    private String mobileNumber;
    private String email;
    private UUID genderCodeValueId;
    private Boolean isEmergencyContact;
    private Boolean canBookAppointments; // If true, user can book appointments for this member
    
    // AUDIT FIELDS
    private Boolean isActive;
    private Boolean isDeleted;
    private UUID createdById;
    private Instant createdAt;
    private Instant updatedAt;
    private Long rowVersion;
}

