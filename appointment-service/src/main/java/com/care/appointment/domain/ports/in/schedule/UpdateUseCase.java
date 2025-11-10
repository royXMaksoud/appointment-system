package com.care.appointment.domain.ports.in.schedule;

import com.care.appointment.application.schedule.command.UpdateScheduleCommand;
import com.care.appointment.domain.model.Schedule;

public interface UpdateUseCase {
    Schedule updateSchedule(UpdateScheduleCommand command);
}

