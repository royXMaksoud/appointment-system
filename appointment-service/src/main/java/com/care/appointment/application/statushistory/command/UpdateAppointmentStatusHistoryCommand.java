package com.care.appointment.application.statushistory.command;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class UpdateAppointmentStatusHistoryCommand {
    UUID historyId;
    UUID appointmentStatusId;
    UUID changedByUserId;
    String reason;
}

