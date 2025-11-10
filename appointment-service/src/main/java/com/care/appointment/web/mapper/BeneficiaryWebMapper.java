package com.care.appointment.web.mapper;

import com.care.appointment.application.beneficiary.command.CreateBeneficiaryCommand;
import com.care.appointment.application.beneficiary.command.UpdateBeneficiaryCommand;
import com.care.appointment.domain.model.Beneficiary;
import com.care.appointment.web.dto.BeneficiaryDTO;
import com.care.appointment.web.dto.admin.beneficiary.BeneficiaryResponse;
import com.care.appointment.web.dto.admin.beneficiary.CreateBeneficiaryRequest;
import com.care.appointment.web.dto.admin.beneficiary.UpdateBeneficiaryRequest;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BeneficiaryWebMapper {

    CreateBeneficiaryCommand toCreateCommand(CreateBeneficiaryRequest request);

    @Mapping(target = "beneficiaryId", source = "beneficiaryId")
    UpdateBeneficiaryCommand toUpdateCommand(UUID beneficiaryId, UpdateBeneficiaryRequest request);

    BeneficiaryResponse toResponse(Beneficiary domain);
    
    /**
     * Maps domain model to DTO for mobile API
     */
    BeneficiaryDTO toDTO(Beneficiary domain);
}

