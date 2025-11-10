package com.care.appointment.infrastructure.db.adapter;

import com.care.appointment.domain.model.Beneficiary;
import com.care.appointment.domain.ports.out.beneficiary.BeneficiaryCrudPort;
import com.care.appointment.domain.ports.out.beneficiary.BeneficiarySearchPort;
import com.care.appointment.infrastructure.db.config.BeneficiaryFilterConfig;
import com.care.appointment.infrastructure.db.entities.BeneficiaryEntity;
import com.care.appointment.infrastructure.db.mapper.BeneficiaryJpaMapper;
import com.care.appointment.infrastructure.db.repositories.BeneficiaryRepository;
import com.sharedlib.core.filter.FilterRequest;
import com.sharedlib.core.filter.GenericSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BeneficiaryDbAdapter implements BeneficiaryCrudPort, BeneficiarySearchPort {

    private final BeneficiaryRepository repository;
    private final BeneficiaryJpaMapper mapper;

    @Override
    public Beneficiary save(Beneficiary domain) {
        BeneficiaryEntity entity = mapper.toEntity(domain);
        BeneficiaryEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Beneficiary update(Beneficiary domain) {
        BeneficiaryEntity entity = mapper.toEntity(domain);
        BeneficiaryEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }

    @Override
    public Optional<Beneficiary> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Page<Beneficiary> search(FilterRequest filter, Pageable pageable) {
        Specification<BeneficiaryEntity> spec = buildSpecification(filter);
        return repository.findAll(spec, pageable).map(mapper::toDomain);
    }

    @Override
    public boolean existsByNationalId(String nationalId) {
        if (nationalId == null) return false;
        return repository.existsByNationalId(nationalId);
    }

    @Override
    public boolean existsByMobileNumber(String mobileNumber) {
        if (mobileNumber == null) return false;
        return repository.existsByMobileNumber(mobileNumber);
    }

    @Override
    public Optional<Beneficiary> findByNationalId(String nationalId) {
        return repository.findByNationalId(nationalId).map(mapper::toDomain);
    }

    @Override
    public Optional<Beneficiary> findByMobileNumberAndDateOfBirth(String mobileNumber, LocalDate dateOfBirth) {
        return repository.findByMobileNumberAndDateOfBirth(mobileNumber, dateOfBirth)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Beneficiary> findByMobileNumberAndMotherName(String mobileNumber, String motherName) {
        return repository.findByMobileNumberAndMotherName(mobileNumber, motherName)
                .map(mapper::toDomain);
    }

    @Override
    public List<Beneficiary> findByRegistrationStatusCodeValueId(UUID registrationStatusCodeValueId) {
        return repository.findByRegistrationStatusCodeValueId(registrationStatusCodeValueId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Beneficiary> findByPreferredLanguageCodeValueId(UUID preferredLanguageCodeValueId) {
        return repository.findByPreferredLanguageCodeValueId(preferredLanguageCodeValueId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Beneficiary> findByGenderCodeValueId(UUID genderCodeValueId) {
        return repository.findByGenderCodeValueId(genderCodeValueId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByIsActiveTrueAndIsDeletedFalse() {
        return repository.countByIsActiveTrueAndIsDeletedFalse();
    }

    @Override
    public List<Beneficiary> findByIsActiveTrueAndIsDeletedFalseOrderByCreatedAtDesc() {
        return repository.findByIsActiveTrueAndIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    private Specification<BeneficiaryEntity> buildSpecification(FilterRequest filter) {
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
        return new GenericSpecificationBuilder<BeneficiaryEntity>(BeneficiaryFilterConfig.ALLOWED_FIELDS)
                .withCriteria(filter.getCriteria())
                .withGroups(filter.getGroups())
                .withScopes(filter.getScopes())
                .build();
    }
}

