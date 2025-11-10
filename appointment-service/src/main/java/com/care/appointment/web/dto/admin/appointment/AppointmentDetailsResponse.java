package com.care.appointment.web.dto.admin.appointment;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDetailsResponse {
    
    // Appointment Details
    private UUID appointmentId;
    private UUID appointmentRequestId;
    private UUID beneficiaryId;
    private String beneficiaryName;
    private String beneficiaryMobile;
    private UUID organizationBranchId;
    private String branchName;
    private UUID serviceTypeId;
    private String serviceTypeName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private Integer slotDurationMinutes;
    
    // Status
    private UUID appointmentStatusId;
    private String appointmentStatus;
    private String priority;
    
    // Notes
    private String notes;
    
    // Action/Outcome
    private UUID actionTypeId;
    private String actionTypeName;
    private String actionNotes;
    
    // Timestamps
    private Instant attendedAt;
    private Instant completedAt;
    private Instant cancelledAt;
    private String cancellationReason;
    
    // Audit
    private UUID createdById;
    private Instant createdAt;
    private UUID updatedById;
    private Instant updatedAt;
    private Long rowVersion;
}

