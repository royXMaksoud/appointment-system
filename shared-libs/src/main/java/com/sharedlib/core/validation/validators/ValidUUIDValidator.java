package com.sharedlib.core.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.sharedlib.core.validation.ValidUUID;
import java.util.UUID;

/**
 * Validator for the {@link ValidUUID} annotation.
 * <p>
 * Checks if the value is a valid UUID string.
 * <p>
 * Usage example:
 * <pre>
 * {@code
 * public class UserDto {
 *     @ValidUUID
 *     private String userId;
 * }
 * }
 * </pre>
 */
public class ValidUUIDValidator implements ConstraintValidator<ValidUUID, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) return true;
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
