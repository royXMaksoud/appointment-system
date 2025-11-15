package com.care.appointment.web.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for predicting no-show risk
 * Can be used with either appointmentId OR manual features
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionRequest {

    // Option 1: Predict by existing appointment
    private UUID appointmentId;

    // Option 2: Predict with manual features
    private Integer age;
    private String gender; // MALE, FEMALE, OTHER
    private String serviceType;
    private String appointmentTime; // HH:mm format
    private String dayOfWeek; // SUNDAY-SATURDAY
    private BigDecimal distanceKm;
    private String priority; // URGENT, NORMAL
    private Integer previousNoShows;
    private Integer previousAppointments;

    public boolean isUsingAppointmentId() {
        return appointmentId != null;
    }

    public boolean isUsingManualFeatures() {
        return age != null && gender != null && serviceType != null;
    }
}
