package com.care.appointment.domain.ports.out.beneficiary;

import com.care.appointment.domain.model.Beneficiary;

import java.util.Optional;
import java.util.UUID;

public interface BeneficiaryCrudPort {
    Beneficiary save(Beneficiary entity);
    Beneficiary update(Beneficiary entity);
    Optional<Beneficiary> findById(UUID id);
    void deleteById(UUID id);
}

