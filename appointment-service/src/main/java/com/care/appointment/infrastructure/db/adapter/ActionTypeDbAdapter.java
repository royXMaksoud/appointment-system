package com.care.appointment.infrastructure.db.adapter;

import com.care.appointment.domain.model.ActionType;
import com.care.appointment.domain.ports.out.actiontype.ActionTypeCrudPort;
import com.care.appointment.domain.ports.out.actiontype.ActionTypeSearchPort;
import com.care.appointment.infrastructure.db.config.ActionTypeFilterConfig;
import com.care.appointment.infrastructure.db.entities.AppointmentActionTypeEntity;
import com.care.appointment.infrastructure.db.mapper.ActionTypeJpaMapper;
import com.care.appointment.infrastructure.db.repositories.AppointmentActionTypeRepository;
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
public class ActionTypeDbAdapter implements ActionTypeCrudPort, ActionTypeSearchPort {
    
    private final AppointmentActionTypeRepository repository;
    private final ActionTypeJpaMapper mapper;
    
    @Override
    public ActionType save(ActionType domain) {
        AppointmentActionTypeEntity entity = mapper.toEntity(domain);
        AppointmentActionTypeEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public ActionType update(ActionType domain) {
        AppointmentActionTypeEntity entity = mapper.toEntity(domain);
        AppointmentActionTypeEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }
    
    @Override
    public Optional<ActionType> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }
    
    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
    
    @Override
    public Page<ActionType> search(FilterRequest filter, Pageable pageable) {
        Specification<AppointmentActionTypeEntity> spec = buildSpecification(filter);
        return repository.findAll(spec, pageable).map(mapper::toDomain);
    }
    
    @Override
    public boolean existsActiveByCodeIgnoreCase(String code) {
        if (code == null) return false;
        String normalized = code.trim();
        if (normalized.isEmpty()) return false;
        
        return repository.existsByCodeIgnoreCaseAndIsDeletedFalse(normalized);
    }
    
    private Specification<AppointmentActionTypeEntity> buildSpecification(FilterRequest filter) {
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
        return new GenericSpecificationBuilder<AppointmentActionTypeEntity>(ActionTypeFilterConfig.ALLOWED_FIELDS)
                .withCriteria(filter.getCriteria())
                .withGroups(filter.getGroups())
                .withScopes(filter.getScopes())
                .build();
    }
}

