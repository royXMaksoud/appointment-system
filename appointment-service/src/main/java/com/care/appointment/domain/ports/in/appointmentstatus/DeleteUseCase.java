package com.care.appointment.domain.ports.in.appointmentstatus;

import java.util.UUID;

public interface DeleteUseCase {
    void deleteAppointmentStatus(UUID id);
}


