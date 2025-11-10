package com.care.appointment.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentDTO {
    
    private UUID appointmentId;
    
    private UUID appointmentRequestId;
    
    @NotNull(message = "Beneficiary ID is required")
    private UUID beneficiaryId;
    
    @NotNull(message = "Organization branch ID is required")
    private UUID organizationBranchId;
    
    @NotNull(message = "Service type ID is required")
    private UUID serviceTypeId;
    
    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;
    
    @NotNull(message = "Appointment time is required")
    private LocalTime appointmentTime;
    
    private Integer slotDurationMinutes;
    
    @NotNull(message = "Appointment status ID is required")
    private UUID appointmentStatusId;
    
    private String statusName;  // localized status name
    
    private String priority;  // URGENT or NORMAL
    
    private String notes;
    
    private UUID actionTypeId;
    
    private String actionTypeName;  // localized action type name
    
    private String actionNotes;
    
    private Instant attendedAt;
    
    private Instant completedAt;
    
    private Instant cancelledAt;
    
    private String cancellationReason;
    
    // Extended information
    private String beneficiaryName;
    
    private String branchName;
    
    private String serviceTypeName;
    
    private Instant createdAt;
    
    private Instant updatedAt;
}

