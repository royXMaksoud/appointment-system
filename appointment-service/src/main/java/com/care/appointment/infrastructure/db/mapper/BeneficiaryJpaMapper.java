package com.care.appointment.infrastructure.db.mapper;

import com.care.appointment.domain.model.Beneficiary;
import com.care.appointment.infrastructure.db.entities.BeneficiaryEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BeneficiaryJpaMapper {

    BeneficiaryEntity toEntity(Beneficiary domain);

    Beneficiary toDomain(BeneficiaryEntity entity);

    void updateEntity(@MappingTarget BeneficiaryEntity target, Beneficiary source);
}

