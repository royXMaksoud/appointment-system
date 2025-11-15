package com.care.appointment.domain.ports.out.statushistory;

import com.care.appointment.domain.model.AppointmentStatusHistory;

import java.util.Optional;
import java.util.UUID;

public interface AppointmentStatusHistoryCrudPort {
    AppointmentStatusHistory save(AppointmentStatusHistory history);
    AppointmentStatusHistory update(AppointmentStatusHistory history);
    Optional<AppointmentStatusHistory> findById(UUID historyId);
    void deleteById(UUID historyId);
}

