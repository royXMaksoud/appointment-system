package com.care.appointment.domain.ports.out.appointmentstatuslanguage;

import com.care.appointment.domain.model.AppointmentStatusLanguage;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface AppointmentStatusLanguageSearchPort {
    Page<AppointmentStatusLanguage> search(FilterRequest filter, Pageable pageable);
    Optional<AppointmentStatusLanguage> findByAppointmentStatusIdAndLanguageCode(UUID statusId, String languageCode);
}


