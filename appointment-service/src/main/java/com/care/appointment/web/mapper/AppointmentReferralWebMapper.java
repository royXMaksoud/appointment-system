package com.care.appointment.web.mapper;

import com.care.appointment.application.referral.command.CreateAppointmentReferralCommand;
import com.care.appointment.application.referral.command.UpdateAppointmentReferralCommand;
import com.care.appointment.domain.model.AppointmentReferral;
import com.care.appointment.web.dto.admin.referral.AppointmentReferralResponse;
import com.care.appointment.web.dto.admin.referral.CreateAppointmentReferralRequest;
import com.care.appointment.web.dto.admin.referral.UpdateAppointmentReferralRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AppointmentReferralWebMapper {

    AppointmentReferralResponse toResponse(AppointmentReferral referral);

    CreateAppointmentReferralCommand toCreateCommand(CreateAppointmentReferralRequest request);

    @Mapping(target = "referralId", source = "referralId")
    UpdateAppointmentReferralCommand toUpdateCommand(UUID referralId, UpdateAppointmentReferralRequest request);
}

