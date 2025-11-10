package com.care.appointment.application.family.command;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFamilyMemberCommand {
    private UUID beneficiaryId;
    private String nationalId;
    private String fullName;
    private String motherName;
    private LocalDate dateOfBirth;
    private String relationType;
    private String relationDescription;
    private String mobileNumber;
    private String email;
    private UUID genderCodeValueId;
    private Boolean isEmergencyContact;
    private Boolean canBookAppointments;
    private UUID createdById;
}

