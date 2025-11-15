package com.care.appointment.infrastructure.db.mapper;

import com.care.appointment.domain.model.AppointmentStatus;
import com.care.appointment.infrastructure.db.entities.AppointmentStatusEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppointmentStatusJpaMapper {

    AppointmentStatusEntity toEntity(AppointmentStatus domain);

    AppointmentStatus toDomain(AppointmentStatusEntity entity);

    void updateEntity(@MappingTarget AppointmentStatusEntity target, AppointmentStatus source);
}


