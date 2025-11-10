package com.sharedlib.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation to validate that a field or parameter contains a valid international phone number.
 * <p>
 * Usage example:
 * <pre>
 * {@code
 * public class UserDto {
 *     @ValidPhone
 *     private String phoneNumber;
 * }
 * }
 * </pre>
 * The phone number must be 7-15 digits and may start with '+'.
 */
@Documented
@Constraint(validatedBy = com.sharedlib.core.validation.validators.ValidPhoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhone {
    /**
     * Message key for i18n error message.
     */
    String message() default "{validation.invalid.phone}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
