package com.care.appointment.domain.ports.out.appointment;

import com.care.appointment.domain.model.Appointment;

import java.util.Optional;
import java.util.UUID;

public interface AppointmentCrudPort {
    Appointment save(Appointment entity);
    Appointment update(Appointment entity);
    Optional<Appointment> findById(UUID id);
    void deleteById(UUID id);
}

