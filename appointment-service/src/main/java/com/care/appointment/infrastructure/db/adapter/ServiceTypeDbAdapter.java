package com.care.appointment.infrastructure.db.adapter;

import com.care.appointment.domain.model.ServiceType;
import com.care.appointment.domain.ports.out.servicetype.ServiceTypeCrudPort;
import com.care.appointment.domain.ports.out.servicetype.ServiceTypeSearchPort;
import com.care.appointment.infrastructure.db.config.ServiceTypeFilterConfig;
import com.care.appointment.infrastructure.db.entities.ServiceTypeEntity;
import com.care.appointment.infrastructure.db.mapper.ServiceTypeJpaMapper;
import com.care.appointment.infrastructure.db.repositories.ServiceTypeRepository;
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
public class ServiceTypeDbAdapter implements ServiceTypeCrudPort, ServiceTypeSearchPort {
    
    private final ServiceTypeRepository repository;
    private final ServiceTypeJpaMapper mapper;
    
    @Override
    public ServiceType save(ServiceType domain) {
        ServiceTypeEntity entity = mapper.toEntity(domain);
        ServiceTypeEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public ServiceType update(ServiceType domain) {
        ServiceTypeEntity entity = mapper.toEntity(domain);
        ServiceTypeEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }
    
    @Override
    public Optional<ServiceType> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }
    
    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
    
    @Override
    public Page<ServiceType> search(FilterRequest filter, Pageable pageable) {
        Specification<ServiceTypeEntity> spec = buildSpecification(filter);
        return repository.findAll(spec, pageable).map(mapper::toDomain);
    }
    
    @Override
    public boolean existsActiveByNameIgnoreCase(String name) {
        if (name == null) return false;
        String normalized = name.trim();
        if (normalized.isEmpty()) return false;
        
        return repository.existsByNameIgnoreCaseAndIsDeletedFalse(normalized);
    }
    
    private Specification<ServiceTypeEntity> buildSpecification(FilterRequest filter) {
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
        return new GenericSpecificationBuilder<ServiceTypeEntity>(ServiceTypeFilterConfig.ALLOWED_FIELDS)
                .withCriteria(filter.getCriteria())
                .withGroups(filter.getGroups())
                .withScopes(filter.getScopes())
                .build();
    }
}

