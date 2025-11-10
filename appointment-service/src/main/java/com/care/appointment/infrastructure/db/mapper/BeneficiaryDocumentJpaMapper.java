package com.care.appointment.infrastructure.db.mapper;

import com.care.appointment.domain.model.BeneficiaryDocument;
import com.care.appointment.infrastructure.db.entities.BeneficiaryDocumentEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BeneficiaryDocumentJpaMapper {

    BeneficiaryDocumentEntity toEntity(BeneficiaryDocument domain);

    BeneficiaryDocument toDomain(BeneficiaryDocumentEntity entity);

    void updateEntity(@MappingTarget BeneficiaryDocumentEntity target, BeneficiaryDocument source);
}

