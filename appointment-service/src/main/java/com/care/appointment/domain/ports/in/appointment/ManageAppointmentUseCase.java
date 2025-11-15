package com.care.appointment.domain.ports.in.appointment;

import com.care.appointment.application.appointment.command.*;
import com.care.appointment.domain.model.Appointment;

import java.util.UUID;

public interface ManageAppointmentUseCase {
    Appointment createAppointment(CreateAppointmentCommand command);
    Appointment updateAppointment(UpdateAppointmentCommand command);
    void deleteAppointment(UUID appointmentId);
    Appointment updateStatus(UpdateAppointmentStatusCommand command);
    Appointment cancelAppointment(CancelAppointmentCommand command);
    Appointment transferAppointment(TransferAppointmentCommand command);
}

