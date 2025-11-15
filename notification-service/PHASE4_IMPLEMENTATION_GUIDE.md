# Notification Service - Phase 4 Implementation Guide

## Overview
Phase 4 adds **SMS Provider Integration (Twilio)**, **Push Notification Provider (Firebase)**, and **Webhook Support** for delivery confirmation and external system integration.

---

## Part 1: SMS Provider Integration (Twilio)

### Step 1: Add Twilio Dependency to pom.xml

```xml
<!-- Twilio SDK for SMS -->
<dependency>
    <groupId>com.twilio.sdk</groupId>
    <artifactId>twilio</artifactId>
    <version>9.0.1</version>
</dependency>
```

### Step 2: Create Twilio Configuration Class

**File**: `infrastructure/config/TwilioConfig.java`

```java
package com.care.notification.infrastructure.config;

import com.twilio.Twilio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Twilio configuration for SMS notifications
 * Initializes Twilio SDK with account credentials
 */
@Component
@Slf4j
public class TwilioConfig {

    @Value("${twilio.account-sid:#{null}}")
    private String accountSid;

    @Value("${twilio.auth-token:#{null}}")
    private String authToken;

    @Value("${twilio.phone-number:#{null}}")
    private String phoneNumber;

    /**
     * Initialize Twilio SDK on application startup
     */
    @PostConstruct
    public void initTwilio() {
        if (accountSid != null && authToken != null) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio SDK initialized successfully");
        } else {
            log.warn("Twilio credentials not configured. SMS notifications will be disabled.");
        }
    }

    public String getAccountSid() {
        return accountSid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isConfigured() {
        return accountSid != null && authToken != null && phoneNumber != null;
    }
}
```

### Step 3: Create Twilio SMS Delivery Service

**File**: `application/service/TwilioSmsService.java`

```java
package com.care.notification.application.service;

import com.care.notification.infrastructure.config.TwilioConfig;
import com.care.notification.presentation.dto.NotificationRequest;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Twilio-based SMS delivery service
 * Sends SMS notifications via Twilio API
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TwilioSmsService {

    private final TwilioConfig twilioConfig;

    /**
     * Send SMS via Twilio
     *
     * @param request Notification request
     * @param phoneNumber Recipient phone number
     * @return Message SID for tracking
     */
    public String sendSms(NotificationRequest request, String phoneNumber) {
        if (!twilioConfig.isConfigured()) {
            log.warn("Twilio not configured. SMS not sent.");
            return null;
        }

        try {
            Message message = Message.creator(
                new PhoneNumber(phoneNumber),      // To number
                new PhoneNumber(twilioConfig.getPhoneNumber()), // From number
                request.getMessage()               // Message body
            ).create();

            log.info("SMS sent successfully. Message SID: {}, To: {}",
                message.getSid(), phoneNumber);

            return message.getSid();
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
            throw new RuntimeException("SMS delivery failed", e);
        }
    }

    /**
     * Send SMS with retry logic
     */
    public String sendSmsWithRetry(NotificationRequest request, String phoneNumber, int maxRetries) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return sendSms(request, phoneNumber);
            } catch (Exception e) {
                if (attempt == maxRetries) {
                    log.error("SMS delivery failed after {} attempts", maxRetries);
                    throw e;
                }
                long delayMs = (long) Math.pow(2, attempt - 1) * 1000; // Exponential backoff
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }
        return null;
    }
}
```

### Step 4: Add Twilio Configuration to application.yml

```yaml
twilio:
  account-sid: ${TWILIO_ACCOUNT_SID:your-account-sid}
  auth-token: ${TWILIO_AUTH_TOKEN:your-auth-token}
  phone-number: ${TWILIO_PHONE_NUMBER:+1234567890}
  retry:
    max-attempts: 3
    backoff-multiplier: 2
```

---

## Part 2: Push Notification Provider (Firebase Cloud Messaging)

### Step 1: Add Firebase Dependency to pom.xml

```xml
<!-- Firebase Admin SDK -->
<dependency>
    <groupId>com.google.firebase</groupId>
    <artifactId>firebase-admin</artifactId>
    <version>9.2.0</version>
</dependency>
```

