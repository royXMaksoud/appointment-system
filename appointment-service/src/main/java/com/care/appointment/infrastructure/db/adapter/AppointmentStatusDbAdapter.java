package com.care.appointment.infrastructure.db.adapter;

import com.care.appointment.domain.model.AppointmentStatus;
import com.care.appointment.domain.ports.out.appointmentstatus.AppointmentStatusCrudPort;
import com.care.appointment.domain.ports.out.appointmentstatus.AppointmentStatusSearchPort;
import com.care.appointment.infrastructure.db.config.AppointmentStatusFilterConfig;
import com.care.appointment.infrastructure.db.entities.AppointmentStatusEntity;
import com.care.appointment.infrastructure.db.mapper.AppointmentStatusJpaMapper;
import com.care.appointment.infrastructure.db.repositories.AppointmentStatusRepository;
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
public class AppointmentStatusDbAdapter implements AppointmentStatusCrudPort, AppointmentStatusSearchPort {

    private final AppointmentStatusRepository repository;
    private final AppointmentStatusJpaMapper mapper;

    @Override
    public AppointmentStatus save(AppointmentStatus status) {
        AppointmentStatusEntity entity = mapper.toEntity(status);
        AppointmentStatusEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public AppointmentStatus update(AppointmentStatus status) {
        AppointmentStatusEntity entity = mapper.toEntity(status);
        AppointmentStatusEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }

    @Override
    public Optional<AppointmentStatus> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        if (code == null) return false;
        return repository.existsByCodeIgnoreCaseAndIsDeletedFalse(code.trim());
    }

    @Override
    public Page<AppointmentStatus> search(FilterRequest filter, Pageable pageable) {
        Specification<AppointmentStatusEntity> spec = buildSpecification(filter);
        return repository.findAll(spec, pageable).map(mapper::toDomain);
    }

    private Specification<AppointmentStatusEntity> buildSpecification(FilterRequest filter) {
        if (filter == null) {
            return (root, query, cb) -> cb.conjunction();
        }

        boolean hasCriteria = filter.getCriteria() != null && !filter.getCriteria().isEmpty();
        boolean hasGroups = filter.getGroups() != null && !filter.getGroups().isEmpty();
        boolean hasScopes = filter.getScopes() != null && !filter.getScopes().isEmpty();

        if (!hasCriteria && !hasGroups && !hasScopes) {
            return (root, query, cb) -> cb.conjunction();
        }

        return new GenericSpecificationBuilder<AppointmentStatusEntity>(AppointmentStatusFilterConfig.ALLOWED_FIELDS)
                .withCriteria(filter.getCriteria())
                .withGroups(filter.getGroups())
                .withScopes(filter.getScopes())
                .build();
    }
}


