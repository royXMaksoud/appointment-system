package com.care.appointment.domain.ports.in.statushistory;

import com.care.appointment.domain.model.AppointmentStatusHistory;

import java.util.Optional;
import java.util.UUID;

public interface LoadUseCase {
    Optional<AppointmentStatusHistory> getById(UUID historyId);
}

