package com.sharedlib.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation to validate that a bean has a valid date range (start < end).
 * <p>
 * Usage example:
 * <pre>
 * {@code
 * @ValidDateRange(startField = "startDate", endField = "endDate")
 * public class DateRangeDto {
 *     private LocalDate startDate;
 *     private LocalDate endDate;
 * }
 * }
 * </pre>
 * Ensures that startDate is before endDate.
 */
@Documented
@Constraint(validatedBy = com.sharedlib.core.validation.validators.ValidDateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
    /**
     * Name of the start date field.
     */
    String startField();
    /**
     * Name of the end date field.
     */
    String endField();
    /**
     * Message key for i18n error message.
     */
    String message() default "{validation.invalid.daterange}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
