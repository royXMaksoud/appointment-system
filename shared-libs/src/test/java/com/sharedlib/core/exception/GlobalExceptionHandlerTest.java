package com.sharedlib.core.exception;

import com.sharedlib.core.context.LanguageContext;
import com.sharedlib.core.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GlobalExceptionHandler} class.
 * Tests cover exception handling, error response generation, and i18n support.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ExceptionTestConfig.class})
@TestPropertySource(properties = {
    "spring.messages.basename=i18n/messages",
    "spring.messages.encoding=UTF-8"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private GlobalExceptionHandler exceptionHandler;

    @Autowired
    private MessageSource messageSource;

    @Mock
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Set up mock request
        when(mockRequest.getRequestURI()).thenReturn("/api/test");
        
        // Set default language context
        LanguageContext.setLanguage("en");
    }

    @AfterEach
    void tearDown() {
        // Clean up language context
        LanguageContext.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Should return localized error response for NotFoundException")
    void shouldReturnLocalizedErrorResponseForNotFoundException() {
        // Given
        String errorMessage = "User not found";
        NotFoundException notFoundException = new NotFoundException(errorMessage);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFound(notFoundException, mockRequest);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Status should be NOT_FOUND");
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response body should not be null");
        assertEquals("error.notfound", errorResponse.getCode(), "Error code should match");
        assertEquals(errorMessage, errorResponse.getMessage(), "Error message should match");
        assertEquals(404, errorResponse.getStatus(), "Status code should be 404");
        assertEquals("/api/test", errorResponse.getPath(), "Request path should match");
        assertNotNull(errorResponse.getTimestamp(), "Timestamp should not be null");
        assertTrue(errorResponse.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)), 
                  "Timestamp should be recent");
    }

    @Test
    @Order(2)
    @DisplayName("Should return localized error response for BadRequestException")
    void shouldReturnLocalizedErrorResponseForBadRequestException() {
        // Given
        String errorMessage = "Invalid input data";
        BadRequestException badRequestException = new BadRequestException(errorMessage);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadRequest(badRequestException, mockRequest);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST");
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response body should not be null");
        assertEquals("error.badrequest", errorResponse.getCode(), "Error code should match");
        assertEquals(errorMessage, errorResponse.getMessage(), "Error message should match");
        assertEquals(400, errorResponse.getStatus(), "Status code should be 400");
        assertEquals("/api/test", errorResponse.getPath(), "Request path should match");
        assertNotNull(errorResponse.getTimestamp(), "Timestamp should not be null");
    }

    @Test
    @Order(3)
    @DisplayName("Should return localized error for MessageResolvableException")
    void shouldReturnLocalizedErrorForMessageResolvableException() {
        // Given
        String messageKey = "error.user.notfound";
        Object[] args = {"12345"};
        MessageResolvableException resolvableException = new MessageResolvableException(messageKey, args);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResolvable(resolvableException, mockRequest);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST");
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response body should not be null");
        assertEquals(messageKey, errorResponse.getCode(), "Error code should match message key");
        
        // Verify localized message
        String expectedLocalizedMessage = messageSource.getMessage(messageKey, args, 
                                                                  java.util.Locale.forLanguageTag("en"));
        assertEquals(expectedLocalizedMessage, errorResponse.getMessage(), "Localized message should match");
        assertEquals(400, errorResponse.getStatus(), "Status code should be 400");
    }

    @Test
    @Order(4)
    @DisplayName("Should handle generic Exception gracefully")
    void shouldHandleGenericExceptionGracefully() {
        // Given
        String errorMessage = "Unexpected error occurred";
        Exception genericException = new RuntimeException(errorMessage);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGeneric(genericException, mockRequest);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Status should be INTERNAL_SERVER_ERROR");
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response body should not be null");
        assertEquals("error.internal", errorResponse.getCode(), "Error code should be error.internal");
        assertEquals(errorMessage, errorResponse.getMessage(), "Error message should match");
        assertEquals(500, errorResponse.getStatus(), "Status code should be 500");
    }

    @Test
    @Order(5)
    @DisplayName("Should handle MessageResolvableException with null arguments")
    void shouldHandleMessageResolvableExceptionWithNullArguments() {
        // Given
        String messageKey = "error.badrequest";
        MessageResolvableException resolvableException = new MessageResolvableException(messageKey);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResolvable(resolvableException, mockRequest);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST");
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response body should not be null");
        assertEquals(messageKey, errorResponse.getCode(), "Error code should match message key");
        
        // Verify localized message without arguments
        String expectedLocalizedMessage = messageSource.getMessage(messageKey, null, 
                                                                  java.util.Locale.forLanguageTag("en"));
        assertEquals(expectedLocalizedMessage, errorResponse.getMessage(), "Localized message should match");
    }

    @Test
    @Order(6)
    @DisplayName("Should handle UnauthorizedException")
    void shouldHandleUnauthorizedException() {
        // Given
        String errorMessage = "Access denied";
        UnauthorizedException unauthorizedException = new UnauthorizedException(errorMessage);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnauthorized(unauthorizedException, mockRequest);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), "Status should be UNAUTHORIZED");
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response body should not be null");
        assertEquals("error.unauthorized", errorResponse.getCode(), "Error code should match");
        assertEquals(errorMessage, errorResponse.getMessage(), "Error message should match");
        assertEquals(401, errorResponse.getStatus(), "Status code should be 401");
    }

    @Test
    @Order(7)
    @DisplayName("Should handle exception with null request")
    void shouldHandleExceptionWithNullRequest() {
        // Given
        String errorMessage = "Test error";
        NotFoundException notFoundException = new NotFoundException(errorMessage);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFound(notFoundException, null);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Status should be NOT_FOUND");
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response body should not be null");
        assertEquals("error.notfound", errorResponse.getCode(), "Error code should match");
        assertEquals(errorMessage, errorResponse.getMessage(), "Error message should match");
        assertNull(errorResponse.getPath(), "Path should be null when request is null");
    }

    @Test
    @Order(8)
    @DisplayName("Should handle MessageResolvableException with multiple arguments")
    void shouldHandleMessageResolvableExceptionWithMultipleArguments() {
        // Given
        String messageKey = "validation.invalid.enum.value";
        Object[] args = {"status", "ACTIVE,INACTIVE"};
        MessageResolvableException resolvableException = new MessageResolvableException(messageKey, args);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResolvable(resolvableException, mockRequest);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST");
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response body should not be null");
        assertEquals(messageKey, errorResponse.getCode(), "Error code should match message key");
        
        // Verify localized message with multiple arguments
        String expectedLocalizedMessage = messageSource.getMessage(messageKey, args, 
                                                                  java.util.Locale.forLanguageTag("en"));
        assertEquals(expectedLocalizedMessage, errorResponse.getMessage(), "Localized message should match");
    }

    @Test
    @Order(9)
    @DisplayName("Should handle exception with different language context")
    void shouldHandleExceptionWithDifferentLanguageContext() {
        // Given
        LanguageContext.setLanguage("ar");
        String messageKey = "error.badrequest";
        MessageResolvableException resolvableException = new MessageResolvableException(messageKey);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResolvable(resolvableException, mockRequest);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST");
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response body should not be null");
        assertEquals(messageKey, errorResponse.getCode(), "Error code should match message key");
        
        // Verify localized message in Arabic
        String expectedLocalizedMessage = messageSource.getMessage(messageKey, null, 
                                                                  java.util.Locale.forLanguageTag("ar"));
        assertEquals(expectedLocalizedMessage, errorResponse.getMessage(), "Localized message should match Arabic");
    }

    @Test
    @Order(10)
    @DisplayName("Should handle exception with empty request URI")
    void shouldHandleExceptionWithEmptyRequestUri() {
        // Given
        HttpServletRequest emptyRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(emptyRequest.getRequestURI()).thenReturn("");
        
        String errorMessage = "Test error";
        BadRequestException badRequestException = new BadRequestException(errorMessage);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadRequest(badRequestException, emptyRequest);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST");
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response body should not be null");
        assertEquals("error.badrequest", errorResponse.getCode(), "Error code should match");
        assertEquals(errorMessage, errorResponse.getMessage(), "Error message should match");
        assertEquals("", errorResponse.getPath(), "Path should be empty string");
    }

    @Test
    @Order(11)
    @DisplayName("Should handle exception with complex request URI")
    void shouldHandleExceptionWithComplexRequestUri() {
        // Given
        HttpServletRequest complexRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(complexRequest.getRequestURI()).thenReturn("/api/users/12345/profile?include=details");
        
        String errorMessage = "User not found";
        NotFoundException notFoundException = new NotFoundException(errorMessage);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFound(notFoundException, complexRequest);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Status should be NOT_FOUND");
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response body should not be null");
        assertEquals("error.notfound", errorResponse.getCode(), "Error code should match");
        assertEquals(errorMessage, errorResponse.getMessage(), "Error message should match");
        assertEquals("/api/users/12345/profile?include=details", errorResponse.getPath(), "Path should match complex URI");
    }

    @Test
    @Order(12)
    @DisplayName("Should handle exception with null message")
    void shouldHandleExceptionWithNullMessage() {
        // Given
        String errorMessage = null;
        NotFoundException notFoundException = new NotFoundException(errorMessage);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFound(notFoundException, mockRequest);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Status should be NOT_FOUND");
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response body should not be null");
        assertEquals("error.notfound", errorResponse.getCode(), "Error code should match");
        assertNull(errorResponse.getMessage(), "Error message should be null");
        assertEquals(404, errorResponse.getStatus(), "Status code should be 404");
    }


} 