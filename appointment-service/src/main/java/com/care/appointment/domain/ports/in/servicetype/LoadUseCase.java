package com.care.appointment.domain.ports.in.servicetype;

import com.care.appointment.domain.model.ServiceType;

import java.util.Optional;
import java.util.UUID;

public interface LoadUseCase {
    Optional<ServiceType> getServiceTypeById(UUID id);
}

