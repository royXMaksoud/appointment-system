package com.care.appointment.infrastructure.db.adapter;

import com.care.appointment.domain.model.FamilyMember;
import com.care.appointment.domain.ports.in.family.FamilyMemberCrudPort;
import com.care.appointment.domain.ports.in.family.FamilyMemberSearchPort;
import com.care.appointment.infrastructure.db.entities.FamilyMemberEntity;
import com.care.appointment.infrastructure.db.mapper.FamilyMemberJpaMapper;
import com.care.appointment.infrastructure.db.repositories.FamilyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FamilyMemberDbAdapter implements FamilyMemberCrudPort, FamilyMemberSearchPort {

    private final FamilyMemberRepository repository;
    private final FamilyMemberJpaMapper mapper;

    @Override
    public FamilyMember save(FamilyMember domain) {
        FamilyMemberEntity entity = mapper.toEntity(domain);
        FamilyMemberEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public FamilyMember update(FamilyMember domain) {
        FamilyMemberEntity entity = mapper.toEntity(domain);
        FamilyMemberEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }

    @Override
    public Optional<FamilyMember> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public List<FamilyMember> findByBeneficiaryId(UUID beneficiaryId) {
        return repository.findByBeneficiaryIdAndIsDeletedFalse(beneficiaryId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<FamilyMember> findActiveByBeneficiaryId(UUID beneficiaryId) {
        return repository.findByBeneficiaryIdAndIsActiveTrueAndIsDeletedFalse(beneficiaryId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<FamilyMember> findEmergencyContactsByBeneficiaryId(UUID beneficiaryId) {
        return repository.findByBeneficiaryIdAndIsEmergencyContactTrue(beneficiaryId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FamilyMember> findByNationalId(String nationalId) {
        return repository.findByNationalId(nationalId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByNationalId(String nationalId) {
        return repository.existsByNationalId(nationalId);
    }

    @Override
    public long countByBeneficiaryId(UUID beneficiaryId) {
        return repository.countByBeneficiaryId(beneficiaryId);
    }
}

