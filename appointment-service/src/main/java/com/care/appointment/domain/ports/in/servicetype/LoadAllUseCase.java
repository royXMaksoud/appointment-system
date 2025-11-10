package com.care.appointment.domain.ports.in.servicetype;

import com.care.appointment.domain.model.ServiceType;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadAllUseCase {
    Page<ServiceType> loadAll(FilterRequest filter, Pageable pageable);
}

