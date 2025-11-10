package com.care.appointment.domain.ports.in.holiday;

import com.care.appointment.application.holiday.command.UpdateHolidayCommand;
import com.care.appointment.domain.model.Holiday;

public interface UpdateUseCase {
    Holiday updateHoliday(UpdateHolidayCommand command);
}

