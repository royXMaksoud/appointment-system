package com.care.appointment.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for notification results
 * Mirrors the notification-service NotificationResult
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResult {
    @JsonProperty("channel")
    private String channel;

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("error_message")
    private String errorMessage;

    @JsonProperty("sent_at")
    private long sentAt;
}
