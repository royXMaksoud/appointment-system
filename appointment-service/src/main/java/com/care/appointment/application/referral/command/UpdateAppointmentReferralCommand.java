package com.care.appointment.application.referral.command;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class UpdateAppointmentReferralCommand {
    UUID referralId;
    UUID referredToAppointmentId;
    UUID referredToServiceTypeId;
    String referralType;
    String reason;
    String clinicalNotes;
    String status;
    Instant referralDate;
    Instant referredAppointmentDate;
    Boolean isUrgent;
    String rejectionReason;
    UUID updatedById;
}

