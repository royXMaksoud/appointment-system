package com.care.appointment.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentRequestDTO {
    
    private UUID appointmentRequestId;
    
    @NotNull(message = "Beneficiary ID is required")
    private UUID beneficiaryId;
    
    @NotNull(message = "Service type ID is required")
    private UUID serviceTypeId;
    
    private LocalDate preferredDate;
    
    private String priority;  // URGENT or NORMAL
    
    private String preferenceType;  // NEAREST_CENTER or EARLIEST_DATE
    
    private Double locationLatitude;
    
    private Double locationLongitude;
    
    private String mobileNumber;
    
    private String status;  // PENDING, APPROVED, REJECTED, CANCELLED
    
    private String rejectionReason;
    
    private Instant createdAt;
    
    private Instant updatedAt;
}

