package com.care.appointment.domain.ports.in.statushistory;

import com.care.appointment.domain.model.AppointmentStatusHistory;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadAllUseCase {
    Page<AppointmentStatusHistory> loadAll(FilterRequest filter, Pageable pageable);
}

