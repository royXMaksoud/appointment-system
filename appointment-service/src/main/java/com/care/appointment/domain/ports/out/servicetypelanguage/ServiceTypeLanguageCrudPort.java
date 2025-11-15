package com.care.appointment.domain.ports.out.servicetypelanguage;

import com.care.appointment.domain.model.ServiceTypeLanguage;

import java.util.Optional;
import java.util.UUID;

public interface ServiceTypeLanguageCrudPort {
    ServiceTypeLanguage save(ServiceTypeLanguage language);
    ServiceTypeLanguage update(ServiceTypeLanguage language);
    Optional<ServiceTypeLanguage> findById(UUID id);
    void deleteById(UUID id);
}


