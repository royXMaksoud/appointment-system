package com.care.appointment.domain.ports.in.beneficiary;

import com.care.appointment.domain.model.Beneficiary;
import com.sharedlib.core.filter.FilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadAllUseCase {
    Page<Beneficiary> loadAll(FilterRequest filter, Pageable pageable);
}

