package com.sharedlib.core.filter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

/**
 * STEP 1 - Purpose of this class:
 * This is a generic reusable service that executes dynamic filtering for any entity type T.
 * It bridges between the filtering request (criteria, scopes, groups) and the JPA repository layer.
 *
 * When to use:
 * - Whenever you want to apply dynamic filtering, pagination, and sorting without rewriting query logic
 * - In any microservice where you need advanced filtering on entities
 *
 * How it fits in execution flow:
 * 1. Controller receives FilterRequest from the API.
 * 2. Service layer calls GenericFilterService.filter(...) passing search criteria, scopes, and groups.
 * 3. GenericSpecificationBuilder creates a Specification<T> object based on the given conditions.
 * 4. The repository executes the built Specification with pagination (Pageable).
 * 5. Results are returned as a Page<T> to the caller.
 */
public class GenericFilterService<T> {

    /**
     * STEP 2 - repository:
     * The JPA repository for the entity T that must extend JpaSpecificationExecutor<T>.
     * This allows the repository to execute dynamic Specifications.
     */
    private final JpaSpecificationExecutor<T> repository;

    /**
     * STEP 3 - allowedFields:
     * A whitelist of fields that can be used for filtering.
     * This prevents filtering on unauthorized or non-existent fields (SQL injection protection).
     */
    private final Set<String> allowedFields;

    /**
     * STEP 4 - Constructor:
     * Initializes the service with a repository and allowed fields.
     */
    public GenericFilterService(JpaSpecificationExecutor<T> repository, Set<String> allowedFields) {
        this.repository = repository;
        this.allowedFields = allowedFields;
    }

    /**
     * STEP 5 - filter method:
     * Executes dynamic filtering with pagination.
     *
     * Parameters:
     * - criteria: Flat list of search conditions (SearchCriteria)
     * - scopes: Scope-based filtering rules (ScopeCriteria)
     * - groups: Logical filter groups (FilterGroup) for AND/OR conditions
     * - pageable: Pagination and sorting configuration
     *
     * Steps executed inside:
     * 1. Create a GenericSpecificationBuilder instance with allowed fields.
     * 2. Pass criteria, scopes, and groups to the builder.
     * 3. Build a Specification<T> from the provided conditions.
     * 4. Execute repository.findAll(spec, pageable) to get paginated results.
     */
    public Page<T> filter(List<SearchCriteria> criteria,
                          List<ScopeCriteria> scopes,
                          List<FilterGroup> groups,
                          Pageable pageable) {

        Specification<T> spec = new GenericSpecificationBuilder<T>(allowedFields)
                .withCriteria(criteria)
                .withScopes(scopes)
                .withGroups(groups)
                .build();

        return repository.findAll(spec, pageable);
    }
    public Page<T> filter(FilterRequest request, Pageable pageable) {
        return filter(
                request != null ? request.getCriteria() : null,
                request != null ? request.getScopes()   : null,
                request != null ? request.getGroups()   : null,
                pageable
        );
    }

}
