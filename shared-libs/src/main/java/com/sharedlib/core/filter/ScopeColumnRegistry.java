package com.sharedlib.core.filter;


import java.util.HashMap;
import java.util.Map;

/**
 * Registry for default scope columns per entity type.
 */
public class ScopeColumnRegistry {

    private static final Map<Class<?>, String> DEFAULT_SCOPE_COLUMNS = new HashMap<>();

    private ScopeColumnRegistry() {}

    public static void register(Class<?> entityClass, String scopeColumn) {
        DEFAULT_SCOPE_COLUMNS.put(entityClass, scopeColumn);
    }

    public static String getDefaultScopeColumn(Class<?> entityClass) {
        return DEFAULT_SCOPE_COLUMNS.get(entityClass);
    }
}
