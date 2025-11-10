package com.care.appointment.application.family.mapper;

import com.care.appointment.application.family.command.CreateFamilyMemberCommand;
import com.care.appointment.application.family.command.UpdateFamilyMemberCommand;
import com.care.appointment.domain.model.FamilyMember;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FamilyMemberDomainMapper {

    @Mapping(target = "familyMemberId", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rowVersion", ignore = true)
    FamilyMember toDomain(CreateFamilyMemberCommand command);

    @Mapping(target = "beneficiaryId", ignore = true)
    @Mapping(target = "nationalId", ignore = true)
    @Mapping(target = "createdById", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rowVersion", ignore = true)
    void updateFromCommand(@MappingTarget FamilyMember domain, UpdateFamilyMemberCommand command);
}

