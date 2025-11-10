package com.ftp.authservice.domain.model;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserProfile {
    private UUID userProfileId;
    private UUID userId;
    private UUID tenantId;
    private UUID companyId;
    private UUID countryId;
    private UUID cityId;
    private String preferredLanguage;
    private Instant createdAt;
    private Instant updatedAt;
    private Long rowVersion;
}
