package com.care.appointment.infrastructure.db.adapter;

import com.care.appointment.domain.model.Schedule;
import com.care.appointment.domain.ports.out.schedule.ScheduleCrudPort;
import com.care.appointment.domain.ports.out.schedule.ScheduleSearchPort;
import com.care.appointment.infrastructure.db.config.ScheduleFilterConfig;
import com.care.appointment.infrastructure.db.entities.CenterWeeklyScheduleEntity;
import com.care.appointment.infrastructure.db.mapper.ScheduleJpaMapper;
import com.care.appointment.infrastructure.db.repositories.CenterWeeklyScheduleRepository;
import com.sharedlib.core.filter.FilterRequest;
import com.sharedlib.core.filter.GenericSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleDbAdapter implements ScheduleCrudPort, ScheduleSearchPort {

    private final CenterWeeklyScheduleRepository repository;
    private final ScheduleJpaMapper mapper;

    @Override
    public Schedule save(Schedule domain) {
        CenterWeeklyScheduleEntity entity = mapper.toEntity(domain);
        CenterWeeklyScheduleEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Schedule update(Schedule domain) {
        CenterWeeklyScheduleEntity entity = mapper.toEntity(domain);
        CenterWeeklyScheduleEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }

    @Override
    public Optional<Schedule> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Page<Schedule> search(FilterRequest filter, Pageable pageable) {
        log.debug("🔍 ScheduleDbAdapter.search() - Received filter: {}", filter);
        Specification<CenterWeeklyScheduleEntity> spec = buildSpecification(filter);
        log.debug("✅ Built specification: {}", spec != null ? spec.getClass().getSimpleName() : "null");
        Page<CenterWeeklyScheduleEntity> result = repository.findAll(spec, pageable);
        log.debug("📊 Query returned {} results out of {} total", result.getNumberOfElements(), result.getTotalElements());
        return result.map(mapper::toDomain);
    }

    @Override
    public boolean existsByOrganizationBranchIdAndDayOfWeekAndIsDeletedFalse(UUID branchId, Integer dayOfWeek) {
        if (branchId == null || dayOfWeek == null) return false;
        return repository.existsByOrganizationBranchIdAndDayOfWeekAndIsDeletedFalse(branchId, dayOfWeek);
    }

    @Override
    public List<Schedule> findByOrganizationBranchIdAndDayOfWeekAndIsDeletedFalse(UUID branchId, Integer dayOfWeek) {
        if (branchId == null || dayOfWeek == null) return java.util.Collections.emptyList();
        
        Specification<CenterWeeklyScheduleEntity> spec = (root, query, cb) -> cb.and(
            cb.equal(root.get("organizationBranchId"), branchId),
            cb.equal(root.get("dayOfWeek"), dayOfWeek),
            cb.equal(root.get("isDeleted"), false)
        );
        
        return repository.findAll(spec).stream()
            .map(mapper::toDomain)
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public boolean hasOverlappingSchedule(UUID branchId, Integer dayOfWeek, 
                                          java.time.LocalTime startTime, java.time.LocalTime endTime, 
                                          UUID excludeScheduleId) {
        if (branchId == null || dayOfWeek == null || startTime == null || endTime == null) {
            return false;
        }
        
        // Get all existing schedules for this branch and day
        List<Schedule> existing = findByOrganizationBranchIdAndDayOfWeekAndIsDeletedFalse(branchId, dayOfWeek);
        
        // Check for time overlap (excluding the current schedule if editing)
        return existing.stream()
            .filter(s -> excludeScheduleId == null || !s.getScheduleId().equals(excludeScheduleId))
            .anyMatch(s -> {
                java.time.LocalTime existingStart = s.getStartTime();
                java.time.LocalTime existingEnd = s.getEndTime();
                
                // Check if time ranges overlap
                // Overlap occurs if: startTime < existingEnd AND endTime > existingStart
                return startTime.isBefore(existingEnd) && endTime.isAfter(existingStart);
            });
    }

    private Specification<CenterWeeklyScheduleEntity> buildSpecification(FilterRequest filter) {
        if (filter == null) {
            log.debug("🔹 buildSpecification() - Filter is null, returning conjunction");
            return (root, q, cb) -> cb.conjunction();
        }

        // Check if there are any filters (criteria, groups, or scopes)
        boolean hasCriteria = filter.getCriteria() != null && !filter.getCriteria().isEmpty();
        boolean hasGroups = filter.getGroups() != null && !filter.getGroups().isEmpty();
        boolean hasScopes = filter.getScopes() != null && !filter.getScopes().isEmpty();

        log.debug("🔹 buildSpecification() - hasCriteria: {}, hasGroups: {}, hasScopes: {}",
                  hasCriteria, hasGroups, hasScopes);

        if (hasCriteria) {
            filter.getCriteria().forEach(c ->
                log.debug("  ├─ Criteria: key='{}', operator='{}', value={}, dataType={}",
                          c.getKey(), c.getOperation(), c.getValue(), c.getDataType()));
        }

        // If no filters at all, return empty spec
        if (!hasCriteria && !hasGroups && !hasScopes) {
            log.debug("🔹 buildSpecification() - No filters found, returning conjunction");
            return (root, q, cb) -> cb.conjunction();
        }

        // Always build specification with all filters (scopes are applied first)
        log.debug("✅ buildSpecification() - Building GenericSpecificationBuilder with criteria");
        return new GenericSpecificationBuilder<CenterWeeklyScheduleEntity>(ScheduleFilterConfig.ALLOWED_FIELDS)
                .withCriteria(filter.getCriteria())
                .withGroups(filter.getGroups())
                .withScopes(filter.getScopes())
                .build();
    }
}

