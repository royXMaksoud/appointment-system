package com.care.appointment.infrastructure.db.mapper;

import com.care.appointment.domain.model.AppointmentReferral;
import com.care.appointment.infrastructure.db.entities.AppointmentReferralEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppointmentReferralJpaMapper {
    AppointmentReferralEntity toEntity(AppointmentReferral domain);
    AppointmentReferral toDomain(AppointmentReferralEntity entity);
    void updateEntity(@MappingTarget AppointmentReferralEntity target, AppointmentReferral source);
}

