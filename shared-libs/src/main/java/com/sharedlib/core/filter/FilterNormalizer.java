package com.sharedlib.core.filter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * FilterNormalizer - Pre-processes FilterRequest to normalize values to their expected types.
 *
 * Purpose:
 * - Convert string values to proper types (UUID, Date, Number, Boolean, etc.)
 * - Handle collections in IN operations
 * - Prevent type conversion errors in GenericSpecification
 * - Apply domain-specific normalization rules before filtering
 *
 * Usage:
 * Called at controller level BEFORE passing FilterRequest to GenericSpecification.
 * This ensures type conversions happen early and consistently.
 *
 * Example:
 * <pre>
 *   @PostMapping("/filter")
 *   public ResponseEntity<Page<ScheduleResponse>> filterSchedules(
 *           @RequestBody FilterRequest request,
 *           @PageableDefault(size = 20) Pageable pageable
 *   ) {
 *       FilterRequest safe = (request != null) ? request : new FilterRequest();
 *       FilterNormalizer.normalize(safe);  // Normalize BEFORE processing
 *       Page<Schedule> results = loadAllSchedulesUseCase.loadAll(safe, pageable);
 *       return ResponseEntity.ok(results.map(mapper::toResponse));
 *   }
 * </pre>
 */
public class FilterNormalizer {

    private FilterNormalizer() {
        // Utility class
    }

    /**
     * Normalize a FilterRequest by converting all criteria values to their expected types.
     *
     * @param request the FilterRequest to normalize (modified in-place)
     */
    public static void normalize(FilterRequest request) {
        if (request == null) {
            return;
        }

        // Normalize search criteria
        if (request.getCriteria() != null) {
            for (SearchCriteria criteria : request.getCriteria()) {
                normalizeCriteria(criteria);
            }
        }

        // Normalize scope criteria if present (allowedValues are typically UUIDs)
        if (request.getScopes() != null) {
            for (ScopeCriteria scope : request.getScopes()) {
                normalizeScope(scope);
            }
        }
    }

    /**
     * Normalize a single SearchCriteria by converting its values.
     */
    private static void normalizeCriteria(SearchCriteria criteria) {
        if (criteria == null) {
            return;
        }

        ValueDataType dataType = criteria.getDataType();
        if (dataType == null) {
            dataType = ValueDataType.STRING;
        }

        // Handle IN and NOT_IN operations - convert collection elements
        if (criteria.getOperation() == SearchOperation.IN || criteria.getOperation() == SearchOperation.NOT_IN) {
            if (criteria.getValue() != null) {
                criteria.setValue(normalizeCollectionValue(criteria.getValue(), dataType));
            }
        }
        // Handle BETWEEN operation - convert both values
        else if (criteria.getOperation() == SearchOperation.BETWEEN) {
            if (criteria.getValue() != null) {
                criteria.setValue(normalizeSingleValue(criteria.getValue(), dataType));
            }
            if (criteria.getValueTo() != null) {
                criteria.setValueTo(normalizeSingleValue(criteria.getValueTo(), dataType));
            }
        }
        // Handle all other operations - convert single value
        else if (criteria.getOperation() != SearchOperation.IS_NULL && criteria.getOperation() != SearchOperation.IS_NOT_NULL) {
            if (criteria.getValue() != null) {
                criteria.setValue(normalizeSingleValue(criteria.getValue(), dataType));
            }
        }
    }

    /**
     * Normalize a ScopeCriteria by converting allowedValues to their expected type.
     * ScopeCriteria stores a List of UUID values that should be normalized based on dataType.
     */
    private static void normalizeScope(ScopeCriteria scope) {
        if (scope == null || scope.getAllowedValues() == null || scope.getAllowedValues().isEmpty()) {
            return;
        }

        // Get the dataType, defaulting to UUID
        ValueDataType dataType = scope.getDataType() != null ? scope.getDataType() : ValueDataType.UUID;

        // Normalize each value in allowedValues
        List<Object> normalized = new ArrayList<>();
        for (Object value : scope.getAllowedValues()) {
            Object converted = normalizeSingleValue(value, dataType);
            if (converted != null) {
                normalized.add(converted);
            }
        }

        // Update allowedValues with normalized values
        scope.setAllowedValues(
            normalized.stream()
                .map(v -> v instanceof UUID ? (UUID) v : UUID.fromString(v.toString()))
                .toList()
        );
    }

