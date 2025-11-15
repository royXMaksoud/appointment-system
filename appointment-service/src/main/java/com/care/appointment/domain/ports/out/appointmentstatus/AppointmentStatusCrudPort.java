package com.care.appointment.domain.ports.out.appointmentstatus;

import com.care.appointment.domain.model.AppointmentStatus;

import java.util.Optional;
import java.util.UUID;

public interface AppointmentStatusCrudPort {
    AppointmentStatus save(AppointmentStatus status);
    AppointmentStatus update(AppointmentStatus status);
    Optional<AppointmentStatus> findById(UUID id);
    void deleteById(UUID id);
    boolean existsByCode(String code);
}


