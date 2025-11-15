package com.care.appointment.application.appointmentstatuslanguage.command;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class UpdateAppointmentStatusLanguageCommand {
    UUID appointmentStatusLanguageId;
    UUID appointmentStatusId;
    String languageCode;
    String name;
    Boolean isActive;
    Boolean isDeleted;
}


