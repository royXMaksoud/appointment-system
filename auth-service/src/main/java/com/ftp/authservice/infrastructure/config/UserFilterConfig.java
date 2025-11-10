package com.ftp.authservice.infrastructure.config;

import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

// com.ftp.authservice.infrastructure.config.UserFilterConfig
// com.ftp.authservice.infrastructure.config.UserFilterConfig
public final class UserFilterConfig {
    public static final Set<String> ALLOWED_FIELDS = Set.of(
            "userId",
            "firstName",
            "fatherName",
            "surName",
            "fullName",   // only if it's an actual column on UserJpaEntity
            "email",      // <-- change from emailAddress to email
            "language",
            "type",
            "enabled",
            "deleted",
            "createdAt",
            "createdById",
            "updatedAt",
            "updatedById"
    );

    public static final List<String> SORTABLE = List.of(
            "firstName",
            "surName",
            "fullName",   // only if it’s real
            "email",      // <-- here too
            "language",
            "type",
            "createdAt"
    );

    public static final int DEFAULT_PAGE_SIZE = 10;
}
