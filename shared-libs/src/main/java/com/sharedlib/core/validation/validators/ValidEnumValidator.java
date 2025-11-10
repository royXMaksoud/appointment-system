package com.sharedlib.core.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.sharedlib.core.validation.ValidEnum;

public class ValidEnumValidator implements ConstraintValidator<ValidEnum, String> {
    private ValidEnum annotation;
    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) return true;
        Object[] enumValues = annotation.enumClass().getEnumConstants();
        for (Object enumValue : enumValues) {
            if (value.equals(enumValue.toString())) {
                return true;
            }
        }
        return false;
    }
}
