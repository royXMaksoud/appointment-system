package com.ftp.authservice.application.dto.permissions;

import java.util.List;
import java.util.UUID;

/** A system groups many sections. */
public record SystemDTO(UUID systemId, String name, List<SectionDTO> sections) {}