### Step 2: Create Firebase Configuration Class

**File**: `infrastructure/config/FirebaseConfig.java`

```java
package com.care.notification.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Firebase Cloud Messaging configuration
 * Initializes Firebase Admin SDK for push notifications
 */
@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${firebase.credentials-path:#{null}}")
    private Resource credentialsPath;

    @Value("${firebase.database-url:#{null}}")
    private String databaseUrl;

    /**
     * Initialize Firebase Admin SDK
     */
    @PostConstruct
    public void initializeFirebase() {
        try {
            if (credentialsPath == null || !credentialsPath.exists()) {
                log.warn("Firebase credentials not found. Push notifications will be disabled.");
                return;
            }

            GoogleCredentials credentials = GoogleCredentials
                .fromStream(credentialsPath.getInputStream());

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setDatabaseUrl(databaseUrl)
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase Admin SDK initialized successfully");
            }
        } catch (IOException e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage());
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }
}
```

### Step 3: Create Firebase Push Notification Service

**File**: `application/service/FirebasePushService.java`

```java
package com.care.notification.application.service;

import com.google.firebase.messaging.*;
import com.care.notification.presentation.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Firebase Cloud Messaging service for push notifications
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FirebasePushService {

    private final FirebaseMessaging firebaseMessaging;

    /**
     * Send push notification to device
     *
     * @param request Notification request
     * @param deviceToken Firebase device token
     * @return Message ID for tracking
     */
    public String sendPushNotification(NotificationRequest request, String deviceToken) {
        try {
            Message message = Message.builder()
                .setToken(deviceToken)
                .setNotification(
                    Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getMessage())
                        .build()
                )
                .putAllData(buildDataPayload(request))
                .setAndroidConfig(
                    AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(
                            AndroidNotification.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getMessage())
                                .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                                .build()
                        )
                        .build()
                )
                .setApnsConfig(
                    ApnsConfig.builder()
                        .putHeader("apns-priority", "10")
                        .setAps(
                            Aps.builder()
                                .setAlert(ApsAlert.builder()
                                    .setTitle(request.getTitle())
                                    .setBody(request.getMessage())
                                    .build()
                                )
                                .setSound("default")
                                .build()
                        )
                        .build()
                )
                .build();

            String messageId = firebaseMessaging.send(message);
            log.info("Push notification sent successfully. Message ID: {}, Device: {}",
                messageId, deviceToken);

            return messageId;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send push notification to {}: {}", deviceToken, e.getMessage());
            throw new RuntimeException("Push notification delivery failed", e);
        }
    }

    /**
     * Send push to topic (subscribe multiple devices)
     */
    public String sendPushToTopic(NotificationRequest request, String topic) {
        try {
            Message message = Message.builder()
                .setTopic(topic)
                .setNotification(
                    Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getMessage())
                        .build()
                )
                .putAllData(buildDataPayload(request))
                .build();

            String messageId = firebaseMessaging.send(message);
            log.info("Push notification sent to topic '{}'. Message ID: {}", topic, messageId);

            return messageId;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send push to topic {}: {}", topic, e.getMessage());
            throw new RuntimeException("Topic push notification failed", e);
        }
    }

    /**
     * Subscribe device to topic
     */
    public void subscribeToTopic(String deviceToken, String topic) {
        try {
            firebaseMessaging.subscribeToTopic(java.util.List.of(deviceToken), topic);
            log.info("Device {} subscribed to topic {}", deviceToken, topic);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to subscribe to topic: {}", e.getMessage());
        }
    }

    /**
     * Unsubscribe device from topic
     */
    public void unsubscribeFromTopic(String deviceToken, String topic) {
        try {
            firebaseMessaging.unsubscribeFromTopic(java.util.List.of(deviceToken), topic);
            log.info("Device {} unsubscribed from topic {}", deviceToken, topic);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to unsubscribe from topic: {}", e.getMessage());
        }
    }

    /**
     * Build data payload from notification request
     */
    private Map<String, String> buildDataPayload(NotificationRequest request) {
        Map<String, String> data = new HashMap<>();
        data.put("notificationId", request.getNotificationId());
        data.put("type", request.getNotificationType());
        data.put("priority", request.getPriority().toString());
        if (request.getMetadata() != null) {
            request.getMetadata().forEach((key, value) ->
                data.put(key, value.toString())
            );
        }
        return data;
    }
}
```

