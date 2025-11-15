package com.care.appointment.domain.ports.out.actiontypelanguage;

import com.care.appointment.domain.model.ActionTypeLanguage;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ActionTypeLanguageSearchPort {
    Page<ActionTypeLanguage> search(FilterRequest filter, Pageable pageable);
    Optional<ActionTypeLanguage> findByActionTypeIdAndLanguageCode(UUID actionTypeId, String languageCode);
}


