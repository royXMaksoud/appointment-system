package com.sharedlib.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation to validate that a field or parameter contains a value from a specified enum.
 * <p>
 * Usage example:
 * <pre>
 * {@code
 * public enum Status {
 *     ACTIVE, INACTIVE
 * }
 * public class UserDto {
 *     @ValidEnum(enumClass = Status.class)
 *     private String status;
 * }
 * }
 * </pre>
 * The value must match one of the enum constants.
 */
@Documented
@Constraint(validatedBy = com.sharedlib.core.validation.validators.ValidEnumValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEnum {
    /**
     * The enum class to validate against.
     */
    Class<? extends Enum<?>> enumClass();
    /**
     * Message key for i18n error message.
     */
    String message() default "{validation.invalid.enum}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
