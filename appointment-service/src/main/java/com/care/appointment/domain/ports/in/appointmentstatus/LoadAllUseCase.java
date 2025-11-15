package com.care.appointment.domain.ports.in.appointmentstatus;

import com.care.appointment.domain.model.AppointmentStatus;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadAllUseCase {
    Page<AppointmentStatus> loadAllAppointmentStatuses(FilterRequest filter, Pageable pageable);
}


