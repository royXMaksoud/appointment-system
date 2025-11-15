package com.care.appointment.domain.ports.in.servicetypelanguage;

import com.care.appointment.domain.model.ServiceTypeLanguage;

import java.util.Optional;
import java.util.UUID;

public interface LoadUseCase {
    Optional<ServiceTypeLanguage> getServiceTypeLanguageById(UUID id);
}


