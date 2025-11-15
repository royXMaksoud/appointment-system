package com.care.appointment.infrastructure.db.adapter;

import com.care.appointment.domain.model.ServiceTypeLanguage;
import com.care.appointment.domain.ports.out.servicetypelanguage.ServiceTypeLanguageCrudPort;
import com.care.appointment.domain.ports.out.servicetypelanguage.ServiceTypeLanguageSearchPort;
import com.care.appointment.infrastructure.db.config.ServiceTypeLanguageFilterConfig;
import com.care.appointment.infrastructure.db.entities.ServiceTypeLangEntity;
import com.care.appointment.infrastructure.db.mapper.ServiceTypeLanguageJpaMapper;
import com.care.appointment.infrastructure.db.repositories.ServiceTypeLangRepository;
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
public class ServiceTypeLanguageDbAdapter implements ServiceTypeLanguageCrudPort, ServiceTypeLanguageSearchPort {

    private final ServiceTypeLangRepository repository;
    private final ServiceTypeLanguageJpaMapper mapper;

    @Override
    public ServiceTypeLanguage save(ServiceTypeLanguage language) {
        ServiceTypeLangEntity entity = mapper.toEntity(language);
        ServiceTypeLangEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public ServiceTypeLanguage update(ServiceTypeLanguage language) {
        ServiceTypeLangEntity entity = mapper.toEntity(language);
        ServiceTypeLangEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }

    @Override
    public Optional<ServiceTypeLanguage> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Page<ServiceTypeLanguage> search(FilterRequest filter, Pageable pageable) {
        Specification<ServiceTypeLangEntity> spec = buildSpecification(filter);
        return repository.findAll(spec, pageable).map(mapper::toDomain);
    }

    @Override
    public Optional<ServiceTypeLanguage> findByServiceTypeIdAndLanguageCode(UUID serviceTypeId, String languageCode) {
        if (serviceTypeId == null || languageCode == null) {
            return Optional.empty();
        }
        return repository.findByServiceTypeIdAndLanguageCodeIgnoreCase(serviceTypeId, languageCode.trim())
                .map(mapper::toDomain);
    }

    private Specification<ServiceTypeLangEntity> buildSpecification(FilterRequest filter) {
        if (filter == null) {
            return (root, query, cb) -> cb.conjunction();
        }

        boolean hasCriteria = filter.getCriteria() != null && !filter.getCriteria().isEmpty();
        boolean hasGroups = filter.getGroups() != null && !filter.getGroups().isEmpty();
        boolean hasScopes = filter.getScopes() != null && !filter.getScopes().isEmpty();

        if (!hasCriteria && !hasGroups && !hasScopes) {
            return (root, query, cb) -> cb.conjunction();
        }

        return new GenericSpecificationBuilder<ServiceTypeLangEntity>(ServiceTypeLanguageFilterConfig.ALLOWED_FIELDS)
                .withCriteria(filter.getCriteria())
                .withGroups(filter.getGroups())
                .withScopes(filter.getScopes())
                .build();
    }
}


