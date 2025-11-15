package com.care.appointment.domain.ports.in.referral;

import com.care.appointment.domain.model.AppointmentReferral;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadAllUseCase {
    Page<AppointmentReferral> loadAll(FilterRequest filter, Pageable pageable);
}

