package com.care.appointment.domain.ports.out.servicetype;

import com.care.appointment.domain.model.ServiceType;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceTypeSearchPort {
    Page<ServiceType> search(FilterRequest filter, Pageable pageable);
    boolean existsActiveByNameIgnoreCase(String name);
}

