package com.care.appointment.domain.ports.out.actiontypelanguage;

import com.care.appointment.domain.model.ActionTypeLanguage;

import java.util.Optional;
import java.util.UUID;

public interface ActionTypeLanguageCrudPort {
    ActionTypeLanguage save(ActionTypeLanguage language);
    ActionTypeLanguage update(ActionTypeLanguage language);
    Optional<ActionTypeLanguage> findById(UUID id);
    void deleteById(UUID id);
}


