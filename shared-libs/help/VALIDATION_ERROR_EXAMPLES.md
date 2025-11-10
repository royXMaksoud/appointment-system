# ValidationError Examples

## Overview
The `ErrorResponse` now supports field-specific validation errors through the `ValidationError` inner class and `details` field.

## Structure

### ErrorResponse with ValidationError
```java
{
  "code": "error.validation",
  "message": "Validation failed",
  "status": 400,
  "timestamp": "2025-08-05T17:58:35",
  "path": "/api/appointments",
  "details": [
    {
      "field": "email",
      "message": "Email must be valid",
      "code": "email.invalid"
    },
    {
      "field": "phoneNumber",
      "message": "Phone number is required",
      "code": "phone.required"
    }
  ]
}
```

## Usage Examples

### 1. Spring Validation (@Valid)
```java
@PostMapping("/appointments")
public ResponseEntity<AppointmentDto> createAppointment(
    @Valid @RequestBody CreateAppointmentRequest request) {
    // If validation fails, GlobalExceptionHandler automatically creates
    // ErrorResponse with field-specific details
}
```

### 2. Manual Validation with ValidationException
```java
@Service
public class AppointmentService {
    
    public void validateAppointment(CreateAppointmentRequest request) {
        List<ErrorResponse.ValidationError> errors = new ArrayList<>();
        
        if (request.getEmail() == null || !isValidEmail(request.getEmail())) {
            errors.add(ErrorResponse.ValidationError.builder()
                .field("email")
                .code("email.invalid")
                .message("Email must be valid")
                .build());
        }
        
        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            errors.add(ErrorResponse.ValidationError.builder()
                .field("phoneNumber")
                .code("phone.required")
                .message("Phone number is required")
                .build());
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Appointment validation failed", errors);
        }
    }
}
```

### 3. Internationalized Validation Messages
```java
@Service
public class AppointmentService {
    
    public void validateAppointment(CreateAppointmentRequest request) {
        List<ErrorResponse.ValidationError> errors = new ArrayList<>();
        
        if (request.getEmail() == null || !isValidEmail(request.getEmail())) {
            String message = messageSource.getMessage(
                "validation.email.invalid", 
                null, 
                getLocale()
            );
            
            errors.add(ErrorResponse.ValidationError.builder()
                .field("email")
                .code("email.invalid")
                .message(message)
                .build());
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("validation.appointment.failed", errors);
        }
    }
}
```

## Message Properties

### messages_en.properties
```properties
validation.email.invalid=Email must be valid
validation.phone.required=Phone number is required
validation.appointment.failed=Appointment validation failed
```

### messages_ar.properties
```properties
validation.email.invalid=يجب أن يكون البريد الإلكتروني صحيحاً
validation.phone.required=رقم الهاتف مطلوب
validation.appointment.failed=فشل في التحقق من صحة الموعد
```

## Benefits

### 1. Field-Specific Errors
- Each field has its own error message
- Clear indication of which fields failed validation
- Better user experience

### 2. Internationalization Support
- Error messages can be localized
- Consistent with the rest of the application
- Supports multiple languages

### 3. Consistent Error Structure
- All validation errors follow the same format
- Easy to parse on the client side
- Standardized across all services

### 4. Spring Integration
- Automatic handling of `@Valid` annotations
- No additional code needed for basic validation
- Works with existing Spring validation

## Testing Examples

### Unit Test for ValidationException
```java
@Test
void shouldThrowValidationExceptionWithFieldErrors() {
    // Given
    List<ErrorResponse.ValidationError> errors = Arrays.asList(
        ErrorResponse.ValidationError.builder()
            .field("email")
            .code("email.invalid")
            .message("Email must be valid")
            .build()
    );
    
    // When & Then
    ValidationException exception = assertThrows(
        ValidationException.class,
        () -> {
            throw new ValidationException("Validation failed", errors);
        }
    );
    
    assertTrue(exception.hasFieldErrors());
    assertEquals(1, exception.getFieldErrors().size());
    assertEquals("email", exception.getFieldErrors().get(0).getField());
}
```

### Integration Test
```java
@Test
void shouldReturnValidationErrorsForInvalidRequest() throws Exception {
    // Given
    CreateAppointmentRequest request = new CreateAppointmentRequest();
    request.setEmail("invalid-email");
    // phoneNumber is null
    
    // When
    MvcResult result = mockMvc.perform(post("/api/appointments")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andReturn();
    
    // Then
    ErrorResponse errorResponse = objectMapper.readValue(
        result.getResponse().getContentAsString(), 
        ErrorResponse.class
    );
    
    assertEquals("error.validation", errorResponse.getCode());
    assertNotNull(errorResponse.getDetails());
    assertEquals(2, errorResponse.getDetails().size());
    
    // Check email error
    ErrorResponse.ValidationError emailError = errorResponse.getDetails()
        .stream()
        .filter(e -> "email".equals(e.getField()))
        .findFirst()
        .orElse(null);
    
    assertNotNull(emailError);
    assertEquals("email.invalid", emailError.getCode());
}
``` 