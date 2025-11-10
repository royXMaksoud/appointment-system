package com.sharedlib.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation to validate that a field or parameter contains a supported language code (e.g., "en", "ar").
 * <p>
 * Usage example:
 * <pre>
 * {@code
 * public class UserDto {
 *     @SupportedLang
 *     private String language;
 * }
 * }
 * </pre>
 * The value must be one of the supported language codes.
 */
@Documented
@Constraint(validatedBy = com.sharedlib.core.validation.validators.SupportedLangValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportedLang {
    /**
     * Message key for i18n error message.
     */
    String message() default "{validation.unsupported.language}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
