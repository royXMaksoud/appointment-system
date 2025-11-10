package com.sharedlib.core.filter;

// core-shared-lib/src/main/java/com/sharedlib/filter/FilterGroup.java

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterGroup {
    /**
     * All SearchCriteria with the same groupId belong to this group.
     */
    private Integer groupId;

    /**
     * How to combine the criteria in this group: AND / OR
     */
    private LogicType logicType;
}
