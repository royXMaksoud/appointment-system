package com.care.appointment.domain.ports.in.appointmentstatuslanguage;

import com.care.appointment.application.appointmentstatuslanguage.command.UpdateAppointmentStatusLanguageCommand;
import com.care.appointment.domain.model.AppointmentStatusLanguage;

public interface UpdateUseCase {
    AppointmentStatusLanguage updateAppointmentStatusLanguage(UpdateAppointmentStatusLanguageCommand command);
}