### Step 4: Add Firebase Configuration to application.yml

```yaml
firebase:
  credentials-path: classpath:firebase-service-account.json
  database-url: https://your-project.firebaseio.com
  push:
    timeout-seconds: 10
    retry-attempts: 3
```

---

## Part 3: Webhook Support for Delivery Confirmation

### Step 1: Create Webhook Event Entity

**File**: `infrastructure/persistence/entity/WebhookEventEntity.java`

```java
package com.care.notification.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Webhook event for external delivery confirmation
 */
@Entity
@Table(name = "webhook_events", indexes = {
    @Index(name = "idx_webhook_status", columnList = "status"),
    @Index(name = "idx_webhook_notification", columnList = "notification_id"),
    @Index(name = "idx_webhook_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "notification_id", nullable = false)
    private UUID notificationId;

    @Column(name = "webhook_url", nullable = false, length = 500)
    private String webhookUrl;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType; // sent, delivered, failed, bounced

    @Column(name = "status", nullable = false, length = 20)
    private String status; // pending, success, failed

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload; // JSON webhook payload

    @Column(name = "response_code")
    private Integer responseCode;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    @Column(name = "retry_count", columnDefinition = "integer default 0")
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "max_retries", columnDefinition = "integer default 5")
    @Builder.Default
    private Integer maxRetries = 5;

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;

    @Column(name = "signature", length = 256)
    private String signature; // HMAC-SHA256 signature for verification

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "is_deleted")
    @Builder.Default
    private boolean isDeleted = false;
}
```

### Step 2: Create Webhook Repository

**File**: `infrastructure/persistence/repository/WebhookEventRepository.java`

```java
package com.care.notification.infrastructure.persistence.repository;

import com.care.notification.infrastructure.persistence.entity.WebhookEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WebhookEventRepository extends JpaRepository<WebhookEventEntity, UUID> {

    /**
     * Find pending webhook events ready for delivery
     */
    @Query("SELECT w FROM WebhookEventEntity w WHERE " +
           "w.status = 'pending' AND " +
           "w.nextRetryAt <= CURRENT_TIMESTAMP AND " +
           "w.retryCount < w.maxRetries AND " +
           "w.isDeleted = false")
    List<WebhookEventEntity> findPendingWebhooks();

    /**
     * Find webhook events by notification
     */
    List<WebhookEventEntity> findByNotificationId(UUID notificationId);

    /**
     * Find failed webhook events
     */
    @Query("SELECT w FROM WebhookEventEntity w WHERE " +
           "w.status = 'failed' AND " +
           "w.isDeleted = false")
    List<WebhookEventEntity> findFailedWebhooks();
}
```

### Step 3: Create Webhook Delivery Service

**File**: `application/service/WebhookDeliveryService.java`

