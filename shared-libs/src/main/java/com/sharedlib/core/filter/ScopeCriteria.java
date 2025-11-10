package com.sharedlib.core.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * Represents a dynamic scope filter, such as "cityId", "tenantId", or "organizationId".
 * If fieldName is null, the service will use the default scope field registered for the entity.
 * Used to restrict query results based on the user's allowed scope values.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ScopeCriteria {

    /**
     * The entity field name to filter by.
     * Example: "tenantId", "organizationBranchId".
     * If null -> the default scope field for the target entity will be used.
     */
    private String fieldName;

    /**
     * The allowed UUID values for the current scope.
     * If null or empty -> no filtering is applied for this scope.
     */
    private List<UUID> allowedValues;

    /** Data type for allowedValues elements */
    @Builder.Default
    private ValueDataType dataType = ValueDataType.UUID;
}
