package com.care.appointment.domain.ports.in.actiontypelanguage;

import com.care.appointment.domain.model.ActionTypeLanguage;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadAllUseCase {
    Page<ActionTypeLanguage> loadAllActionTypeLanguages(FilterRequest filter, Pageable pageable);
}


