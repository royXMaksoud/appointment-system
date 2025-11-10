// src/main/java/com/sharedlib/core/dropdown/sql/SqlSimpleCascadeProvider.java
package com.sharedlib.core.dropdown.sql;

import com.sharedlib.core.dropdown.SimpleCascadeProvider;
import com.sharedlib.core.web.dto.IdNameDto;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;
import java.util.stream.Collectors;

public class SqlSimpleCascadeProvider implements SimpleCascadeProvider {

    private final String key;
    private final String sql;
    private final NamedParameterJdbcTemplate jdbc;
    private final Set<String> required;

    public SqlSimpleCascadeProvider(String key,
                                    String sql,
                                    NamedParameterJdbcTemplate jdbc,
                                    Set<String> requiredParams) {
        this.key = Objects.requireNonNull(key, "key");
        this.sql = Objects.requireNonNull(sql, "sql");
        this.jdbc = Objects.requireNonNull(jdbc, "jdbc");
        this.required = Set.copyOf(Objects.requireNonNull(requiredParams, "requiredParams"));
    }

    @Override public String key() { return key; }
    @Override public Set<String> requiredParams() { return required; }

    @Override
    public List<? extends IdNameDto<?>> listChildren(Map<String, Object> params, String lang) {
        // validate params
        List<String> missing = required.stream()
                .filter(r -> params == null || !params.containsKey(r) || params.get(r) == null)
                .toList();
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Missing required params: " + missing);
        }

        // rely on SQL ORDER BY
        return jdbc.query(sql, params, (rs, i) ->
                new IdNameDto<>(
                        rs.getObject("id", java.util.UUID.class),
                        rs.getString("name")
                )
        );
    }
}
