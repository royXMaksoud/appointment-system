package com.ftp.authservice.application.dto.permissions;

import java.time.Instant;
import java.util.List;

/**
 * What the frontend consumes. Include an ETag so clients can cache and revalidate.
 */
public record PermissionTree(String etag, Instant generatedAt, List<SystemDTO> systems) {}
