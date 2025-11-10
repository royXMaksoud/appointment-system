package com.sharedlib.core.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.sharedlib.core.validation.ValidDateRange;
import java.lang.reflect.Field;
import java.time.LocalDate;

public class ValidDateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {
    private String startField;
    private String endField;
    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.startField = constraintAnnotation.startField();
        this.endField = constraintAnnotation.endField();
    }
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Field start = value.getClass().getDeclaredField(startField);
            Field end = value.getClass().getDeclaredField(endField);
            start.setAccessible(true);
            end.setAccessible(true);
            Object startValue = start.get(value);
            Object endValue = end.get(value);
            if (startValue == null || endValue == null) return true;
            if (startValue instanceof LocalDate && endValue instanceof LocalDate) {
                return ((LocalDate) startValue).isBefore((LocalDate) endValue);
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }
}
