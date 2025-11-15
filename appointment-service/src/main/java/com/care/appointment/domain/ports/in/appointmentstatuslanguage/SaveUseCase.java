package com.care.appointment.domain.ports.in.appointmentstatuslanguage;

import com.care.appointment.application.appointmentstatuslanguage.command.CreateAppointmentStatusLanguageCommand;
import com.care.appointment.domain.model.AppointmentStatusLanguage;

public interface SaveUseCase {
    AppointmentStatusLanguage saveAppointmentStatusLanguage(CreateAppointmentStatusLanguageCommand command);
}


