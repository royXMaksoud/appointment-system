package com.care.appointment.domain.ports.out.actiontype;

import com.care.appointment.domain.model.ActionType;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ActionTypeSearchPort {
    Page<ActionType> search(FilterRequest filter, Pageable pageable);
    boolean existsActiveByCodeIgnoreCase(String code);
}

