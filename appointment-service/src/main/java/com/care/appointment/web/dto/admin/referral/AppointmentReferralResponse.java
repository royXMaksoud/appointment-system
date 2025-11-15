package com.care.appointment.web.dto.admin.referral;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentReferralResponse {
    private UUID referralId;
    private UUID appointmentId;
    private UUID beneficiaryId;
    private UUID referredToAppointmentId;
    private UUID referredToServiceTypeId;
    private String referralType;
    private String reason;
    private String clinicalNotes;
    private String status;
    private Instant referralDate;
    private Instant referredAppointmentDate;
    private Boolean isUrgent;
    private String rejectionReason;
    private UUID createdById;
    private String createdByName;
    private Instant createdAt;
    private UUID updatedById;
    private String updatedByName;
    private Instant updatedAt;
    private Long rowVersion;
}

