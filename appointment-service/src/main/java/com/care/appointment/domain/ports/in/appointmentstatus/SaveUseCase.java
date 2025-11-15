package com.care.appointment.domain.ports.in.appointmentstatus;

import com.care.appointment.application.appointmentstatus.command.CreateAppointmentStatusCommand;
import com.care.appointment.domain.model.AppointmentStatus;

public interface SaveUseCase {
    AppointmentStatus saveAppointmentStatus(CreateAppointmentStatusCommand command);
}


