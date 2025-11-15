package com.care.appointment.application.referral.command;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class CreateAppointmentReferralCommand {
    UUID appointmentId;
    UUID beneficiaryId;
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
    UUID createdById;
}

