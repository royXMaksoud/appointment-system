package com.sharedlib.core.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ErrorResponse and ValidationError classes.
 */
class ErrorResponseTest {

    @Test
    void shouldCreateErrorResponse_WhenValidData() {
        // Given
        String code = "error.validation";
        String message = "Validation failed";
        int status = 400;
        LocalDateTime timestamp = LocalDateTime.now();
        String path = "/api/appointments";

        // When
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(code)
                .message(message)
                .status(status)
                .timestamp(timestamp)
                .path(path)
                .build();

        // Then
        assertNotNull(errorResponse);
        assertEquals(code, errorResponse.getCode());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(path, errorResponse.getPath());
        assertNull(errorResponse.getDetails());
    }

    @Test
    void shouldCreateErrorResponseWithValidationErrors_WhenValidData() {
        // Given
        String code = "error.validation";
        String message = "Validation failed";
        int status = 400;
        LocalDateTime timestamp = LocalDateTime.now();
        String path = "/api/appointments";
        
        List<ErrorResponse.ValidationError> validationErrors = Arrays.asList(
            ErrorResponse.ValidationError.builder()
                .field("email")
                .code("email.invalid")
                .message("Email must be valid")
                .build(),
            ErrorResponse.ValidationError.builder()
                .field("phoneNumber")
                .code("phone.required")
                .message("Phone number is required")
                .build()
        );

        // When
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(code)
                .message(message)
                .status(status)
                .timestamp(timestamp)
                .path(path)
                .details(validationErrors)
                .build();

        // Then
        assertNotNull(errorResponse);
        assertEquals(code, errorResponse.getCode());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(path, errorResponse.getPath());
        assertNotNull(errorResponse.getDetails());
        assertEquals(2, errorResponse.getDetails().size());
        
        // Check first validation error
        ErrorResponse.ValidationError firstError = errorResponse.getDetails().get(0);
        assertEquals("email", firstError.getField());
        assertEquals("email.invalid", firstError.getCode());
        assertEquals("Email must be valid", firstError.getMessage());
        
        // Check second validation error
        ErrorResponse.ValidationError secondError = errorResponse.getDetails().get(1);
        assertEquals("phoneNumber", secondError.getField());
        assertEquals("phone.required", secondError.getCode());
        assertEquals("Phone number is required", secondError.getMessage());
    }

    @Test
    void shouldCreateValidationError_WhenValidData() {
        // Given
        String field = "email";
        String code = "email.invalid";
        String message = "Email must be valid";

        // When
        ErrorResponse.ValidationError validationError = ErrorResponse.ValidationError.builder()
                .field(field)
                .code(code)
                .message(message)
                .build();

        // Then
        assertNotNull(validationError);
        assertEquals(field, validationError.getField());
        assertEquals(code, validationError.getCode());
        assertEquals(message, validationError.getMessage());
    }

    @Test
    void shouldCreateValidationErrorWithNullValues_WhenValidData() {
        // Given
        String field = "email";
        String code = null;
        String message = null;

        // When
        ErrorResponse.ValidationError validationError = ErrorResponse.ValidationError.builder()
                .field(field)
                .code(code)
                .message(message)
                .build();

        // Then
        assertNotNull(validationError);
        assertEquals(field, validationError.getField());
        assertNull(validationError.getCode());
        assertNull(validationError.getMessage());
    }

    @Test
    void shouldCreateErrorResponseWithEmptyValidationErrors_WhenValidData() {
        // Given
        String code = "error.validation";
        String message = "Validation failed";
        int status = 400;
        LocalDateTime timestamp = LocalDateTime.now();
        String path = "/api/appointments";
        List<ErrorResponse.ValidationError> validationErrors = Arrays.asList();

        // When
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(code)
                .message(message)
                .status(status)
                .timestamp(timestamp)
                .path(path)
                .details(validationErrors)
                .build();

        // Then
        assertNotNull(errorResponse);
        assertEquals(code, errorResponse.getCode());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(path, errorResponse.getPath());
        assertNotNull(errorResponse.getDetails());
        assertEquals(0, errorResponse.getDetails().size());
    }

    @Test
    void shouldCreateErrorResponseWithNullValidationErrors_WhenValidData() {
        // Given
        String code = "error.validation";
        String message = "Validation failed";
        int status = 400;
        LocalDateTime timestamp = LocalDateTime.now();
        String path = "/api/appointments";

        // When
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(code)
                .message(message)
                .status(status)
                .timestamp(timestamp)
                .path(path)
                .details(null)
                .build();

        // Then
        assertNotNull(errorResponse);
        assertEquals(code, errorResponse.getCode());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(path, errorResponse.getPath());
        assertNull(errorResponse.getDetails());
    }

    @Test
    void shouldCreateValidationErrorWithSpecialCharacters_WhenValidData() {
        // Given
        String field = "user_name";
        String code = "user.name.invalid";
        String message = "اسم المستخدم غير صحيح";

        // When
        ErrorResponse.ValidationError validationError = ErrorResponse.ValidationError.builder()
                .field(field)
                .code(code)
                .message(message)
                .build();

        // Then
        assertNotNull(validationError);
        assertEquals(field, validationError.getField());
        assertEquals(code, validationError.getCode());
        assertEquals(message, validationError.getMessage());
    }

    @Test
    void shouldCreateErrorResponseWithMultipleValidationErrors_WhenValidData() {
        // Given
        String code = "error.validation";
        String message = "Multiple validation errors";
        int status = 400;
        LocalDateTime timestamp = LocalDateTime.now();
        String path = "/api/appointments";
        
        List<ErrorResponse.ValidationError> validationErrors = Arrays.asList(
            ErrorResponse.ValidationError.builder()
                .field("email")
                .code("email.invalid")
                .message("Email must be valid")
                .build(),
            ErrorResponse.ValidationError.builder()
                .field("phoneNumber")
                .code("phone.required")
                .message("Phone number is required")
                .build(),
            ErrorResponse.ValidationError.builder()
                .field("age")
                .code("age.min")
                .message("Age must be at least 18")
                .build(),
            ErrorResponse.ValidationError.builder()
                .field("password")
                .code("password.weak")
                .message("Password is too weak")
                .build()
        );

        // When
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(code)
                .message(message)
                .status(status)
                .timestamp(timestamp)
                .path(path)
                .details(validationErrors)
                .build();

        // Then
        assertNotNull(errorResponse);
        assertEquals(code, errorResponse.getCode());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(path, errorResponse.getPath());
        assertNotNull(errorResponse.getDetails());
        assertEquals(4, errorResponse.getDetails().size());
        
        // Check all validation errors
        assertEquals("email", errorResponse.getDetails().get(0).getField());
        assertEquals("phoneNumber", errorResponse.getDetails().get(1).getField());
        assertEquals("age", errorResponse.getDetails().get(2).getField());
        assertEquals("password", errorResponse.getDetails().get(3).getField());
    }
} 