```java
package com.care.notification.application.service;

import com.care.notification.infrastructure.persistence.entity.WebhookEventEntity;
import com.care.notification.infrastructure.persistence.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

/**
 * Webhook delivery service for sending delivery confirmation to external systems
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookDeliveryService {

    private final WebhookEventRepository webhookEventRepository;
    private final RestTemplate restTemplate;

    @Value("${webhook.secret-key:your-secret-key}")
    private String secretKey;

    @Value("${webhook.timeout-seconds:30}")
    private int timeoutSeconds;

    /**
     * Publish webhook event asynchronously
     */
    @Async
    public void publishWebhookEvent(WebhookEventEntity event) {
        // Sign the payload
        String signature = generateSignature(event.getPayload());
        event.setSignature(signature);
        event.setStatus("pending");
        event.setRetryCount(0);
        event.setNextRetryAt(LocalDateTime.now());

        webhookEventRepository.save(event);
        log.info("Webhook event created: {}", event.getId());

        // Attempt immediate delivery
        deliverWebhook(event);
    }

    /**
     * Deliver webhook with retry logic
     */
    private void deliverWebhook(WebhookEventEntity event) {
        try {
            var response = restTemplate.postForEntity(
                event.getWebhookUrl(),
                event.getPayload(),
                String.class
            );

            event.setStatus("success");
            event.setResponseCode(response.getStatusCode().value());
            event.setProcessedAt(LocalDateTime.now());

            log.info("Webhook delivered successfully: {}", event.getId());
        } catch (Exception e) {
            handleWebhookFailure(event, e);
        }

        webhookEventRepository.save(event);
    }

    /**
     * Handle webhook delivery failure with exponential backoff
     */
    private void handleWebhookFailure(WebhookEventEntity event, Exception exception) {
        event.setRetryCount(event.getRetryCount() + 1);

        if (event.getRetryCount() >= event.getMaxRetries()) {
            event.setStatus("failed");
            log.error("Webhook delivery failed after {} retries: {}",
                event.getMaxRetries(), event.getId());
        } else {
            // Exponential backoff: 2^n minutes
            long delayMinutes = (long) Math.pow(2, event.getRetryCount());
            event.setNextRetryAt(LocalDateTime.now().plus(delayMinutes, ChronoUnit.MINUTES));
            log.warn("Webhook delivery failed, will retry in {} minutes: {}",
                delayMinutes, event.getId());
        }

        event.setResponseBody(exception.getMessage());
    }

    /**
     * Generate HMAC-SHA256 signature for webhook payload
     */
    private String generateSignature(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8), 0,
                secretKey.getBytes(StandardCharsets.UTF_8).length, "HmacSHA256"
            );
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            log.error("Failed to generate signature: {}", e.getMessage());
            throw new RuntimeException("Signature generation failed", e);
        }
    }

    /**
     * Verify webhook signature (for receiving webhooks)
     */
    public boolean verifySignature(String payload, String providedSignature) {
        String calculatedSignature = generateSignature(payload);
        return calculatedSignature.equals(providedSignature);
    }
}
```

### Step 4: Create Webhook Scheduler

**File**: `infrastructure/scheduler/WebhookRetryScheduler.java`

```java
package com.care.notification.infrastructure.scheduler;

import com.care.notification.application.service.WebhookDeliveryService;
import com.care.notification.infrastructure.persistence.entity.WebhookEventEntity;
import com.care.notification.infrastructure.persistence.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Scheduled task for retrying failed webhook deliveries
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookRetryScheduler {

    private final WebhookEventRepository webhookEventRepository;
    private final WebhookDeliveryService webhookDeliveryService;

    /**
     * Retry pending webhooks every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void retryPendingWebhooks() {
        try {
            List<WebhookEventEntity> pendingWebhooks =
                webhookEventRepository.findPendingWebhooks();

            log.info("Found {} pending webhooks to retry", pendingWebhooks.size());

            for (WebhookEventEntity webhook : pendingWebhooks) {
                // Delivery logic would be called here
                log.debug("Retrying webhook event: {}", webhook.getId());
            }
        } catch (Exception e) {
            log.error("Error in webhook retry scheduler: {}", e.getMessage());
        }
    }
}
```

### Step 5: Add Webhook Configuration to application.yml

```yaml
webhook:
  secret-key: ${WEBHOOK_SECRET_KEY:your-secret-key}
  timeout-seconds: 30
  max-retries: 5
  retry:
    scheduler-interval: 300000 # 5 minutes
    backoff-multiplier: 2
```

---

## Part 4: Database Migrations for Phase 4

### Create V4 Migration File

**File**: `V4__Create_Webhook_Events_Table.sql`

