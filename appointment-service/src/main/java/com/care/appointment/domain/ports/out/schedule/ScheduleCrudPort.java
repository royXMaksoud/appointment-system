package com.care.appointment.domain.ports.out.schedule;

import com.care.appointment.domain.model.Schedule;

import java.util.Optional;
import java.util.UUID;

public interface ScheduleCrudPort {
    Schedule save(Schedule entity);
    Schedule update(Schedule entity);
    Optional<Schedule> findById(UUID id);
    void deleteById(UUID id);
}

