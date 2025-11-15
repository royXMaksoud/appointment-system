# Notification Service Documentation

## Overview
The Notification Service is a dedicated microservice for handling multi-channel notifications across the Care Management System. It provides a centralized, reusable notification system that can be accessed by any microservice via REST API.

**Port**: 6067

## Architecture

### Purpose
- **Centralized Notifications**: Single service for all notification operations
- **Multi-Channel Support**: Email, SMS, and Push Notifications
- **Service Reusability**: Any microservice can call this service
- **Smart Routing**: Intelligent channel selection based on user preferences
- **Resilience**: Circuit breaker and retry patterns for reliability

### Technology Stack
- **Framework**: Spring Boot 3.4.5
- **Discovery**: Netflix Eureka
- **Config**: Spring Cloud Config
- **Messaging**: JavaMail (SMTP), Twilio/AWS SNS (SMS), Firebase/APNs (Push)
- **Resilience**: Resilience4j with Circuit Breaker, Retry, Rate Limiter

## Project Structure

```
notification-service/
├── src/
│   ├── main/
│   │   ├── java/com/care/notification/
│   │   │   ├── NotificationServiceApplication.java      # Spring Boot app
│   │   │   ├── application/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── NotificationRequest.java         # Request DTO
│   │   │   │   │   ├── NotificationResult.java          # Response DTO
│   │   │   │   │   └── AppointmentQRDTO.java            # QR code data
│   │   │   │   └── service/
│   │   │   │       ├── NotificationService.java         # Main service
│   │   │   │       ├── EmailService.java                # Email sender
│   │   │   │       ├── SMSService.java                  # SMS sender
│   │   │   │       └── PushNotificationService.java     # Push notifications
│   │   │   ├── presentation/
│   │   │   │   └── controller/
│   │   │   │       └── NotificationController.java      # REST endpoints
│   │   │   └── infrastructure/
│   │   │       └── (database entities if needed)
│   │   └── resources/
│   │       ├── application.yml                          # Main config
│   │       └── bootstrap.yml                            # Eureka config
│   └── test/
├── pom.xml                                              # Maven dependencies
└── CLAUDE.md                                            # This file
```

## Key Components

### 1. NotificationService (Core Service)
**Location**: `application/service/NotificationService.java`

Handles intelligent notification routing:
- Determines available channels based on user preferences and device status
- Falls back to alternative channels if primary fails
- Supports multiple notification types (appointment created, reminder, cancelled, QR resend)

**Key Methods**:
```java
NotificationResult notifyAppointmentCreated(NotificationRequest request)
NotificationResult notifyAppointmentReminder(NotificationRequest request)
NotificationResult notifyAppointmentCancelled(NotificationRequest request)
NotificationResult resendQRCode(NotificationRequest request)
```

### 2. EmailService
**Location**: `application/service/EmailService.java`

Sends HTML-formatted emails with:
- RTL support for Arabic
- Appointment details and QR codes
- Professional templates with verification codes

**Configuration** (application.yml):
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
```

### 3. SMSService
**Location**: `application/service/SMSService.java`

Sends SMS notifications for:
- Appointment creation
- Appointment reminders
- Appointment cancellations
- QR code resending

**Configuration** (application.yml):
```yaml
app:
  notification:
    sms:
      enabled: false  # Set to true when provider configured
      provider: twilio
```

**TODO**: Integrate with Twilio or AWS SNS

### 4. PushNotificationService
**Location**: `application/service/PushNotificationService.java`

Sends push notifications to mobile apps:
- Firebase Cloud Messaging (Android)
- Apple Push Notifications (iOS)
- Device tracking and status management

**Configuration** (application.yml):
```yaml
app:
  notification:
    push:
      enabled: false  # Set to true when Firebase/APNs configured
      provider: firebase
