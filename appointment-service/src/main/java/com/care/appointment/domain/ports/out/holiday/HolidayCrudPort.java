package com.care.appointment.domain.ports.out.holiday;

import com.care.appointment.domain.model.Holiday;

import java.util.Optional;
import java.util.UUID;

public interface HolidayCrudPort {
    Holiday save(Holiday entity);
    Holiday update(Holiday entity);
    Optional<Holiday> findById(UUID id);
    void deleteById(UUID id);
}

