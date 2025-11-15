package com.care.appointment.infrastructure.db.adapter;

import com.care.appointment.domain.model.AppointmentStatusHistory;
import com.care.appointment.domain.ports.out.statushistory.AppointmentStatusHistoryCrudPort;
import com.care.appointment.domain.ports.out.statushistory.AppointmentStatusHistorySearchPort;
import com.care.appointment.infrastructure.db.config.AppointmentStatusHistoryFilterConfig;
import com.care.appointment.infrastructure.db.entities.AppointmentStatusHistoryEntity;
import com.care.appointment.infrastructure.db.mapper.AppointmentStatusHistoryJpaMapper;
import com.care.appointment.infrastructure.db.repositories.AppointmentStatusHistoryRepository;
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
public class AppointmentStatusHistoryDbAdapter implements
        AppointmentStatusHistoryCrudPort,
        AppointmentStatusHistorySearchPort {

    private final AppointmentStatusHistoryRepository repository;
    private final AppointmentStatusHistoryJpaMapper mapper;

    @Override
    public AppointmentStatusHistory save(AppointmentStatusHistory history) {
        AppointmentStatusHistoryEntity entity = mapper.toEntity(history);
        AppointmentStatusHistoryEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public AppointmentStatusHistory update(AppointmentStatusHistory history) {
        AppointmentStatusHistoryEntity entity = mapper.toEntity(history);
        AppointmentStatusHistoryEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }

    @Override
    public Optional<AppointmentStatusHistory> findById(UUID historyId) {
        return repository.findById(historyId).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID historyId) {
        repository.deleteById(historyId);
    }

    @Override
    public Page<AppointmentStatusHistory> search(FilterRequest filter, Pageable pageable) {
        Specification<AppointmentStatusHistoryEntity> spec = buildSpecification(filter);
        return repository.findAll(spec, pageable).map(mapper::toDomain);
    }

    private Specification<AppointmentStatusHistoryEntity> buildSpecification(FilterRequest filter) {
        if (filter == null) {
            return (root, query, cb) -> cb.conjunction();
        }

        boolean hasCriteria = filter.getCriteria() != null && !filter.getCriteria().isEmpty();
        boolean hasGroups = filter.getGroups() != null && !filter.getGroups().isEmpty();
        boolean hasScopes = filter.getScopes() != null && !filter.getScopes().isEmpty();

        if (!hasCriteria && !hasGroups && !hasScopes) {
            return (root, query, cb) -> cb.conjunction();
        }

        return new GenericSpecificationBuilder<AppointmentStatusHistoryEntity>(AppointmentStatusHistoryFilterConfig.ALLOWED_FIELDS)
                .withCriteria(filter.getCriteria())
                .withGroups(filter.getGroups())
                .withScopes(filter.getScopes())
                .build();
    }
}

