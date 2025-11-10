package com.care.appointment.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentTransferDTO {
    
    private UUID transferId;
    
    @NotNull(message = "Appointment ID is required")
    private UUID appointmentId;
    
    @NotNull(message = "From branch ID is required")
    private UUID fromOrganizationBranchId;
    
    @NotNull(message = "To branch ID is required")
    private UUID toOrganizationBranchId;
    
    private UUID newAppointmentId;
    
    private String transferReason;
    
    private UUID transferredByUserId;
    
    private Instant transferredAt;
    
    private String status;  // PENDING, ACCEPTED, REJECTED
    
    private String responseNotes;
    
    private UUID respondedByUserId;
    
    private Instant respondedAt;
    
    // Extended information
    private String fromBranchName;
    
    private String toBranchName;
    
    private AppointmentDTO appointmentDetails;
}

