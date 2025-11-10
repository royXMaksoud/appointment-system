package com.care.appointment.infrastructure.db.mapper;

import com.care.appointment.domain.model.ServiceType;
import com.care.appointment.infrastructure.db.entities.ServiceTypeEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceTypeJpaMapper {
    
    @Mapping(target = "parentServiceTypeId", source = "parentId")
    ServiceTypeEntity toEntity(ServiceType domain);
    
    @Mapping(target = "parentId", source = "parentServiceTypeId")
    ServiceType toDomain(ServiceTypeEntity entity);
    
    @Mapping(target = "parentServiceTypeId", source = "parentId")
    void updateEntity(@MappingTarget ServiceTypeEntity target, ServiceType source);
}

