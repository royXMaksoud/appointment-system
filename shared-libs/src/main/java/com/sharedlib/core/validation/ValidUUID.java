package com.sharedlib.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation to validate that a field or parameter contains a valid UUID string.
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
 * The value must be a valid UUID format.
 */
@Documented
@Constraint(validatedBy = com.sharedlib.core.validation.validators.ValidUUIDValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUUID {
    /**
     * Message key for i18n error message.
     */
    String message() default "{validation.invalid.uuid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
