package com.care.appointment.infrastructure.db.mapper;

import com.care.appointment.domain.model.ServiceTypeLanguage;
import com.care.appointment.infrastructure.db.entities.ServiceTypeLangEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceTypeLanguageJpaMapper {

    @Mapping(target = "serviceType", ignore = true)
    ServiceTypeLangEntity toEntity(ServiceTypeLanguage domain);

    ServiceTypeLanguage toDomain(ServiceTypeLangEntity entity);

    @Mapping(target = "serviceType", ignore = true)
    void updateEntity(@MappingTarget ServiceTypeLangEntity target, ServiceTypeLanguage source);
}


