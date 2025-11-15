package com.care.appointment.domain.ports.out.appointment;

import com.care.appointment.domain.model.Appointment;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AppointmentSearchPort {
    Page<Appointment> search(FilterRequest filter, Pageable pageable);
    Page<Appointment> findByBeneficiaryId(UUID beneficiaryId, Pageable pageable);
}