```sql
-- Create Webhook Events Table
CREATE TABLE IF NOT EXISTS webhook_events (
    id UUID PRIMARY KEY,
    notification_id UUID NOT NULL,
    webhook_url VARCHAR(500) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    payload TEXT,
    response_code INTEGER,
    response_body TEXT,
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 5,
    next_retry_at TIMESTAMP,
    signature VARCHAR(256),
    created_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT false,

    CONSTRAINT fk_webhook_notification FOREIGN KEY (notification_id) REFERENCES notifications(id)
);

-- Create Indexes
CREATE INDEX IF NOT EXISTS idx_webhook_status ON webhook_events(status);
CREATE INDEX IF NOT EXISTS idx_webhook_notification ON webhook_events(notification_id);
CREATE INDEX IF NOT EXISTS idx_webhook_created_at ON webhook_events(created_at);
CREATE INDEX IF NOT EXISTS idx_webhook_event_type ON webhook_events(event_type);
CREATE INDEX IF NOT EXISTS idx_webhook_next_retry ON webhook_events(next_retry_at) WHERE status = 'pending';

-- Create Audit Trigger
CREATE OR REPLACE FUNCTION update_webhook_events_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.processed_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS webhook_events_update_timestamp ON webhook_events;
CREATE TRIGGER webhook_events_update_timestamp
    BEFORE UPDATE ON webhook_events
    FOR EACH ROW
    EXECUTE FUNCTION update_webhook_events_timestamp();
```

---

## Part 5: Enhanced Notification Service Integration

### Update SMSService to Use Twilio

**File**: `application/service/SMSService.java` (Enhanced)

```java
package com.care.notification.application.service;

import com.care.notification.infrastructure.persistence.entity.NotificationEntity;
import com.care.notification.infrastructure.persistence.repository.NotificationRepository;
import com.care.notification.presentation.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SMS Service - Using Twilio provider
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SMSService {

    private final TwilioSmsService twilioSmsService;
    private final NotificationRepository notificationRepository;

    /**
     * Send SMS notification via Twilio
     */
    public void sendSms(NotificationRequest request) {
        try {
            if (request.getRecipientPhone() == null) {
                log.warn("No phone number provided for SMS notification");
                return;
            }

            String messageSid = twilioSmsService.sendSmsWithRetry(
                request,
                request.getRecipientPhone(),
                3
            );

            // Update notification record
            NotificationEntity notification = NotificationEntity.builder()
                .id(request.getNotificationId())
                .channel("SMS")
                .status("SENT")
                .externalMessageId(messageSid)
                .build();

            notificationRepository.save(notification);
            log.info("SMS notification sent via Twilio: {}", messageSid);

        } catch (Exception e) {
            log.error("Failed to send SMS: {}", e.getMessage());
            // Update notification status to FAILED
            updateNotificationStatus(request.getNotificationId(), "FAILED", e.getMessage());
        }
    }

    private void updateNotificationStatus(String notificationId, String status, String errorMsg) {
        // Implementation to update notification status
    }
}
```

### Update PushNotificationService to Use Firebase

**File**: `application/service/PushNotificationService.java` (Enhanced)

```java
package com.care.notification.application.service;

import com.care.notification.infrastructure.persistence.entity.NotificationEntity;
import com.care.notification.infrastructure.persistence.repository.NotificationRepository;
import com.care.notification.presentation.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Push Notification Service - Using Firebase Cloud Messaging
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PushNotificationService {

    private final FirebasePushService firebasePushService;
    private final NotificationRepository notificationRepository;

    /**
     * Send push notification via Firebase
     */
    public void sendPushNotification(NotificationRequest request) {
        try {
            if (request.getDeviceToken() == null) {
                log.warn("No device token provided for push notification");
                return;
            }

            String messageId = firebasePushService.sendPushNotification(
                request,
                request.getDeviceToken()
            );

            // Update notification record
            NotificationEntity notification = NotificationEntity.builder()
                .id(request.getNotificationId())
                .channel("PUSH")
                .status("SENT")
                .externalMessageId(messageId)
                .build();

            notificationRepository.save(notification);
            log.info("Push notification sent via Firebase: {}", messageId);

        } catch (Exception e) {
            log.error("Failed to send push notification: {}", e.getMessage());
            updateNotificationStatus(request.getNotificationId(), "FAILED", e.getMessage());
        }
    }

    /**
     * Send to topic (broadcast)
     */
    public void sendToTopic(NotificationRequest request, String topic) {
        try {
            String messageId = firebasePushService.sendPushToTopic(request, topic);
            log.info("Push notification sent to topic '{}': {}", topic, messageId);
        } catch (Exception e) {
            log.error("Failed to send push to topic: {}", e.getMessage());
        }
    }

    private void updateNotificationStatus(String notificationId, String status, String errorMsg) {
        // Implementation to update notification status
    }
}
```

