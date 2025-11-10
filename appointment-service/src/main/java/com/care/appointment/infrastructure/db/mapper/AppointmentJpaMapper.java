package com.care.appointment.infrastructure.db.mapper;

import com.care.appointment.domain.model.Appointment;
import com.care.appointment.infrastructure.db.entities.AppointmentEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppointmentJpaMapper {

    AppointmentEntity toEntity(Appointment domain);

    Appointment toDomain(AppointmentEntity entity);

    void updateEntity(@MappingTarget AppointmentEntity target, Appointment source);
}

