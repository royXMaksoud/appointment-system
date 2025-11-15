package com.care.appointment.application.appointment.service;

import com.care.appointment.application.appointment.query.NearestServiceCenterQuery;
import com.care.appointment.domain.model.NearestServiceCenterOption;
import com.care.appointment.domain.ports.in.appointment.SuggestAppointmentUseCase;
import com.care.appointment.infrastructure.client.AccessManagementClient;
import com.care.appointment.infrastructure.db.entities.CenterWeeklyScheduleEntity;
import com.care.appointment.infrastructure.db.entities.ServiceTypeEntity;
import com.care.appointment.infrastructure.db.repositories.AppointmentRepository;
import com.care.appointment.infrastructure.db.repositories.BeneficiaryRepository;
import com.care.appointment.infrastructure.db.repositories.CenterServiceRepository;
import com.care.appointment.infrastructure.db.repositories.CenterWeeklyScheduleRepository;
import com.care.appointment.infrastructure.db.repositories.ServiceTypeRepository;
import com.care.appointment.web.dto.OrganizationBranchDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AppointmentSuggestionService implements SuggestAppointmentUseCase {

    private static final int DEFAULT_LIMIT = 5;
    private static final int DEFAULT_SEARCH_WINDOW_DAYS = 30;

    private final BeneficiaryRepository beneficiaryRepository;
    private final CenterServiceRepository centerServiceRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final CenterWeeklyScheduleRepository centerWeeklyScheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final AccessManagementClient accessManagementClient;

    @Override
    public List<NearestServiceCenterOption> findNearestByLocation(NearestServiceCenterQuery query) {
        List<NearestServiceCenterOption> options = computeSuggestions(query);
        return options.stream()
                .sorted(Comparator
                        .comparing(NearestServiceCenterOption::getDistanceKm, Comparator.nullsLast(Double::compareTo))
                        .thenComparing(NearestServiceCenterOption::getNextAvailableDate, Comparator.nullsLast(LocalDate::compareTo))
                        .thenComparing(NearestServiceCenterOption::getNextAvailableTime, Comparator.nullsLast(LocalTime::compareTo)))
                .limit(resolveLimit(query))
                .collect(Collectors.toList());
    }

    @Override
    public List<NearestServiceCenterOption> findNearestByAvailability(NearestServiceCenterQuery query) {
        List<NearestServiceCenterOption> options = computeSuggestions(query);
        return options.stream()
                .filter(option -> option.getNextAvailableDate() != null)
                .sorted(Comparator
                        .comparing(NearestServiceCenterOption::getNextAvailableDate, Comparator.nullsLast(LocalDate::compareTo))
                        .thenComparing(NearestServiceCenterOption::getNextAvailableTime, Comparator.nullsLast(LocalTime::compareTo))
                        .thenComparing(NearestServiceCenterOption::getDistanceKm, Comparator.nullsLast(Double::compareTo)))
                .limit(resolveLimit(query))
                .collect(Collectors.toList());
    }

    private List<NearestServiceCenterOption> computeSuggestions(NearestServiceCenterQuery query) {
        Objects.requireNonNull(query, "query is required");
        UUID beneficiaryId = Objects.requireNonNull(query.getBeneficiaryId(), "beneficiaryId is required");
        UUID serviceTypeId = Objects.requireNonNull(query.getServiceTypeId(), "serviceTypeId is required");

        var beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new IllegalArgumentException("Beneficiary not found: " + beneficiaryId));

        Double latitude = query.getLatitudeOverride() != null
                ? query.getLatitudeOverride()
                : beneficiary.getLatitude();
        Double longitude = query.getLongitudeOverride() != null
                ? query.getLongitudeOverride()
                : beneficiary.getLongitude();

        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Beneficiary does not have latitude/longitude coordinates");
        }

        ServiceTypeEntity selectedServiceType = serviceTypeRepository.findById(serviceTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Service type not found: " + serviceTypeId));

        List<ServiceTypeEntity> allServiceTypes = serviceTypeRepository.findAllActive();
        Map<UUID, List<ServiceTypeEntity>> tree = new HashMap<>();
        for (ServiceTypeEntity entity : allServiceTypes) {
            if (entity == null) continue;
            UUID parentId = entity.getParentServiceTypeId();
            tree.computeIfAbsent(parentId, key -> new ArrayList<>()).add(entity);
        }

        Set<UUID> relevantServiceTypeIds = collectServiceTypeIds(serviceTypeId, tree);
        if (relevantServiceTypeIds.isEmpty()) {
            relevantServiceTypeIds = Set.of(serviceTypeId);
        }

        Set<UUID> branchIds = relevantServiceTypeIds.stream()
                .flatMap(id -> centerServiceRepository.findBranchIdsByServiceTypeId(id).stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (branchIds.isEmpty()) {
            log.info("Nearest lookup: no active center_services for serviceType={} (ids considered={})",
                    serviceTypeId, relevantServiceTypeIds);
            return Collections.emptyList();
        }

        log.debug("Nearest lookup: active branches for serviceType {} -> {}", serviceTypeId, branchIds);

        List<OrganizationBranchDTO> branches = fetchBranchesByIds(new ArrayList<>(branchIds));
        if (branches == null || branches.isEmpty()) {
            log.warn("Nearest lookup: unable to resolve branch details for ids {}", branchIds);
            return Collections.emptyList();
        }

        Map<UUID, OrganizationBranchDTO> branchMap = new HashMap<>();
        for (OrganizationBranchDTO dto : branches) {
            if (dto == null) {
                continue;
            }
            UUID branchId = dto.getOrganizationBranchId();
            if (branchId == null) {
                continue;
            }
            branchMap.putIfAbsent(branchId, dto);
        }

        Map<UUID, String> organizationNames = resolveOrganizationNames(branches);

        int searchWindow = resolveSearchWindowDays(query);

        List<NearestServiceCenterOption> suggestions = new ArrayList<>();

        for (UUID branchId : branchIds) {
            OrganizationBranchDTO branch = branchMap.get(branchId);
            if (branch == null) {
                continue;
            }
            Double branchLat = branch.getLatitude();
            Double branchLng = branch.getLongitude();
            if (branchLat == null || branchLng == null) {
                continue;
            }

            double distance = calculateDistance(latitude, longitude, branchLat, branchLng);

            Optional<AvailabilityResult> availability = findNextAvailability(branchId, searchWindow);
            if (availability.isEmpty()) {
                continue;
            }

            AvailabilityResult slot = availability.get();

            NearestServiceCenterOption option = NearestServiceCenterOption.builder()
                    .organizationBranchId(branchId)
                    .organizationId(branch.getOrganizationId())
                    .organizationName(organizationNames.get(branch.getOrganizationId()))
                    .branchName(branch.getName())
                    .branchCode(branch.getCode())
                    .address(branch.getAddress())
                    .branchLatitude(branchLat)
                    .branchLongitude(branchLng)
                    .beneficiaryLatitude(latitude)
                    .beneficiaryLongitude(longitude)
                    .distanceKm(distance)
                    .serviceTypeId(selectedServiceType.getServiceTypeId())
                    .serviceTypeName(selectedServiceType.getName())
                    .nextAvailableDate(slot.nextDate)
                    .nextAvailableTime(slot.startTime)
                    .maxCapacityPerSlot(slot.maxCapacityPerSlot)
                    .slotsPerDay(slot.slotsPerDay)
                    .dailyCapacity(slot.dailyCapacity)
                    .bookedCount(slot.bookedCount)
                    .remainingCapacity(slot.remainingCapacity)
                    .build();

            suggestions.add(option);
        }

        return suggestions;
    }

    private List<OrganizationBranchDTO> fetchBranchesByIds(List<UUID> branchIds) {
        if (branchIds == null || branchIds.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            List<OrganizationBranchDTO> response = accessManagementClient.getBranchesByIds(branchIds);
            if (response != null && !response.isEmpty()) {
                return response;
            }
        } catch (Exception ex) {
            log.warn("Direct branch lookup failed, attempting per-branch fallback: {}", ex.getMessage());
        }

        Map<UUID, OrganizationBranchDTO> collected = new LinkedHashMap<>();
        for (UUID branchId : branchIds) {
            if (branchId == null || collected.containsKey(branchId)) {
                continue;
            }
            try {
                OrganizationBranchDTO dto = accessManagementClient.getOrganizationBranch(branchId);
                if (dto != null && dto.getOrganizationBranchId() != null) {
                    collected.put(dto.getOrganizationBranchId(), dto);
                }
            } catch (Exception ex) {
                log.warn("Fallback lookup failed for branch {}: {}", branchId, ex.getMessage());
            }
        }

        return new ArrayList<>(collected.values());
    }

    private Map<UUID, String> resolveOrganizationNames(List<OrganizationBranchDTO> branches) {
        Map<UUID, String> result = new HashMap<>();
        for (OrganizationBranchDTO branch : branches) {
            if (branch == null) {
                continue;
            }
            UUID orgId = branch.getOrganizationId();
            if (orgId == null) {
                continue;
            }
            String label = branch.getName();
            if (label == null || label.isBlank()) {
                label = branch.getCode();
            }
            if (label == null || label.isBlank()) {
                label = orgId.toString();
            }
            result.putIfAbsent(orgId, label);
        }
        return result;
    }

    private Set<UUID> collectServiceTypeIds(UUID rootId, Map<UUID, List<ServiceTypeEntity>> tree) {
        Set<UUID> result = new LinkedHashSet<>();
        Deque<UUID> stack = new ArrayDeque<>();
        stack.push(rootId);

        while (!stack.isEmpty()) {
            UUID current = stack.pop();
            if (current == null || result.contains(current)) {
                continue;
            }
            result.add(current);
            List<ServiceTypeEntity> children = tree.getOrDefault(current, Collections.emptyList());
            for (ServiceTypeEntity child : children) {
                stack.push(child.getServiceTypeId());
            }
        }

        return result;
    }

    private Optional<AvailabilityResult> findNextAvailability(UUID branchId, int searchWindowDays) {
        List<CenterWeeklyScheduleEntity> schedules = centerWeeklyScheduleRepository
                .findByOrganizationBranchIdAndIsActiveTrue(branchId);
        if (schedules.isEmpty()) {
            return Optional.empty();
        }

        Map<Integer, CenterWeeklyScheduleEntity> scheduleByDay = schedules.stream()
                .filter(schedule -> schedule.getDayOfWeek() != null)
                .collect(Collectors.toMap(
                        CenterWeeklyScheduleEntity::getDayOfWeek,
                        schedule -> schedule,
                        (existing, replacement) -> existing));

        LocalDate today = LocalDate.now();
        for (int i = 0; i < searchWindowDays; i++) {
            LocalDate date = today.plusDays(i);
            int dayOfWeek = toScheduleDayOfWeek(date);
            CenterWeeklyScheduleEntity schedule = scheduleByDay.get(dayOfWeek);
            if (schedule == null) {
                continue;
            }

            AvailabilityResult result = evaluateDayAvailability(branchId, schedule, date);
            if (result.remainingCapacity > 0) {
                return Optional.of(result);
            }
        }

        return Optional.empty();
    }

    private AvailabilityResult evaluateDayAvailability(UUID branchId,
                                                       CenterWeeklyScheduleEntity schedule,
                                                       LocalDate date) {
        int slotDuration = schedule.getSlotDurationMinutes() != null && schedule.getSlotDurationMinutes() > 0
                ? schedule.getSlotDurationMinutes()
                : 30;
        Integer maxCapacityPerSlot = schedule.getMaxCapacityPerSlot() != null && schedule.getMaxCapacityPerSlot() > 0
                ? schedule.getMaxCapacityPerSlot()
                : 1;

        LocalTime startTime = Optional.ofNullable(schedule.getStartTime()).orElse(LocalTime.of(9, 0));
        LocalTime endTime = Optional.ofNullable(schedule.getEndTime()).orElse(startTime.plusHours(8));

        long totalMinutes = Duration.between(startTime, endTime).toMinutes();
        int slots = (int) Math.max(1, totalMinutes / slotDuration);
        int dailyCapacity = slots * maxCapacityPerSlot;

        long bookedCount = appointmentRepository.countActiveByBranchAndDate(branchId, date);
        int remaining = (int) Math.max(0, dailyCapacity - bookedCount);

        return new AvailabilityResult(date, startTime, slotDuration, slots, maxCapacityPerSlot,
                dailyCapacity, bookedCount, remaining);
    }

    private int toScheduleDayOfWeek(LocalDate date) {
        int dow = date.getDayOfWeek().getValue(); // Monday=1 ... Sunday=7
        return dow == 7 ? 0 : dow;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0088; // Earth radius in KM
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return Math.round(R * c * 100.0) / 100.0;
    }

    private int resolveLimit(NearestServiceCenterQuery query) {
        return Optional.ofNullable(query.getLimit())
                .filter(limit -> limit > 0)
                .orElse(DEFAULT_LIMIT);
    }

    private int resolveSearchWindowDays(NearestServiceCenterQuery query) {
        return Optional.ofNullable(query.getSearchWindowDays())
                .filter(days -> days > 0)
                .orElse(DEFAULT_SEARCH_WINDOW_DAYS);
    }

    private record AvailabilityResult(
            LocalDate nextDate,
            LocalTime startTime,
            int slotDuration,
            int slotsPerDay,
            int maxCapacityPerSlot,
            int dailyCapacity,
            long bookedCount,
            int remainingCapacity
    ) {
    }
}


