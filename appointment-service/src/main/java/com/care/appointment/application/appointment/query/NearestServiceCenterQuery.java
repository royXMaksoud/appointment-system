package com.care.appointment.application.appointment.query;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class NearestServiceCenterQuery {
    UUID beneficiaryId;
    UUID serviceTypeId;
    Double latitudeOverride;
    Double longitudeOverride;
    Integer limit;
    Integer searchWindowDays;
}



