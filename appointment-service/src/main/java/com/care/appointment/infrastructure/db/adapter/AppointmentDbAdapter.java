package com.care.appointment.infrastructure.db.adapter;

import com.care.appointment.domain.model.Appointment;
import com.care.appointment.domain.ports.out.appointment.AppointmentCrudPort;
import com.care.appointment.domain.ports.out.appointment.AppointmentSearchPort;
import com.care.appointment.infrastructure.db.config.AppointmentFilterConfig;
import com.care.appointment.infrastructure.db.entities.AppointmentEntity;
import com.care.appointment.infrastructure.db.mapper.AppointmentJpaMapper;
import com.care.appointment.infrastructure.db.repositories.AppointmentRepository;
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
public class AppointmentDbAdapter implements AppointmentCrudPort, AppointmentSearchPort {

    private final AppointmentRepository repository;
    private final AppointmentJpaMapper mapper;

    @Override
    public Appointment save(Appointment domain) {
        AppointmentEntity entity = mapper.toEntity(domain);
        AppointmentEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Appointment update(Appointment domain) {
        AppointmentEntity entity = mapper.toEntity(domain);
        AppointmentEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }

    @Override
    public Optional<Appointment> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Appointment> findByAppointmentCode(String appointmentCode) {
        if (appointmentCode == null || appointmentCode.isBlank()) {
            return Optional.empty();
        }
        return repository.findByAppointmentCode(appointmentCode).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Page<Appointment> search(FilterRequest filter, Pageable pageable) {
        Specification<AppointmentEntity> spec = buildSpecification(filter);
        return repository.findAll(spec, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Appointment> findByBeneficiaryId(UUID beneficiaryId, Pageable pageable) {
        return repository.findByBeneficiaryId(beneficiaryId, pageable).map(mapper::toDomain);
    }

    private Specification<AppointmentEntity> buildSpecification(FilterRequest filter) {
        if (filter == null) {
            return (root, q, cb) -> cb.conjunction();
        }

        // Check if there are any filters (criteria, groups, or scopes)
        boolean hasCriteria = filter.getCriteria() != null && !filter.getCriteria().isEmpty();
        boolean hasGroups = filter.getGroups() != null && !filter.getGroups().isEmpty();
        boolean hasScopes = filter.getScopes() != null && !filter.getScopes().isEmpty();

        // If no filters at all, return empty spec
        if (!hasCriteria && !hasGroups && !hasScopes) {
            return (root, q, cb) -> cb.conjunction();
        }

        // Always build specification with all filters (scopes are applied first)
        return new GenericSpecificationBuilder<AppointmentEntity>(AppointmentFilterConfig.ALLOWED_FIELDS)
                .withCriteria(filter.getCriteria())
                .withGroups(filter.getGroups())
                .withScopes(filter.getScopes())
                .build();
    }
}

