package com.care.appointment.web.mapper;

import com.care.appointment.application.family.command.CreateFamilyMemberCommand;
import com.care.appointment.application.family.command.UpdateFamilyMemberCommand;
import com.care.appointment.domain.model.FamilyMember;
import com.care.appointment.web.dto.FamilyMemberDTO;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FamilyMemberWebMapper {

    CreateFamilyMemberCommand toCreateCommand(FamilyMemberDTO request);

    @Mapping(target = "familyMemberId", source = "familyMemberId")
    UpdateFamilyMemberCommand toUpdateCommand(UUID familyMemberId, FamilyMemberDTO request);

    FamilyMemberDTO toDTO(FamilyMember domain);
}

