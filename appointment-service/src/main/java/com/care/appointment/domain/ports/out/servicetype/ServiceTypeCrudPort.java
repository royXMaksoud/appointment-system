package com.care.appointment.domain.ports.out.servicetype;

import com.care.appointment.domain.model.ServiceType;

import java.util.Optional;
import java.util.UUID;

public interface ServiceTypeCrudPort {
    ServiceType save(ServiceType entity);
    ServiceType update(ServiceType entity);
    Optional<ServiceType> findById(UUID id);
    void deleteById(UUID id);
}

