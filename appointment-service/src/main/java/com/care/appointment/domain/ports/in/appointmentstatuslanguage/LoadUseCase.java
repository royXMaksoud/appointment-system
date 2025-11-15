package com.care.appointment.domain.ports.in.appointmentstatuslanguage;

import com.care.appointment.domain.model.AppointmentStatusLanguage;

import java.util.Optional;
import java.util.UUID;

public interface LoadUseCase {
    Optional<AppointmentStatusLanguage> getAppointmentStatusLanguageById(UUID id);
}


