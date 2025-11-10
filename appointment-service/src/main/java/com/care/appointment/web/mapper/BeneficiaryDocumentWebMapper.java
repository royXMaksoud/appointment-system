package com.care.appointment.web.mapper;

import com.care.appointment.application.document.command.CreateBeneficiaryDocumentCommand;
import com.care.appointment.application.document.command.UpdateBeneficiaryDocumentCommand;
import com.care.appointment.domain.model.BeneficiaryDocument;
import com.care.appointment.web.dto.BeneficiaryDocumentDTO;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BeneficiaryDocumentWebMapper {

    CreateBeneficiaryDocumentCommand toCreateCommand(BeneficiaryDocumentDTO request);

    @Mapping(target = "documentId", source = "documentId")
    UpdateBeneficiaryDocumentCommand toUpdateCommand(UUID documentId, BeneficiaryDocumentDTO request);

    BeneficiaryDocumentDTO toDTO(BeneficiaryDocument domain);
}

