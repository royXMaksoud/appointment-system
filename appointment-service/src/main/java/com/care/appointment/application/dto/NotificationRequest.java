package com.care.appointment.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for notification requests
 * Mirrors the notification-service NotificationRequest
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    @JsonProperty("beneficiary_id")
    private UUID beneficiaryId;

    @JsonProperty("mobile_number")
    private String mobileNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("has_installed_mobile_app")
    private boolean hasInstalledMobileApp;

    @JsonProperty("preferred_channel")
    private String preferredChannel; // SMS, EMAIL, PUSH

    @JsonProperty("notification_type")
    private NotificationType notificationType;

    @JsonProperty("appointment_qr")
    private AppointmentQRDTO appointmentQR;

    @JsonProperty("cancellation_reason")
    private String cancellationReason;

    public enum NotificationType {
        APPOINTMENT_CREATED,
        APPOINTMENT_REMINDER,
        APPOINTMENT_CANCELLED,
        QR_RESEND,
        VERIFICATION_CODE_SENT,
        APPOINTMENT_VERIFIED,
    }
}
