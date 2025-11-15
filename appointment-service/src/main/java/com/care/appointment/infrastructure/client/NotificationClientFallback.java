package com.care.appointment.infrastructure.client;

import com.care.appointment.application.dto.NotificationRequest;
import com.care.appointment.application.dto.NotificationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for NotificationClient
 * Used when notification-service is unavailable (circuit breaker open)
 */
@Component
@Slf4j
public class NotificationClientFallback implements NotificationClient {

    /**
     * Fallback for appointment created notification
     */
    @Override
    public NotificationResult notifyAppointmentCreated(NotificationRequest request) {
        log.warn("Notification service is unavailable. Using fallback for appointment created notification");
        return buildFallbackResult("FALLBACK", "Notification service is temporarily unavailable");
    }

    /**
     * Fallback for appointment reminder notification
     */
    @Override
    public NotificationResult notifyAppointmentReminder(NotificationRequest request) {
        log.warn("Notification service is unavailable. Using fallback for appointment reminder notification");
        return buildFallbackResult("FALLBACK", "Notification service is temporarily unavailable");
    }

    /**
     * Fallback for appointment cancelled notification
     */
    @Override
    public NotificationResult notifyAppointmentCancelled(NotificationRequest request) {
        log.warn("Notification service is unavailable. Using fallback for appointment cancelled notification");
        return buildFallbackResult("FALLBACK", "Notification service is temporarily unavailable");
    }

    /**
     * Fallback for resend QR code
     */
    @Override
    public NotificationResult resendQRCode(NotificationRequest request) {
        log.warn("Notification service is unavailable. Using fallback for QR resend");
        return buildFallbackResult("FALLBACK", "Notification service is temporarily unavailable");
    }

    /**
     * Build fallback result
     */
    private NotificationResult buildFallbackResult(String channel, String errorMessage) {
        return NotificationResult.builder()
                .channel(channel)
                .success(false)
                .errorMessage(errorMessage)
                .sentAt(System.currentTimeMillis())
                .build();
    }
}
