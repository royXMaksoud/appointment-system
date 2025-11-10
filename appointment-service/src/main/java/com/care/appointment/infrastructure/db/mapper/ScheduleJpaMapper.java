package com.care.appointment.infrastructure.db.mapper;

import com.care.appointment.domain.model.Schedule;
import com.care.appointment.infrastructure.db.entities.CenterWeeklyScheduleEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScheduleJpaMapper {

    CenterWeeklyScheduleEntity toEntity(Schedule domain);

    Schedule toDomain(CenterWeeklyScheduleEntity entity);

    void updateEntity(@MappingTarget CenterWeeklyScheduleEntity target, Schedule source);
}

