package com.sharedlib.core.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.sharedlib.core.validation.ValidPhone;

public class ValidPhoneValidator implements ConstraintValidator<ValidPhone, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) return true;
        // Simple international phone validation
        return value.matches("^\\+?[0-9]{7,15}$");
    }
}
