package com.sharedlib.core.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Adds UUID-based scope criteria to a FilterRequest using a provided resolver (no ThreadLocal).
 * This version matches ScopeCriteria.allowedValues type: List<UUID>.
 */
public class ScopeAutoEnricher {

    /** Resolve the allowed UUID values and (optionally) override the scope column. */
    public interface ScopeValueResolver {
        /** Allowed UUIDs for this request (e.g., tenantId/cityId list). */
        List<UUID> resolveAllowedUuidValues();

        /** Optional: override default scope column (if null -> use registry). */
        default String overrideFieldName() { return null; }
    }

    /**
     * Enrich the request with a UUID scope on the entity's default scope column (from ScopeColumnRegistry)
     * or an overridden column provided by the resolver.
     */
    public void enrich(FilterRequest request, Class<?> entityClass, ScopeValueResolver resolver) {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(entityClass, "entityClass must not be null");
        Objects.requireNonNull(resolver, "resolver must not be null");

        // Column: resolver override > registry default
        String column = (resolver.overrideFieldName() != null && !resolver.overrideFieldName().isBlank())
                ? resolver.overrideFieldName()
                : ScopeColumnRegistry.getDefaultScopeColumn(entityClass);
        if (column == null || column.isBlank()) return;

        List<UUID> allowed = resolver.resolveAllowedUuidValues();
        if (allowed == null || allowed.isEmpty()) return;

        // Append to request.scopes (non-destructive)
        var scopes = request.getScopes() != null ? new ArrayList<>(request.getScopes()) : new ArrayList<ScopeCriteria>();
        scopes.add(ScopeCriteria.builder()
                .fieldName(column)
                .allowedValues(new ArrayList<>(allowed)) // List<UUID> — matches ScopeCriteria
                .dataType(ValueDataType.UUID)            // keep data type consistent
                .build());
        request.setScopes(scopes);
    }
}
