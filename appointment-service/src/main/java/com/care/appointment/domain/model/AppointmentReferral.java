package com.care.appointment.domain.model;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain model for Appointment Referrals
 * 
 * Tracks referrals between appointments - when a patient is referred from one appointment
 * to another (e.g., from general consultation to specialist, or between departments).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentReferral {
    
    private UUID referralId;
    private UUID appointmentId; // Source appointment
    private UUID beneficiaryId; // Patient being referred
    private UUID referredToAppointmentId; // Target appointment (nullable if not yet booked)
    private UUID referredToServiceTypeId; // Target service type
    private String referralType; // REFERRAL, TRANSFER, FOLLOW_UP, SECOND_OPINION
    private String reason; // Why the referral was made
    private String clinicalNotes; // Clinical notes for the referral
    private String status; // PENDING, ACCEPTED, COMPLETED, CANCELLED, REJECTED
    private Instant referralDate;
    private Instant referredAppointmentDate; // When the referred appointment is scheduled
    private Boolean isUrgent;
    private String rejectionReason; // If status is REJECTED
    private UUID createdById;
    
    private Instant createdAt;
    private Instant updatedAt;
    private Long rowVersion;
}

