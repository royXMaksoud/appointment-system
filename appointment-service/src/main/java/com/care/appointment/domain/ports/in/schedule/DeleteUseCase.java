package com.care.appointment.domain.ports.in.schedule;

import java.util.UUID;

public interface DeleteUseCase {
    void deleteSchedule(UUID scheduleId);
}

