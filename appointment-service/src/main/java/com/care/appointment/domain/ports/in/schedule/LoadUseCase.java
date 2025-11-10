package com.care.appointment.domain.ports.in.schedule;

import com.care.appointment.domain.model.Schedule;

import java.util.Optional;
import java.util.UUID;

public interface LoadUseCase {
    Optional<Schedule> getScheduleById(UUID scheduleId);
}

