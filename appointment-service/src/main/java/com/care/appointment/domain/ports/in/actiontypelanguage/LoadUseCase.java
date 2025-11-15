package com.care.appointment.domain.ports.in.actiontypelanguage;

import com.care.appointment.domain.model.ActionTypeLanguage;

import java.util.Optional;
import java.util.UUID;

public interface LoadUseCase {
    Optional<ActionTypeLanguage> getActionTypeLanguageById(UUID id);
}


