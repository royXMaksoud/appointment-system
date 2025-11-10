package com.care.appointment.domain.ports.in.actiontype;

import com.care.appointment.domain.model.ActionType;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadAllUseCase {
    Page<ActionType> loadAll(FilterRequest filter, Pageable pageable);
}

