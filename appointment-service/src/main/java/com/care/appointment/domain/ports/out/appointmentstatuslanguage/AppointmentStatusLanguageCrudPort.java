package com.care.appointment.domain.ports.out.appointmentstatuslanguage;

import com.care.appointment.domain.model.AppointmentStatusLanguage;

import java.util.Optional;
import java.util.UUID;

public interface AppointmentStatusLanguageCrudPort {
    AppointmentStatusLanguage save(AppointmentStatusLanguage language);
    AppointmentStatusLanguage update(AppointmentStatusLanguage language);
    Optional<AppointmentStatusLanguage> findById(UUID id);
    void deleteById(UUID id);
}


