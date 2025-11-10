package com.sharedlib.core.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.sharedlib.core.validation.SupportedLang;
import java.util.Set;
import java.util.Arrays;

/**
 * Validator for the {@link SupportedLang} annotation.
 * <p>
 * Checks if the value is among the supported language codes (e.g., "en", "ar").
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
 */
public class SupportedLangValidator implements ConstraintValidator<SupportedLang, String> {
    private static final Set<String> SUPPORTED_LANGS = Set.of("en", "ar");
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) return true;
        return SUPPORTED_LANGS.contains(value);
    }
}
