package com.care.appointment.domain.ports.in.appointmentstatus;

import com.care.appointment.application.appointmentstatus.command.UpdateAppointmentStatusCommand;
import com.care.appointment.domain.model.AppointmentStatus;

public interface UpdateUseCase {
    AppointmentStatus updateAppointmentStatus(UpdateAppointmentStatusCommand command);
}


