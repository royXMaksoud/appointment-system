package com.care.appointment.infrastructure.db.mapper;

import com.care.appointment.domain.model.ActionTypeLanguage;
import com.care.appointment.infrastructure.db.entities.AppointmentActionTypeLangEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ActionTypeLanguageJpaMapper {

    @Mapping(target = "actionType", ignore = true)
    AppointmentActionTypeLangEntity toEntity(ActionTypeLanguage domain);

    ActionTypeLanguage toDomain(AppointmentActionTypeLangEntity entity);

    @Mapping(target = "actionType", ignore = true)
    void updateEntity(@MappingTarget AppointmentActionTypeLangEntity target, ActionTypeLanguage source);
}


