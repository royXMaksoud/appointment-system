package com.ftp.authservice.application.dto.permissions;

import java.util.List;
import java.util.UUID;

/** A section groups many actions. */
public record SectionDTO(UUID systemSectionId, String name, List<ActionDTO> actions) {}
