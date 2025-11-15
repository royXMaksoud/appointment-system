package com.care.appointment.infrastructure.client.dto;

import lombok.Data;

import java.util.UUID;

/**
 * Lightweight view of a user record fetched from auth-service.
 */
@Data
public class UserSummary {
    private UUID id;
    private String firstName;
    private String fatherName;
    private String surName;
    private String fullName;
    private String emailAddress;
}

