package com.sharedlib.core.filter;

import org.springframework.data.jpa.domain.Specification;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Builds a JPA Specification<T> from criteria, scope rules, and groups.
 * - Safe merging when spec is null
 * - Scope uses its declared dataType
 * - Strict whitelist validation (exact keys, supports dotted paths if whitelisted as-is)
 * - Robust handling for empty groups and nulls
 */
public class GenericSpecificationBuilder<T> {

    // Flat field-level criteria
    private final List<SearchCriteria> criteriaList = new ArrayList<>();

    // Permission-based filters (scope)
    private final List<ScopeCriteria> scopeList = new ArrayList<>();

    // Logical groups (AND/OR) by groupId
    private final List<FilterGroup> groupList = new ArrayList<>();

    // Whitelist of allowed field names (must match exactly; include dotted paths if needed)
    private final Set<String> allowedFields;

    public GenericSpecificationBuilder(Set<String> allowedFields) {
        this.allowedFields = (allowedFields != null) ? Set.copyOf(allowedFields) : Set.of();
    }

    /** Add simple filtering criteria. */
    public GenericSpecificationBuilder<T> withCriteria(List<SearchCriteria> criteria) {
        if (criteria != null && !criteria.isEmpty()) {
            this.criteriaList.addAll(criteria);
        }
        return this;
    }

    /** Add scope-based rules (permissions). */
    public GenericSpecificationBuilder<T> withScopes(List<ScopeCriteria> scopes) {
        if (scopes != null && !scopes.isEmpty()) {
            this.scopeList.addAll(scopes);
        }
        return this;
    }

    /** Add groups defining how to combine criteria (AND/OR). */
    public GenericSpecificationBuilder<T> withGroups(List<FilterGroup> groups) {
        if (groups != null && !groups.isEmpty()) {
            this.groupList.addAll(groups);
        }
        return this;
    }

    /** Build the final Specification<T>. */
    public Specification<T> build() {
        // (1) Validate all fields against whitelist
        validateFields();

        // Start with null; merge safely via helper
        Specification<T> spec = null;

        // (2) Apply scopes first (AND all scopes)
        for (ScopeCriteria scope : scopeList) {
            if (scope == null) continue;
            List<?> values = normalizeScopeValues(scope.getAllowedValues());
            if (values.isEmpty()) continue;

            SearchCriteria scopeCriteria = SearchCriteria.builder()
                    .key(scope.getFieldName())
                    .operation(SearchOperation.IN)
                    .value(values)
                    .dataType(scope.getDataType()) // <- use declared data type
                    .build();

            spec = and(spec, new GenericSpecification<>(scopeCriteria));
        }

        // (3) Group criteria by groupId (null => 0)
        Map<Integer, List<SearchCriteria>> groupedCriteria = criteriaList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(c -> Optional.ofNullable(c.getGroupId()).orElse(0)));

        // (4) Build each group's spec (AND inside the group), then merge by group's LogicType with main spec
        for (Map.Entry<Integer, List<SearchCriteria>> entry : groupedCriteria.entrySet()) {
            Integer groupId = entry.getKey();
            List<SearchCriteria> groupCriteria = entry.getValue();
            if (groupCriteria == null || groupCriteria.isEmpty()) continue;

            Specification<T> groupSpec = null;
            for (SearchCriteria c : groupCriteria) {
                if (c == null) continue;
                groupSpec = and(groupSpec, new GenericSpecification<>(c));
            }
            if (groupSpec == null) continue;

            LogicType logicType = groupList.stream()
                    .filter(g -> g != null && g.getGroupId() != null && g.getGroupId().equals(groupId))
                    .map(FilterGroup::getLogicType)
                    .findFirst()
                    .orElse(LogicType.AND);

            spec = (logicType == LogicType.AND) ? and(spec, groupSpec) : or(spec, groupSpec);
        }

        // spec may stay null => caller repo.findAll(spec, pageable) will treat as no filters
        return spec;
    }

    /** Validate that all used fields are explicitly whitelisted. */
    private void validateFields() {
        for (SearchCriteria c : criteriaList) {
            if (c == null) continue;
            String key = c.getKey();
            if (key == null || !allowedFields.contains(key)) {
                throw new IllegalArgumentException("Field not allowed for filtering: " + key);
            }
        }
        for (ScopeCriteria s : scopeList) {
            if (s == null) continue;
            String key = s.getFieldName();
            if (key == null || !allowedFields.contains(key)) {
                throw new IllegalArgumentException("Scope field not allowed: " + key);
            }
        }
    }

    /* ---- Safe merge helpers ---- */

    private List<Object> normalizeScopeValues(List<?> rawValues) {
        if (rawValues == null || rawValues.isEmpty()) {
            return List.of();
        }
        return rawValues.stream()
                .filter(Objects::nonNull)
                .flatMap(this::flattenScopeValue)
                .map(this::trimString)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private Stream<?> flattenScopeValue(Object value) {
        if (value == null) {
            return Stream.empty();
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream().filter(Objects::nonNull).flatMap(this::flattenScopeValue);
        }
        if (value instanceof Object[] array) {
            return Arrays.stream(array).filter(Objects::nonNull).flatMap(this::flattenScopeValue);
        }
        if (value instanceof String str) {
            return Arrays.stream(str.split("[,\\s]+"))
                    .map(String::trim)
                    .filter(token -> !token.isEmpty());
        }
        return Stream.of(value);
    }

    private Object trimString(Object value) {
        if (value instanceof String str) {
            String trimmed = str.trim();
            return trimmed.isEmpty() ? null : trimmed;
        }
        return value;
    }


    /** spec AND other (null-safe) */
    private Specification<T> and(Specification<T> base, Specification<T> other) {
        if (other == null) return base;
        return (base == null) ? other : base.and(other);
    }

    /** spec OR other (null-safe) */
    private Specification<T> or(Specification<T> base, Specification<T> other) {
        if (other == null) return base;
        return (base == null) ? other : base.or(other);
    }
}
