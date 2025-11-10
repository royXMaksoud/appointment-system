package com.care.appointment.domain.ports.out.appointment;

import com.care.appointment.domain.model.Appointment;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentSearchPort {
    Page<Appointment> search(FilterRequest filter, Pageable pageable);
}

