package com.care.appointment.infrastructure.db.mapper;

import com.care.appointment.domain.model.AppointmentStatusLanguage;
import com.care.appointment.infrastructure.db.entities.AppointmentStatusLangEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppointmentStatusLanguageJpaMapper {

    @Mapping(target = "appointmentStatus", ignore = true)
    AppointmentStatusLangEntity toEntity(AppointmentStatusLanguage domain);

    AppointmentStatusLanguage toDomain(AppointmentStatusLangEntity entity);

    @Mapping(target = "appointmentStatus", ignore = true)
    void updateEntity(@MappingTarget AppointmentStatusLangEntity target, AppointmentStatusLanguage source);
}


