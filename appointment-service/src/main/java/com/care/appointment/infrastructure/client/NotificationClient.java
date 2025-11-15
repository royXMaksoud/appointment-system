package com.care.appointment.infrastructure.client;

import com.care.appointment.application.dto.AppointmentQRDTO;
import com.care.appointment.application.dto.NotificationRequest;
import com.care.appointment.application.dto.NotificationResult;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign Client for Notification Service
 * Provides inter-service communication for sending notifications
 */
@FeignClient(
    name = "notification-service",
    url = "${notification.service.url:http://localhost:6067}",
    fallback = NotificationClientFallback.class
)
public interface NotificationClient {

    /**
     * Send appointment created notification
     */
    @PostMapping("/api/v1/notifications/appointment-created")
    @CircuitBreaker(name = "notificationService", fallbackMethod = "appointmentCreatedFallback")
    @Retry(name = "notificationService")
    NotificationResult notifyAppointmentCreated(@RequestBody NotificationRequest request);

    /**
     * Send appointment reminder notification
     */
    @PostMapping("/api/v1/notifications/appointment-reminder")
    @CircuitBreaker(name = "notificationService", fallbackMethod = "appointmentReminderFallback")
    @Retry(name = "notificationService")
    NotificationResult notifyAppointmentReminder(@RequestBody NotificationRequest request);

    /**
     * Send appointment cancelled notification
     */
    @PostMapping("/api/v1/notifications/appointment-cancelled")
    @CircuitBreaker(name = "notificationService", fallbackMethod = "appointmentCancelledFallback")
    @Retry(name = "notificationService")
    NotificationResult notifyAppointmentCancelled(@RequestBody NotificationRequest request);

    /**
     * Resend appointment QR code
     */
    @PostMapping("/api/v1/notifications/resend-qr")
    @CircuitBreaker(name = "notificationService", fallbackMethod = "resendQRFallback")
    @Retry(name = "notificationService")
    NotificationResult resendQRCode(@RequestBody NotificationRequest request);
}
