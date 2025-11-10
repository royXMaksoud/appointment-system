package com.sharedlib.core.filter;

// shared-lib: com.sharedlib.core.filtermeta.FilterMetaFactory.java


import com.sharedlib.core.filter.SearchOperation;
import com.sharedlib.core.filter.ValueDataType;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

public final class FilterMetaFactory {

    private FilterMetaFactory() {}


    public static final Map<ValueDataType, List<SearchOperation>> DEFAULT_MATRIX = Map.of(
            ValueDataType.STRING,          List.of(SearchOperation.EQUAL, SearchOperation.LIKE,
                    SearchOperation.STARTS_WITH, SearchOperation.ENDS_WITH, SearchOperation.IN),
            ValueDataType.UUID,            List.of(SearchOperation.EQUAL, SearchOperation.IN),
            ValueDataType.BOOLEAN,         List.of(SearchOperation.EQUAL),
            ValueDataType.NUMBER,          List.of(SearchOperation.EQUAL, SearchOperation.GREATER_THAN,
                    SearchOperation.GREATER_THAN_EQUAL, SearchOperation.LESS_THAN,
                    SearchOperation.LESS_THAN_EQUAL, SearchOperation.BETWEEN),
            ValueDataType.DATE,            List.of(SearchOperation.BETWEEN, SearchOperation.GREATER_THAN_EQUAL,
                    SearchOperation.LESS_THAN_EQUAL),
            ValueDataType.INSTANT,         List.of(SearchOperation.BETWEEN, SearchOperation.GREATER_THAN_EQUAL,
                    SearchOperation.LESS_THAN_EQUAL),
            ValueDataType.OFFSET_DATE_TIME,List.of(SearchOperation.BETWEEN, SearchOperation.GREATER_THAN_EQUAL,
                    SearchOperation.LESS_THAN_EQUAL),
            ValueDataType.ENUM,            List.of(SearchOperation.EQUAL, SearchOperation.IN)
    );

    public static FilterMeta build(
            Class<?> entityClass,
            Set<String> allowedFields,
            List<String> sortable,
            int defaultPageSize,
            Map<String, ValueDataType> overrides,
            Map<ValueDataType, List<SearchOperation>> matrix
    ) {
        Map<String, ValueDataType> types = resolveTypes(entityClass, allowedFields, overrides);
        var fields = types.entrySet().stream()
                .map(e -> new FilterMeta.FieldMeta(
                        e.getKey(),
                        e.getValue(),
                        "system.fields." + e.getKey()
                )).collect(Collectors.toList());

        return new FilterMeta(
                defaultPageSize,
                fields,
                (sortable != null ? sortable : List.of()),
                (matrix != null ? matrix : DEFAULT_MATRIX)
        );
    }

    private static Map<String, ValueDataType> resolveTypes(Class<?> entityClass,
                                                           Set<String> allowedFields,
                                                           Map<String, ValueDataType> overrides) {
        Map<String, ValueDataType> out = new LinkedHashMap<>();
        for (String key : allowedFields) {
            ValueDataType t = (overrides != null && overrides.containsKey(key))
                    ? overrides.get(key)
                    : inferType(entityClass, key);
            out.put(key, t);
        }
        return out;
    }

    private static ValueDataType inferType(Class<?> entityClass, String key) {

        if (key.contains(".")) return ValueDataType.STRING;
        try {
            Field f = entityClass.getDeclaredField(key);
            Class<?> t = f.getType();
            if (t == String.class) return ValueDataType.STRING;
            if (t == UUID.class) return ValueDataType.UUID;
            if (t == Boolean.class || t == boolean.class) return ValueDataType.BOOLEAN;
            if (Number.class.isAssignableFrom(t) ||
                    t == int.class || t == long.class || t == double.class || t == float.class
            ) return ValueDataType.NUMBER;
            if (t == LocalDate.class) return ValueDataType.DATE;
            if (t == Instant.class) return ValueDataType.INSTANT;
            if (t == OffsetDateTime.class) return ValueDataType.OFFSET_DATE_TIME;
            if (t.isEnum()) return ValueDataType.ENUM;
            return ValueDataType.STRING;
        } catch (NoSuchFieldException e) {
            return ValueDataType.STRING;
        }
    }
}
