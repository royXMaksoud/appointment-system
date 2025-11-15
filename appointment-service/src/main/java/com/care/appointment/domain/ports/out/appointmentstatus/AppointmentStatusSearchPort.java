package com.care.appointment.domain.ports.out.appointmentstatus;

import com.care.appointment.domain.model.AppointmentStatus;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentStatusSearchPort {
    Page<AppointmentStatus> search(FilterRequest filter, Pageable pageable);
}


