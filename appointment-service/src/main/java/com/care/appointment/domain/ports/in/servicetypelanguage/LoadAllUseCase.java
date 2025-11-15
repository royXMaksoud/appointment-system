package com.care.appointment.domain.ports.in.servicetypelanguage;

import com.care.appointment.domain.model.ServiceTypeLanguage;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadAllUseCase {
    Page<ServiceTypeLanguage> loadAllServiceTypeLanguages(FilterRequest filter, Pageable pageable);
}


