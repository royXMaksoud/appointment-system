package com.care.appointment.domain.ports.in.schedule;

import com.care.appointment.domain.model.Schedule;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadAllUseCase {
    Page<Schedule> loadAll(FilterRequest filter, Pageable pageable);
}

