package com.care.appointment.application.common.service;

import com.care.appointment.infrastructure.client.AuthServiceClient;
import com.care.appointment.infrastructure.client.dto.UserSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Helper service that resolves user IDs into human-readable display names by calling auth-service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDirectoryService {

    private final AuthServiceClient authServiceClient;

    /**
     * Resolves the provided user IDs into a map of {@code userId -> displayName}.
     * Missing users are skipped gracefully.
     */
    public Map<UUID, String> getDisplayNames(Collection<UUID> userIds) {
        Map<UUID, String> result = new HashMap<>();
        if (userIds == null || userIds.isEmpty()) {
            return result;
        }

        userIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .forEach(userId -> {
                    try {
                        UserSummary summary = authServiceClient.getUserById(userId);
                        if (summary != null) {
                            result.put(userId, determineDisplayName(summary));
                        }
                    } catch (Exception ex) {
                        log.warn("Unable to resolve user name for id {}: {}", userId, ex.getMessage());
                    }
                });

        return result;
    }

    private String determineDisplayName(UserSummary summary) {
        if (summary == null) {
            return null;
        }
        if (StringUtils.hasText(summary.getFullName())) {
            return summary.getFullName();
        }

        StringBuilder builder = new StringBuilder();
        if (StringUtils.hasText(summary.getFirstName())) {
            builder.append(summary.getFirstName());
        }
        if (StringUtils.hasText(summary.getFatherName())) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(summary.getFatherName());
        }
        if (StringUtils.hasText(summary.getSurName())) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(summary.getSurName());
        }
        if (builder.length() > 0) {
            return builder.toString();
        }
        if (StringUtils.hasText(summary.getEmailAddress())) {
            return summary.getEmailAddress();
        }
        return summary.getId() != null ? summary.getId().toString() : "Unknown User";
    }
}