```

**TODO**: Integrate Firebase Admin SDK or APNs certificates

### 5. NotificationController (REST API)
**Location**: `presentation/controller/NotificationController.java`

Exposes REST endpoints for other services:

```
POST   /api/v1/notifications/appointment-created
POST   /api/v1/notifications/appointment-reminder
POST   /api/v1/notifications/appointment-cancelled
POST   /api/v1/notifications/resend-qr
GET    /api/v1/notifications/health
```

## DTOs (Data Transfer Objects)

### NotificationRequest
```java
{
  "beneficiary_id": "uuid",
  "mobile_number": "+966501234567",
  "email": "user@example.com",
  "device_id": "FCM_TOKEN_OR_DEVICE_ID",
  "has_installed_mobile_app": true,
  "preferred_channel": "SMS",  // SMS, EMAIL, or PUSH
  "notification_type": "APPOINTMENT_CREATED",
  "appointment_qr": { ... },
  "cancellation_reason": "Optional reason"
}
```

### NotificationResult
```java
{
  "channel": "EMAIL",
  "success": true,
  "error_message": null,
  "sent_at": 1700000000000
}
```

### AppointmentQRDTO
```java
{
  "appointment_id": "uuid",
  "appointment_code": "2025-HQ-0001",
  "qr_code_url": "data:image/png;base64,...",
  "verification_code": "4-2-7",
  "verification_code_expires_at": "2025-01-15T10:30:00Z",
  "appointment_date": "2025-01-16",
  "appointment_time": "14:30",
  "beneficiary_name": "John Doe",
  "service_type": "Medical Checkup",
  "center_name": "Main Health Center"
}
```

## Inter-Service Communication

### From Appointment Service

The appointment-service uses **OpenFeign** to call notification-service:

```java
// appointment-service/infrastructure/client/NotificationClient.java
@FeignClient(name = "notification-service", url = "http://localhost:6067")
public interface NotificationClient {
    @PostMapping("/api/v1/notifications/appointment-created")
    NotificationResult notifyAppointmentCreated(@RequestBody NotificationRequest request);
    // ... other methods
}
```

**Usage in Appointment Service**:
```java
@RequiredArgsConstructor
public class AppointmentController {
    private final NotificationClient notificationClient;

    @PostMapping
    public void createAppointment(AppointmentRequest request) {
        // Create appointment...

        // Send notification
        NotificationRequest notifRequest = NotificationRequest.builder()
            .beneficiaryId(beneficiary.getId())
            .mobileNumber(beneficiary.getMobileNumber())
            .email(beneficiary.getEmail())
            .deviceId(beneficiary.getDeviceId())
            .preferredChannel("SMS")
            .notificationType(NotificationType.APPOINTMENT_CREATED)
            .appointmentQR(appointmentQRDTO)
            .build();

        notificationClient.notifyAppointmentCreated(notifRequest);
    }
}
```

## Configuration

### application.yml (Port 6067)
- **Eureka Registration**: Automatically registers on startup
- **Config Server**: Fetches settings from config-server (8888)
- **Database**: PostgreSQL with HikariCP connection pooling
- **Mail**: SMTP configuration for email sending
- **Resilience**: Circuit breaker for notification failures

### Profiles
- **dev** (default): Local development on localhost
- **docker**: Container environment with service names
- **prod**: Production with environment variables

## Resilience Patterns

### Circuit Breaker
Prevents cascading failures when services are down:
- **CLOSED**: Normal operation
- **OPEN**: Service failing, requests rejected immediately
- **HALF_OPEN**: Testing if service recovered

### Retry Logic
Automatically retries failed notifications:
- Initial delay: 100ms
- Max retries: 3
- Backoff multiplier: 1.5

### Rate Limiter
Prevents overloading notification service:
- Configured per microservice
- Smooth rate limiting

## Running the Service

### Local Development
```bash
# Terminal 1: Start Eureka
cd ../service-registry
mvn spring-boot:run

# Terminal 2: Start Config Server
cd ../config-server
mvn spring-boot:run

