package com.care.appointment.domain.ports.in.appointmentstatus;

import com.care.appointment.domain.model.AppointmentStatus;

import java.util.Optional;
import java.util.UUID;

public interface LoadUseCase {
    Optional<AppointmentStatus> getAppointmentStatusById(UUID id);
}


