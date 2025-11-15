package com.care.appointment.web.dto.common;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LookupItemResponse {
    private UUID value;
    private String label;
    private String code;
    private String name;
}