# Terminal 3: Start Notification Service
cd notification-service
mvn spring-boot:run
```

Service will be available at: `http://localhost:6067`

### Docker Compose
```bash
docker-compose up notification-service
```

## Testing the API

### Send Appointment Created Notification
```bash
curl -X POST http://localhost:6067/api/v1/notifications/appointment-created \
  -H "Content-Type: application/json" \
  -d '{
    "beneficiary_id": "550e8400-e29b-41d4-a716-446655440000",
    "mobile_number": "+966501234567",
    "email": "user@example.com",
    "device_id": "FCM_TOKEN",
    "has_installed_mobile_app": true,
    "preferred_channel": "SMS",
    "notification_type": "APPOINTMENT_CREATED",
    "appointment_qr": {
      "appointment_id": "550e8400-e29b-41d4-a716-446655440001",
      "appointment_code": "2025-HQ-0001",
      "qr_code_url": "data:image/png;base64,...",
      "verification_code": "4-2-7",
      "appointment_date": "2025-01-16",
      "appointment_time": "14:30",
      "beneficiary_name": "John Doe",
      "service_type": "Medical Checkup",
      "center_name": "Main Health Center"
    }
  }'
```

### Health Check
```bash
curl http://localhost:6067/api/v1/notifications/health
```

## Integration with Other Services

### Reference Data Service
- Fetches organization branch codes for appointment codes
- Used indirectly through appointment-service

### Appointment Service
- Primary consumer of notification-service
- Sends notifications when appointments are created/modified
- Uses OpenFeign client

### Future Integrations
- User Service: Send user notifications
- Data Analysis Service: Notification reports
- Chatbot Service: Notify users via chatbot

## Common Issues and Solutions

### Email Not Sending
1. Check Gmail settings for "Less secure app access"
2. Use App Password instead of account password
3. Verify SMTP credentials in application.yml

### SMS Not Sending
1. Implement Twilio or AWS SNS integration
2. Verify phone numbers are in E.164 format (+966XXXXXXXXX)
3. Check provider account balance

### Push Notifications Not Working
1. Configure Firebase Admin SDK credentials
2. Add APNs certificates for iOS
3. Verify device tokens are valid

### Circuit Breaker Open
1. Check notification provider status
2. Review logs for specific errors
3. Wait for half-open state to test recovery
4. Increase timeout if notifications are slow

## Monitoring

### Health Endpoints
- `/actuator/health` - Service health
- `/actuator/health/liveness` - Liveness probe
- `/actuator/health/readiness` - Readiness probe

### Metrics
- `/actuator/metrics` - Prometheus metrics
- Circuit breaker state
- Notification send success rates

### Logging
- Default level: INFO
- Application level: DEBUG
- Configure in application.yml

## Future Enhancements

1. **Notification Templates**: Customizable email/SMS templates
2. **Scheduling**: Delayed/scheduled notifications
3. **Notification History**: Track all sent notifications
4. **User Preferences**: More granular notification preferences
5. **Multilingual Support**: Auto-detect user language
6. **Notification Analytics**: Track open rates, click rates
7. **Webhook Support**: Send notifications to external systems
8. **Queue-Based Processing**: Use message queues for async processing

## Dependencies

See `pom.xml` for complete list. Key dependencies:
- spring-boot-starter-mail: Email support
- spring-boot-starter-web: REST API
- spring-cloud-starter-openfeign: Inter-service calls
- resilience4j: Fault tolerance
- spring-cloud-starter-netflix-eureka-client: Service discovery

## Security

- JWT validation on incoming requests (inherited from shared config)
- Sensitive data (phone, email) never logged
- SMTP credentials stored in environment variables
- API endpoints protected by Spring Security

## Contributing

When modifying notification-service:
1. Update DTOs if request/response changes
2. Add new notification types to NotificationType enum
3. Implement new channel methods following existing patterns
4. Update documentation
5. Add unit tests for new features
6. Test inter-service calls from appointment-service

