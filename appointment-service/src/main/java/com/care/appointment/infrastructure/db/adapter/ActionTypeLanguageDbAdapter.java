package com.care.appointment.infrastructure.db.adapter;

import com.care.appointment.domain.model.ActionTypeLanguage;
import com.care.appointment.domain.ports.out.actiontypelanguage.ActionTypeLanguageCrudPort;
import com.care.appointment.domain.ports.out.actiontypelanguage.ActionTypeLanguageSearchPort;
import com.care.appointment.infrastructure.db.config.ActionTypeLanguageFilterConfig;
import com.care.appointment.infrastructure.db.entities.AppointmentActionTypeLangEntity;
import com.care.appointment.infrastructure.db.mapper.ActionTypeLanguageJpaMapper;
import com.care.appointment.infrastructure.db.repositories.AppointmentActionTypeLangRepository;
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
public class ActionTypeLanguageDbAdapter implements ActionTypeLanguageCrudPort, ActionTypeLanguageSearchPort {

    private final AppointmentActionTypeLangRepository repository;
    private final ActionTypeLanguageJpaMapper mapper;

    @Override
    public ActionTypeLanguage save(ActionTypeLanguage language) {
        AppointmentActionTypeLangEntity entity = mapper.toEntity(language);
        AppointmentActionTypeLangEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public ActionTypeLanguage update(ActionTypeLanguage language) {
        AppointmentActionTypeLangEntity entity = mapper.toEntity(language);
        AppointmentActionTypeLangEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }

    @Override
    public Optional<ActionTypeLanguage> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Page<ActionTypeLanguage> search(FilterRequest filter, Pageable pageable) {
        Specification<AppointmentActionTypeLangEntity> spec = buildSpecification(filter);
        return repository.findAll(spec, pageable).map(mapper::toDomain);
    }

    @Override
    public Optional<ActionTypeLanguage> findByActionTypeIdAndLanguageCode(UUID actionTypeId, String languageCode) {
        if (actionTypeId == null || languageCode == null) {
            return Optional.empty();
        }
        return repository.findByActionTypeIdAndLanguageCodeIgnoreCase(actionTypeId, languageCode.trim())
                .map(mapper::toDomain);
    }

    private Specification<AppointmentActionTypeLangEntity> buildSpecification(FilterRequest filter) {
        if (filter == null) {
            return (root, query, cb) -> cb.conjunction();
        }

        boolean hasCriteria = filter.getCriteria() != null && !filter.getCriteria().isEmpty();
        boolean hasGroups = filter.getGroups() != null && !filter.getGroups().isEmpty();
        boolean hasScopes = filter.getScopes() != null && !filter.getScopes().isEmpty();

        if (!hasCriteria && !hasGroups && !hasScopes) {
            return (root, query, cb) -> cb.conjunction();
        }

        return new GenericSpecificationBuilder<AppointmentActionTypeLangEntity>(ActionTypeLanguageFilterConfig.ALLOWED_FIELDS)
                .withCriteria(filter.getCriteria())
                .withGroups(filter.getGroups())
                .withScopes(filter.getScopes())
                .build();
    }
}


