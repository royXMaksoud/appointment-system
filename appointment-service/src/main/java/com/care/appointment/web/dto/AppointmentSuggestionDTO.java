package com.care.appointment.web.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO for appointment suggestions returned by search service
 */
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentSuggestionDTO {
    
    private UUID organizationBranchId;
    
    private String branchName;
    
    private String branchAddress;
    
    private Double branchLatitude;
    
    private Double branchLongitude;
    
    private Double distanceInKm;
    
    private LocalDate availableDate;
    
    private LocalTime availableTime;
    
    private Integer slotDurationMinutes;
    
    private UUID serviceTypeId;
    
    private String serviceTypeName;
    
    private Integer availableSlotsCount;
}

