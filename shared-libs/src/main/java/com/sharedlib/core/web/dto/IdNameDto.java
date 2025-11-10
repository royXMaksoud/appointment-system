package com.sharedlib.core.web.dto;

/** Minimal DTO for dropdowns: id + name only. */
public record IdNameDto<T>(T id, String name) {}
