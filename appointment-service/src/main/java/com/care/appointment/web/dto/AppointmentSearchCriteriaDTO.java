package com.care.appointment.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for appointment search criteria from mobile app
 */
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentSearchCriteriaDTO {
    
    @NotNull(message = "Service type ID is required")
    private UUID serviceTypeId;
    
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    private Double longitude;
    
    private LocalDate preferredDate;
    
    @NotNull(message = "Preference type is required")
    private String preferenceType;  // NEAREST_CENTER or EARLIEST_DATE
    
    private String priority;  // URGENT or NORMAL
    
    private Integer radiusKm;  // search radius in kilometers
    
    private Integer maxResults;  // maximum number of suggestions to return
}

