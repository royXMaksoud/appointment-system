package com.care.appointment.infrastructure.db.mapper;

import com.care.appointment.domain.model.Holiday;
import com.care.appointment.infrastructure.db.entities.CenterHolidayEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HolidayJpaMapper {

    CenterHolidayEntity toEntity(Holiday domain);

    Holiday toDomain(CenterHolidayEntity entity);

    void updateEntity(@MappingTarget CenterHolidayEntity target, Holiday source);
}

