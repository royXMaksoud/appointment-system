package com.care.appointment.web.dto.admin.referral;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentReferralRequest {

    @NotNull
    private UUID appointmentId;

    @NotNull
    private UUID beneficiaryId;

    private UUID referredToAppointmentId;

    @NotNull
    private UUID referredToServiceTypeId;

    @NotNull
    @Size(max = 50)
    private String referralType;

    @Size(max = 500)
    private String reason;

    private String clinicalNotes;

    @Size(max = 50)
    private String status;

    private Instant referralDate;

    private Instant referredAppointmentDate;

    private Boolean isUrgent;

    @Size(max = 500)
    private String rejectionReason;

    private UUID createdById;
}

