package com.care.appointment.application.document.mapper;

import com.care.appointment.application.document.command.CreateBeneficiaryDocumentCommand;
import com.care.appointment.application.document.command.UpdateBeneficiaryDocumentCommand;
import com.care.appointment.domain.model.BeneficiaryDocument;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BeneficiaryDocumentDomainMapper {

    @Mapping(target = "documentId", ignore = true)
    @Mapping(target = "legacyDocumentType", source = "documentTypeCode")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "updatedById", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rowVersion", ignore = true)
    BeneficiaryDocument toDomain(CreateBeneficiaryDocumentCommand command);

    @Mapping(target = "beneficiaryId", ignore = true)
    @Mapping(target = "legacyDocumentType", source = "documentTypeCode")
    @Mapping(target = "createdById", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rowVersion", ignore = true)
    void updateFromCommand(@MappingTarget BeneficiaryDocument domain, UpdateBeneficiaryDocumentCommand command);
}