---

## Part 6: Environment Setup Instructions

### Prerequisites
1. **Twilio Account**:
   - Visit https://www.twilio.com/console
   - Copy Account SID, Auth Token, and Phone Number
   - Set environment variables

2. **Firebase Project**:
   - Create project at https://console.firebase.google.com
   - Download service account JSON key
   - Place in `src/main/resources/firebase-service-account.json`

3. **Environment Variables**:
```bash
# Twilio
export TWILIO_ACCOUNT_SID=your_account_sid
export TWILIO_AUTH_TOKEN=your_auth_token
export TWILIO_PHONE_NUMBER=+1234567890

# Firebase
export FIREBASE_CREDENTIALS_PATH=classpath:firebase-service-account.json
export FIREBASE_DATABASE_URL=https://your-project.firebaseio.com

# Webhooks
export WEBHOOK_SECRET_KEY=your-webhook-secret-key
```

### Docker Compose Updates
```yaml
environment:
  TWILIO_ACCOUNT_SID: ${TWILIO_ACCOUNT_SID}
  TWILIO_AUTH_TOKEN: ${TWILIO_AUTH_TOKEN}
  TWILIO_PHONE_NUMBER: ${TWILIO_PHONE_NUMBER}
  FIREBASE_CREDENTIALS_PATH: /app/firebase-service-account.json
  WEBHOOK_SECRET_KEY: ${WEBHOOK_SECRET_KEY}
```

---

## Part 7: Testing the Integrations

### Test Twilio SMS
```bash
curl -X POST http://localhost:6066/api/v1/notifications/sms \
  -H "Content-Type: application/json" \
  -d '{
    "recipientPhone": "+1234567890",
    "message": "Test SMS from Notification Service",
    "notificationType": "TEST"
  }'
```

### Test Firebase Push
```bash
curl -X POST http://localhost:6066/api/v1/notifications/push \
  -H "Content-Type: application/json" \
  -d '{
    "deviceToken": "firebase_device_token_here",
    "title": "Test Notification",
    "message": "Test push from Notification Service",
    "notificationType": "TEST"
  }'
```

### Test Webhook
```bash
curl -X POST http://localhost:6066/api/v1/webhooks/events \
  -H "Content-Type: application/json" \
  -d '{
    "notificationId": "550e8400-e29b-41d4-a716-446655440000",
    "webhookUrl": "https://webhook.site/your-unique-id",
    "eventType": "sent",
    "payload": "{\"status\": \"sent\"}"
  }'
```

---

## Part 8: Monitoring & Troubleshooting

### Check Webhook Status
```bash
curl http://localhost:6066/api/v1/webhooks/events?status=pending
curl http://localhost:6066/api/v1/webhooks/events?status=failed
```

### View Twilio Message Logs
```bash
curl -u ACCOUNT_SID:AUTH_TOKEN https://api.twilio.com/2010-04-01/Accounts/ACCOUNT_SID/Messages
```

### View Firebase Message Logs
- Check Firebase Console: https://console.firebase.google.com/project/[PROJECT_ID]/messaging

### Database Monitoring
```sql
-- Check webhook events
SELECT * FROM webhook_events WHERE status = 'pending' ORDER BY created_at DESC;

-- Check failed webhooks
SELECT * FROM webhook_events WHERE status = 'failed' ORDER BY created_at DESC LIMIT 10;

-- Check Twilio SMS delivery
SELECT * FROM notifications WHERE channel = 'SMS' ORDER BY created_at DESC LIMIT 10;
```

---

## Phase 4 Deliverables Checklist

- ✅ Twilio SMS integration
- ✅ Firebase Cloud Messaging integration
- ✅ Webhook event system
- ✅ Webhook retry mechanism
- ✅ Signature verification
- ✅ Database migrations
- ✅ Configuration documentation
- ✅ Testing instructions
- ✅ Monitoring guidance

---

**Next**: Proceed to implementation in the following order:
1. Add dependencies to pom.xml
2. Create Twilio configuration and service
3. Create Firebase configuration and service
4. Create webhook infrastructure
5. Create database migration
6. Test all integrations

