package com.care.appointment.domain.ports.in.holiday;

import com.care.appointment.domain.model.Holiday;

import java.util.Optional;
import java.util.UUID;

public interface LoadUseCase {
    Optional<Holiday> getHolidayById(UUID holidayId);
}

