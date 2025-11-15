package com.care.appointment.infrastructure.db.adapter;

import com.care.appointment.domain.model.AppointmentStatusLanguage;
import com.care.appointment.domain.ports.out.appointmentstatuslanguage.AppointmentStatusLanguageCrudPort;
import com.care.appointment.domain.ports.out.appointmentstatuslanguage.AppointmentStatusLanguageSearchPort;
import com.care.appointment.infrastructure.db.config.AppointmentStatusLanguageFilterConfig;
import com.care.appointment.infrastructure.db.entities.AppointmentStatusLangEntity;
import com.care.appointment.infrastructure.db.mapper.AppointmentStatusLanguageJpaMapper;
import com.care.appointment.infrastructure.db.repositories.AppointmentStatusLangRepository;
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
public class AppointmentStatusLanguageDbAdapter implements AppointmentStatusLanguageCrudPort, AppointmentStatusLanguageSearchPort {

    private final AppointmentStatusLangRepository repository;
    private final AppointmentStatusLanguageJpaMapper mapper;

    @Override
    public AppointmentStatusLanguage save(AppointmentStatusLanguage language) {
        AppointmentStatusLangEntity entity = mapper.toEntity(language);
        AppointmentStatusLangEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public AppointmentStatusLanguage update(AppointmentStatusLanguage language) {
        AppointmentStatusLangEntity entity = mapper.toEntity(language);
        AppointmentStatusLangEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }

    @Override
    public Optional<AppointmentStatusLanguage> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Page<AppointmentStatusLanguage> search(FilterRequest filter, Pageable pageable) {
        Specification<AppointmentStatusLangEntity> spec = buildSpecification(filter);
        return repository.findAll(spec, pageable).map(mapper::toDomain);
    }

    @Override
    public Optional<AppointmentStatusLanguage> findByAppointmentStatusIdAndLanguageCode(UUID statusId, String languageCode) {
        if (statusId == null || languageCode == null) {
            return Optional.empty();
        }
        return repository.findByAppointmentStatusIdAndLanguageCodeIgnoreCase(statusId, languageCode.trim())
                .map(mapper::toDomain);
    }

    private Specification<AppointmentStatusLangEntity> buildSpecification(FilterRequest filter) {
        if (filter == null) {
            return (root, query, cb) -> cb.conjunction();
        }

        boolean hasCriteria = filter.getCriteria() != null && !filter.getCriteria().isEmpty();
        boolean hasGroups = filter.getGroups() != null && !filter.getGroups().isEmpty();
        boolean hasScopes = filter.getScopes() != null && !filter.getScopes().isEmpty();

        if (!hasCriteria && !hasGroups && !hasScopes) {
            return (root, query, cb) -> cb.conjunction();
        }

        return new GenericSpecificationBuilder<AppointmentStatusLangEntity>(AppointmentStatusLanguageFilterConfig.ALLOWED_FIELDS)
                .withCriteria(filter.getCriteria())
                .withGroups(filter.getGroups())
                .withScopes(filter.getScopes())
                .build();
    }
}


