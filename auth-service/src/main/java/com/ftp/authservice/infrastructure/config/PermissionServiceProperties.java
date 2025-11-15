package com.ftp.authservice.infrastructure.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds configuration under "permission.*" (see application.yml).
 * Keep external URLs here so moving to AWS is a one-line change.
 */
@Data
@ConfigurationProperties(prefix = "permission")
public class PermissionServiceProperties {
    private String baseUrl;             // e.g. http://localhost:6062 or the internal AWS url
    private String userPermissionsPath; // e.g. /api/permissions/users
    private String internalKey;         // e.g. dev-internal-key for inter-service auth
    private int connectTimeoutMs;       // client connection timeout
    private int readTimeoutMs;          // read timeout
}
