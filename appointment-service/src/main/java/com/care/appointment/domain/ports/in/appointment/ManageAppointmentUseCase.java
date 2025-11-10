package com.care.appointment.domain.ports.in.appointment;

import com.care.appointment.application.appointment.command.CancelAppointmentCommand;
import com.care.appointment.application.appointment.command.TransferAppointmentCommand;
import com.care.appointment.application.appointment.command.UpdateAppointmentStatusCommand;
import com.care.appointment.domain.model.Appointment;

public interface ManageAppointmentUseCase {
    Appointment updateStatus(UpdateAppointmentStatusCommand command);
    Appointment cancelAppointment(CancelAppointmentCommand command);
    Appointment transferAppointment(TransferAppointmentCommand command);
}

