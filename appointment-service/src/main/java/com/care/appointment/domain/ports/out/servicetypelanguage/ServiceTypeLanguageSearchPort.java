package com.care.appointment.domain.ports.out.servicetypelanguage;

import com.care.appointment.domain.model.ServiceTypeLanguage;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ServiceTypeLanguageSearchPort {
    Page<ServiceTypeLanguage> search(FilterRequest filter, Pageable pageable);
    Optional<ServiceTypeLanguage> findByServiceTypeIdAndLanguageCode(UUID serviceTypeId, String languageCode);
}


