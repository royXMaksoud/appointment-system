package com.care.appointment.infrastructure.db.adapter;

import com.care.appointment.domain.model.AppointmentReferral;
import com.care.appointment.domain.ports.out.referral.AppointmentReferralCrudPort;
import com.care.appointment.domain.ports.out.referral.AppointmentReferralSearchPort;
import com.care.appointment.infrastructure.db.config.AppointmentReferralFilterConfig;
import com.care.appointment.infrastructure.db.entities.AppointmentReferralEntity;
import com.care.appointment.infrastructure.db.mapper.AppointmentReferralJpaMapper;
import com.care.appointment.infrastructure.db.repositories.AppointmentReferralRepository;
import com.sharedlib.core.filter.FilterRequest;
import com.sharedlib.core.filter.GenericSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AppointmentReferralDbAdapter implements AppointmentReferralCrudPort, AppointmentReferralSearchPort {

    private final AppointmentReferralRepository repository;
    private final AppointmentReferralJpaMapper mapper;

    @Override
    public AppointmentReferral save(AppointmentReferral domain) {
        AppointmentReferralEntity entity = mapper.toEntity(domain);
        AppointmentReferralEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public AppointmentReferral update(AppointmentReferral domain) {
        AppointmentReferralEntity entity = mapper.toEntity(domain);
        AppointmentReferralEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }

    @Override
    public Optional<AppointmentReferral> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Page<AppointmentReferral> search(FilterRequest filter, Pageable pageable) {
        Specification<AppointmentReferralEntity> spec = buildSpecification(filter);
        return repository.findAll(spec, pageable).map(mapper::toDomain);
    }

    private Specification<AppointmentReferralEntity> buildSpecification(FilterRequest filter) {
        if (filter == null) {
            return (root, query, cb) -> cb.conjunction();
        }

        boolean hasCriteria = filter.getCriteria() != null && !filter.getCriteria().isEmpty();
        boolean hasGroups = filter.getGroups() != null && !filter.getGroups().isEmpty();
        boolean hasScopes = filter.getScopes() != null && !filter.getScopes().isEmpty();

        if (!hasCriteria && !hasGroups && !hasScopes) {
            return (root, query, cb) -> cb.conjunction();
        }

        return new GenericSpecificationBuilder<AppointmentReferralEntity>(AppointmentReferralFilterConfig.ALLOWED_FIELDS)
                .withCriteria(filter.getCriteria())
                .withGroups(filter.getGroups())
                .withScopes(filter.getScopes())
                .build();
    }
}
