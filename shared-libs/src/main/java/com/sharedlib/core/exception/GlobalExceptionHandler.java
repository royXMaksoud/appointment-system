package com.sharedlib.core.exception;

import com.sharedlib.core.context.LanguageContext;
import com.sharedlib.core.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Global exception handler for handling all thrown exceptions across the app.
 * It converts exceptions into consistent error responses, supporting i18n.
 *
 * Notes:
 * - Always supply a defaultMessage to messageSource.getMessage(...) to prevent NoSuchMessageException -> 500.
 * - ValidationException and MethodArgumentNotValidException must return 400 with field-level details when available.
 * - DataIntegrityViolationException returns 409 and tries to localize by constraint name + extract conflicting value.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        // Use provided message as-is; code should already be meaningful (client error)
        return buildResponse(ex.getErrorCode(), ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        return buildResponse(ex.getErrorCode(), ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(MessageResolvableException.class)
    public ResponseEntity<ErrorResponse> handleResolvable(MessageResolvableException ex, HttpServletRequest request) {
        // Provide a safe defaultMessage fallback to avoid 500 if key missing
        String localizedMessage = messageSource.getMessage(
                ex.getMessageKey(),
                ex.getArgs(),
                ex.getMessage(),           // default message fallback
                getLocale()
        );
        return buildResponse(ex.getMessageKey(), localizedMessage, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex, HttpServletRequest request) {
        if (ex.hasFieldErrors()) {
            // Top-level generic validation message (safe fallback)
            String top = messageSource.getMessage(
                    "error.validation",
                    null,
                    "Validation error",
                    getLocale()
            );

            // Localize individual field errors:
            List<ErrorResponse.ValidationError> localized = ex.getFieldErrors().stream().map(ve -> {
                String code = ve.getCode();
                String msg  = ve.getMessage();

                // If message is blank or equals the code (still a key), resolve it from bundles
                if (msg == null || msg.isBlank() || (code != null && msg.equals(code))) {
                    String resolved = messageSource.getMessage(
                            (code != null ? code : "error.validation"),
                            null,
                            (code != null ? code : "Validation error"), // default fallback
                            getLocale()
                    );
                    return ErrorResponse.ValidationError.builder()
                            .field(ve.getField())
                            .code(code)
                            .message(resolved)
                            .build();
                }
                return ve;
            }).toList();

            ErrorResponse body = ErrorResponse.builder()
                    .code(ex.getMessageKey())
                    .message(top)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .details(localized)
                    .build();

            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        // No field errors -> resolve top-level message safely
        String top = messageSource.getMessage(
                ex.getMessageKey(),
                null,
                ex.getMessage() != null ? ex.getMessage() : "Validation error",
                getLocale()
        );
        return buildResponse(ex.getMessageKey(), top, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Handles Spring's validation errors (from @Valid annotations).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::buildValidationError)
                .collect(Collectors.toList());

        String top = messageSource.getMessage(
                "error.validation",
                null,
                "Validation error",
                getLocale()
        );

        ErrorResponse error = ErrorResponse.builder()
                .code("error.validation")
                .message(top)
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .details(validationErrors)
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle database integrity violations (e.g., unique constraint).
     * Returns 409 and tries to derive a localized message from constraint name.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        String messageKey = "error.data.integrity";
        String message = (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());

        // Try to unwrap Hibernate's ConstraintViolationException to get constraint name
        ConstraintViolationException hibernateCve = findCause(ex, ConstraintViolationException.class);
        String constraintName = (hibernateCve != null ? hibernateCve.getConstraintName() : null);

        // Map known DB constraint names to i18n message keys
        Map<String, String> constraintToKey = Map.of(
                "ukbn8tp6d0jcwd9njtpcm0u9qfu", "error.system.code.duplicate" // systems.code unique
        );
        if (constraintName != null && constraintToKey.containsKey(constraintName)) {
            messageKey = constraintToKey.get(constraintName);
        }

        // Try to extract the conflicting value from Postgres message: "Key (code)=(Test) already exists."
        String conflictingValue = extractPgKeyValue(message);
        String localized = messageSource.getMessage(
                messageKey,
                (conflictingValue != null ? new Object[]{conflictingValue} : null),
                "Data integrity violation",
                getLocale()
        );

        return buildResponse(messageKey, localized, HttpStatus.CONFLICT, request);
    }

    @SuppressWarnings("unchecked")
    private <T extends Throwable> T findCause(Throwable ex, Class<T> type) {
        Throwable cur = ex;
        while (cur != null) {
            if (type.isInstance(cur)) return (T) cur;
            cur = cur.getCause();
        }
        return null;
    }

    private String extractPgKeyValue(String pgMessage) {
        if (pgMessage == null) return null;
        Pattern p = Pattern.compile("Key \\([^)]*\\)=\\(([^)]*)\\) already exists", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(pgMessage);
        return m.find() ? m.group(1) : null;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        // Keep a generic code; message will be exception message (useful in dev). Consider logging stack trace.
        return buildResponse("error.internal", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Handles unauthorized access exceptions.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        // Avoid printing stacktrace in production; prefer logging if needed.
        return buildResponse("error.unauthorized", ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
    }

    /**
     * Builds a standardized error response.
     */
    private ResponseEntity<ErrorResponse> buildResponse(String code, String message, HttpStatus status, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .code(code)
                .message(message)
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .path(request != null ? request.getRequestURI() : null)
                .build();
        return new ResponseEntity<>(error, status);
    }

    /**
     * Builds a validation error from Spring's FieldError.
     */
    private ErrorResponse.ValidationError buildValidationError(FieldError fieldError) {
        return ErrorResponse.ValidationError.builder()
                .field(fieldError.getField())
                .code(fieldError.getCode())
                .message(fieldError.getDefaultMessage())
                .build();
    }

    /**
     * Resolves the current locale from LanguageContext.
     */
    private Locale getLocale() {
        String lang = LanguageContext.getLanguage();
        if (lang == null || lang.isBlank()) {
            return Locale.ENGLISH;
        }
        // normalize: "EN", "en_US" -> "en-US"
        String norm = lang.trim().replace('_', '-');
        Locale loc = Locale.forLanguageTag(norm);
        return (loc == null || loc.getLanguage() == null || loc.getLanguage().isBlank())
                ? Locale.ENGLISH
                : loc;
    }
}
