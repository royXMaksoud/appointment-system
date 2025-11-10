package com.care.appointment.domain.ports.in.schedule;

import com.care.appointment.application.schedule.command.CreateScheduleCommand;
import com.care.appointment.domain.model.Schedule;
import com.care.appointment.web.dto.admin.schedule.CreateScheduleBatchRequest;

import java.util.List;

public interface SaveUseCase {
    Schedule saveSchedule(CreateScheduleCommand command);
    List<Schedule> saveSchedulesBatch(CreateScheduleBatchRequest request);
}

