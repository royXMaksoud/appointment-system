package com.sharedlib.core.filter;

// core-shared-lib/src/main/java/com/sharedlib/filter/SearchOperation.java

public enum SearchOperation {
    EQUAL,
    NOT_EQUAL,
    GREATER_THAN,        // >
    GREATER_THAN_EQUAL,  // >=
    LESS_THAN,           // <
    LESS_THAN_EQUAL,     // <=
    BETWEEN,
    LIKE,
    STARTS_WITH,
    ENDS_WITH,
    IN,
    NOT_IN,
    IS_NULL,
    IS_NOT_NULL
}
