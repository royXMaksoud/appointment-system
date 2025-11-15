package com.care.appointment.infrastructure.db.mapper;

import com.care.appointment.domain.model.AppointmentStatusHistory;
import com.care.appointment.infrastructure.db.entities.AppointmentStatusHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AppointmentStatusHistoryJpaMapper {

    AppointmentStatusHistoryEntity toEntity(AppointmentStatusHistory domain);

    AppointmentStatusHistory toDomain(AppointmentStatusHistoryEntity entity);

    void updateEntity(@MappingTarget AppointmentStatusHistoryEntity target, AppointmentStatusHistory source);
}

