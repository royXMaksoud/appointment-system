package com.sharedlib.core.filter;

// shared-lib: com.sharedlib.core.filtermeta.FilterMeta.java


import com.sharedlib.core.filter.SearchOperation;
import com.sharedlib.core.filter.ValueDataType;
import java.util.List;
import java.util.Map;

public record FilterMeta(
        int defaultPageSize,
        List<FieldMeta> fields,                 // ["systemId": UUID, "code": STRING, ...]
        List<String> sortable,                  // ["code","name","createdAt","isActive"]
        Map<ValueDataType, List<SearchOperation>> operatorMatrix //
) {
    public record FieldMeta(String key, ValueDataType dataType, String labelKey) {}
}
