package com.care.appointment.domain.ports.in.statushistory;

import com.care.appointment.domain.model.AppointmentStatusHistory;

public interface UpdateUseCase {
    AppointmentStatusHistory update(AppointmentStatusHistory history);
}

