package com.care.appointment.domain.ports.in.statushistory;

import com.care.appointment.domain.model.AppointmentStatusHistory;

public interface SaveUseCase {
    AppointmentStatusHistory save(AppointmentStatusHistory history);
}

