package com.care.appointment.domain.ports.in.holiday;

import com.care.appointment.application.holiday.command.CreateHolidayCommand;
import com.care.appointment.domain.model.Holiday;

public interface SaveUseCase {
    Holiday saveHoliday(CreateHolidayCommand command);
}

