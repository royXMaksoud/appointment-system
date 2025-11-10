package com.care.appointment.infrastructure.db.adapter;

import com.care.appointment.domain.model.BeneficiaryDocument;
import com.care.appointment.domain.ports.in.document.BeneficiaryDocumentCrudPort;
import com.care.appointment.domain.ports.in.document.BeneficiaryDocumentSearchPort;
import com.care.appointment.infrastructure.db.entities.BeneficiaryDocumentEntity;
import com.care.appointment.infrastructure.db.mapper.BeneficiaryDocumentJpaMapper;
import com.care.appointment.infrastructure.db.repositories.BeneficiaryDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BeneficiaryDocumentDbAdapter implements BeneficiaryDocumentCrudPort, BeneficiaryDocumentSearchPort {

    private final BeneficiaryDocumentRepository repository;
    private final BeneficiaryDocumentJpaMapper mapper;

    @Override
    public BeneficiaryDocument save(BeneficiaryDocument domain) {
        BeneficiaryDocumentEntity entity = mapper.toEntity(domain);
        BeneficiaryDocumentEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public BeneficiaryDocument update(BeneficiaryDocument domain) {
        BeneficiaryDocumentEntity entity = mapper.toEntity(domain);
        BeneficiaryDocumentEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }

    @Override
    public Optional<BeneficiaryDocument> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public List<BeneficiaryDocument> findByBeneficiaryId(UUID beneficiaryId) {
        return repository.findByBeneficiaryIdAndIsDeletedFalse(beneficiaryId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BeneficiaryDocument> findActiveByBeneficiaryId(UUID beneficiaryId) {
        return repository.findByBeneficiaryIdAndIsActiveTrueAndIsDeletedFalse(beneficiaryId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BeneficiaryDocument> findByBeneficiaryIdAndDocumentType(UUID beneficiaryId, String documentType) {
        return repository.findByBeneficiaryIdAndDocumentType(beneficiaryId, documentType)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BeneficiaryDocument> findByStorageKey(String storageKey) {
        return repository.findByStorageKey(storageKey).map(mapper::toDomain);
    }

    @Override
    public long countByBeneficiaryId(UUID beneficiaryId) {
        return repository.countByBeneficiaryId(beneficiaryId);
    }
}

