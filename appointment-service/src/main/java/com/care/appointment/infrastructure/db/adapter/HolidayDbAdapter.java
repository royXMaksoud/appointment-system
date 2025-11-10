package com.care.appointment.infrastructure.db.adapter;

import com.care.appointment.domain.model.Holiday;
import com.care.appointment.domain.ports.out.holiday.HolidayCrudPort;
import com.care.appointment.domain.ports.out.holiday.HolidaySearchPort;
import com.care.appointment.infrastructure.db.config.HolidayFilterConfig;
import com.care.appointment.infrastructure.db.entities.CenterHolidayEntity;
import com.care.appointment.infrastructure.db.mapper.HolidayJpaMapper;
import com.care.appointment.infrastructure.db.repositories.CenterHolidayRepository;
import com.sharedlib.core.filter.FilterRequest;
import com.sharedlib.core.filter.GenericSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HolidayDbAdapter implements HolidayCrudPort, HolidaySearchPort {

    private final CenterHolidayRepository repository;
    private final HolidayJpaMapper mapper;

    @Override
    public Holiday save(Holiday domain) {
        CenterHolidayEntity entity = mapper.toEntity(domain);
        CenterHolidayEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Holiday update(Holiday domain) {
        CenterHolidayEntity entity = mapper.toEntity(domain);
        CenterHolidayEntity updated = repository.save(entity);
        return mapper.toDomain(updated);
    }

    @Override
    public Optional<Holiday> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Page<Holiday> search(FilterRequest filter, Pageable pageable) {
        Specification<CenterHolidayEntity> spec = buildSpecification(filter);
        return repository.findAll(spec, pageable).map(mapper::toDomain);
    }

    @Override
    public boolean existsByOrganizationBranchIdAndHolidayDateAndIsDeletedFalse(UUID branchId, LocalDate holidayDate) {
        if (branchId == null || holidayDate == null) return false;
        return repository.existsByOrganizationBranchIdAndHolidayDateAndIsDeletedFalse(branchId, holidayDate);
    }

    @Override
    public List<Holiday> findActiveHolidaysByOrganizationBranchId(UUID branchId) {
        if (branchId == null) return java.util.Collections.emptyList();
        
        Specification<CenterHolidayEntity> spec = (root, query, cb) -> cb.and(
            cb.equal(root.get("organizationBranchId"), branchId),
            cb.equal(root.get("isActive"), true),
            cb.equal(root.get("isDeleted"), false)
        );
        
        return repository.findAll(spec).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean hasActiveHolidayOnDayOfWeek(UUID branchId, Integer dayOfWeek) {
        if (branchId == null || dayOfWeek == null) return false;
        
        // Get all active holidays for this branch
        List<Holiday> activeHolidays = findActiveHolidaysByOrganizationBranchId(branchId);
        
        if (activeHolidays.isEmpty()) return false;
        
        // Check if any holiday falls on the same day of week
        // dayOfWeek: 0=Sunday, 1=Monday, ..., 6=Saturday
        // Java DayOfWeek: 1=Monday, ..., 7=Sunday
        DayOfWeek targetDayOfWeek = convertToDayOfWeek(dayOfWeek);
        
        return activeHolidays.stream().anyMatch(holiday -> {
            LocalDate holidayDate = holiday.getHolidayDate();
            if (holidayDate == null) return false;
            
            DayOfWeek holidayDayOfWeek = holidayDate.getDayOfWeek();
            
            // For recurring yearly holidays, check if the day of week matches
            // For non-recurring holidays, check if the day of week matches
            if (holiday.getIsRecurringYearly() != null && holiday.getIsRecurringYearly()) {
                // Recurring yearly: check if the day of week matches
                return holidayDayOfWeek.equals(targetDayOfWeek);
            } else {
                // Non-recurring: check if the day of week matches
                return holidayDayOfWeek.equals(targetDayOfWeek);
            }
        });
    }
    
    /**
     * Convert schedule dayOfWeek (0=Sunday, 1=Monday, ..., 6=Saturday)
     * to Java DayOfWeek (1=Monday, ..., 7=Sunday)
     */
    private DayOfWeek convertToDayOfWeek(Integer dayOfWeek) {
        if (dayOfWeek == null) return null;
        // Schedule: 0=Sunday, 1=Monday, 2=Tuesday, 3=Wednesday, 4=Thursday, 5=Friday, 6=Saturday
        // Java DayOfWeek: 1=Monday, 2=Tuesday, 3=Wednesday, 4=Thursday, 5=Friday, 6=Saturday, 7=Sunday
        return switch (dayOfWeek) {
            case 0 -> DayOfWeek.SUNDAY;    // 0 -> 7
            case 1 -> DayOfWeek.MONDAY;    // 1 -> 1
            case 2 -> DayOfWeek.TUESDAY;    // 2 -> 2
            case 3 -> DayOfWeek.WEDNESDAY; // 3 -> 3
            case 4 -> DayOfWeek.THURSDAY;  // 4 -> 4
            case 5 -> DayOfWeek.FRIDAY;    // 5 -> 5
            case 6 -> DayOfWeek.SATURDAY;  // 6 -> 6
            default -> null;
        };
    }

    private Specification<CenterHolidayEntity> buildSpecification(FilterRequest filter) {
        if (filter == null) {
            return (root, q, cb) -> cb.conjunction();
        }

        // Check if there are any filters (criteria, groups, or scopes)
        boolean hasCriteria = filter.getCriteria() != null && !filter.getCriteria().isEmpty();
        boolean hasGroups = filter.getGroups() != null && !filter.getGroups().isEmpty();
        boolean hasScopes = filter.getScopes() != null && !filter.getScopes().isEmpty();

        // If no filters at all, return empty spec
        if (!hasCriteria && !hasGroups && !hasScopes) {
            return (root, q, cb) -> cb.conjunction();
        }

        // Always build specification with all filters (scopes are applied first)
        return new GenericSpecificationBuilder<CenterHolidayEntity>(HolidayFilterConfig.ALLOWED_FIELDS)
                .withCriteria(filter.getCriteria())
                .withGroups(filter.getGroups())
                .withScopes(filter.getScopes())
                .build();
    }
}

