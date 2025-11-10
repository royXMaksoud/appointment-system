package com.care.appointment.infrastructure.db.adapter;

import com.care.appointment.domain.model.AppointmentReferral;
import com.care.appointment.domain.ports.in.referral.AppointmentReferralCrudPort;
import com.care.appointment.infrastructure.db.entities.AppointmentReferralEntity;
import com.care.appointment.infrastructure.db.mapper.AppointmentReferralJpaMapper;
import com.care.appointment.infrastructure.db.repositories.AppointmentReferralRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AppointmentReferralDbAdapter implements AppointmentReferralCrudPort {

    private final AppointmentReferralRepository repository;
    private final AppointmentReferralJpaMapper mapper;

    @Override
    public AppointmentReferral save(AppointmentReferral domain) {
        AppointmentReferralEntity entity = mapper.toEntity(domain);
        AppointmentReferralEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AppointmentReferral> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    public List<AppointmentReferral> findByBeneficiaryId(UUID beneficiaryId) {
        return repository.findByBeneficiaryId(beneficiaryId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<AppointmentReferral> findByStatus(String status) {
        return repository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}

