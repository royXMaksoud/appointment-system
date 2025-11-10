package com.care.appointment.infrastructure.db.mapper;

import com.care.appointment.domain.model.FamilyMember;
import com.care.appointment.infrastructure.db.entities.FamilyMemberEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FamilyMemberJpaMapper {

    FamilyMemberEntity toEntity(FamilyMember domain);

    FamilyMember toDomain(FamilyMemberEntity entity);

    void updateEntity(@MappingTarget FamilyMemberEntity target, FamilyMember source);
}