    /**
     * Normalize a collection value by converting each element to the target type.
     * This handles both Collection and single values.
     */
    private static Object normalizeCollectionValue(Object value, ValueDataType dataType) {
        if (value == null) {
            return null;
        }

        // If already a collection, convert each element
        if (value instanceof Collection<?> col) {
            List<Object> converted = new ArrayList<>();
            for (Object element : col) {
                Object normalized = normalizeSingleValue(element, dataType);
                if (normalized != null) {
                    converted.add(normalized);
                }
            }
            return converted;
        }

        // Single value - wrap in list
        Object normalized = normalizeSingleValue(value, dataType);
        return normalized != null ? List.of(normalized) : List.of();
    }

    /**
     * Normalize a single value to the target data type.
     */
    private static Object normalizeSingleValue(Object value, ValueDataType dataType) {
        if (value == null) {
            return null;
        }

        // If already the correct type, return as-is
        if (isAlreadyCorrectType(value, dataType)) {
            return value;
        }

        try {
            switch (dataType) {
                case UUID:
                    return convertToUuid(value);
                case NUMBER:
                    return new java.math.BigDecimal(value.toString());
                case BOOLEAN:
                    return convertToBoolean(value);
                case DATE:
                    return LocalDate.parse(value.toString());
                case INSTANT:
                    return Instant.parse(value.toString());
                case OFFSET_DATE_TIME:
                    return OffsetDateTime.parse(value.toString());
                case ENUM:
                    return value; // Enum conversion happens in GenericSpecification
                case STRING:
                default:
                    return value.toString();
            }
        } catch (Exception e) {
            // If conversion fails, log and return original value
            // GenericSpecification will handle or report the error
            return value;
        }
    }

    /**
     * Check if a value is already of the expected type.
     */
    private static boolean isAlreadyCorrectType(Object value, ValueDataType dataType) {
        return switch (dataType) {
            case UUID -> value instanceof UUID;
            case NUMBER -> value instanceof Number;
            case BOOLEAN -> value instanceof Boolean;
            case DATE -> value instanceof LocalDate;
            case INSTANT -> value instanceof Instant;
            case OFFSET_DATE_TIME -> value instanceof OffsetDateTime;
            default -> false;
        };
    }

    /**
     * Convert a value to UUID, handling various input formats.
     */
    private static UUID convertToUuid(Object value) {
        if (value instanceof UUID uuid) {
            return uuid;
        }

        if (value instanceof CharSequence sequence) {
            String uuidStr = sequence.toString().trim();
            if (uuidStr.isEmpty()) {
                return null;
            }
            return UUID.fromString(uuidStr);
        }

        if (value instanceof Map<?, ?> map) {
            Object candidate = findValueInMap(map);
            if (candidate != null) {
                return convertToUuid(candidate);
            }
        }

        // Try direct parsing
        try {
            return UUID.fromString(value.toString());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cannot convert '" + value + "' to UUID", e);
        }
    }

    /**
     * Find a UUID value from a map (looks for "id", "uuid", "value" keys).
     */
    private static Object findValueInMap(Map<?, ?> map) {
        Object candidate = map.get("id");
        if (candidate != null) return candidate;

        candidate = map.get("uuid");
        if (candidate != null) return candidate;

        candidate = map.get("value");
        if (candidate != null) return candidate;

        // If single entry, use its value
        if (map.size() == 1) {
            return map.values().iterator().next();
        }

        return null;
    }

    /**
     * Convert a value to Boolean, handling various string formats.
     */
    private static Boolean convertToBoolean(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }

        String str = value.toString().toLowerCase().trim();
        if ("true".equals(str) || "1".equals(str) || "yes".equals(str)) {
            return true;
        }
        if ("false".equals(str) || "0".equals(str) || "no".equals(str)) {
            return false;
        }

        throw new IllegalArgumentException("Cannot convert '" + value + "' to Boolean");
    }
}
