package com.sharedlib.core.filter;

// core-shared-lib/src/main/java/com/sharedlib/filter/SearchCriteria.java

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class SearchCriteria {
    /**
     * Field name to filter on (must be whitelisted by the caller service)
     */
    @JsonAlias("field")

    private String key;

    /**
     * Operation type: EQUAL, IN, BETWEEN, LIKE, GT, GTE, LT, LTE, IS_NULL, NOT_EQUAL, etc.
     */
    @JsonAlias({"op","operator"})


    private SearchOperation operation;

    /**
     * Main value for most operations (=, >, <, like, in[as collection], etc.)
     * Type is interpreted using dataType.
     */
    private Object value;

    /**
     * Second value for BETWEEN
     */
    private Object valueTo;

    /**
     * Group identifier for logical grouping (AND/OR + nested)
     */
    private Integer groupId;

    /**
     * Mark if this field is a foreign key (for clarity/documentation; treated like regular fields)
     */
    @Builder.Default
    private boolean foreignKey = false;

    /**
     * Data type hint to safely cast values: STRING, UUID, DATE, ENUM, NUMBER, BOOLEAN, INSTANT, OFFSET_DATE_TIME
     */
    @Builder.Default
    private ValueDataType dataType = ValueDataType.STRING;

    /**
     * Optional: fully-qualified enum class name when dataType == ENUM (e.g., "com.acme.domain.Status")
     */

    private String enumClassFqn;
}
