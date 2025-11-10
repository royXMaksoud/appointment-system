package com.sharedlib.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation to validate that a field or parameter contains a valid email address.
 * <p>
 * Usage example:
 * <pre>
 * {@code
 * public class UserDto {
 *     @ValidEmail
 *     private String email;
 * }
 * }
 * </pre>
 * The email must match a standard email format.
 */
@Documented
@Constraint(validatedBy = com.sharedlib.core.validation.validators.ValidEmailValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {
    /**
     * Message key for i18n error message.
     */
    String message() default "{validation.invalid.email}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
