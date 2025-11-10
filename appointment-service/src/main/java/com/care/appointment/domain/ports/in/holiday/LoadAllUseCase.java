package com.care.appointment.domain.ports.in.holiday;

import com.care.appointment.domain.model.Holiday;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadAllUseCase {
    Page<Holiday> loadAll(FilterRequest filter, Pageable pageable);
}

