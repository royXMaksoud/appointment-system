package com.care.appointment.domain.ports.in.appointmentstatuslanguage;

import com.care.appointment.domain.model.AppointmentStatusLanguage;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadAllUseCase {
    Page<AppointmentStatusLanguage> loadAllAppointmentStatusLanguages(FilterRequest filter, Pageable pageable);
}


