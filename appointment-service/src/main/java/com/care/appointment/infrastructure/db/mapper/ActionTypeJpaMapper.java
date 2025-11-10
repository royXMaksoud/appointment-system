package com.care.appointment.infrastructure.db.mapper;

import com.care.appointment.domain.model.ActionType;
import com.care.appointment.infrastructure.db.entities.AppointmentActionTypeEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ActionTypeJpaMapper {
    
    AppointmentActionTypeEntity toEntity(ActionType domain);
    
    ActionType toDomain(AppointmentActionTypeEntity entity);
    
    void updateEntity(@MappingTarget AppointmentActionTypeEntity target, ActionType source);
}

