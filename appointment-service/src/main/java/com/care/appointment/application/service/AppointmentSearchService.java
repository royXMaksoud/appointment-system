package com.care.appointment.application.service;

import com.care.appointment.infrastructure.client.AccessManagementClient;
import com.care.appointment.infrastructure.db.entities.*;
import com.care.appointment.infrastructure.db.repositories.*;
import com.care.appointment.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for smart appointment search and suggestions
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentSearchService {
    
    private final AccessManagementClient accessManagementClient;
    private final CenterServiceRepository centerServiceRepository;
    private final CenterWeeklyScheduleRepository weeklyScheduleRepository;
    private final CenterDailyCapacityRepository dailyCapacityRepository;
    private final CenterHolidayRepository holidayRepository;
    private final AppointmentRepository appointmentRepository;
    private final ServiceTypeLangRepository serviceTypeLangRepository;
    
    private static final int DEFAULT_SEARCH_DAYS = 30;
    private static final int DEFAULT_MAX_RESULTS = 5;
    
    /**
     * Search for available appointment slots based on criteria
     */
    @Transactional(readOnly = true)
    public List<AppointmentSuggestionDTO> searchAvailableAppointments(AppointmentSearchCriteriaDTO criteria) {
        log.info("Searching appointments for serviceType={}, preference={}", 
            criteria.getServiceTypeId(), criteria.getPreferenceType());
        
        // 1. Get branches that provide this service
        List<UUID> eligibleBranchIds = centerServiceRepository
            .findBranchIdsByServiceTypeId(criteria.getServiceTypeId());
        
        if (eligibleBranchIds.isEmpty()) {
            log.warn("No centers provide serviceTypeId={}", criteria.getServiceTypeId());
            return Collections.emptyList();
        }
        
        // 2. Get nearby branches from access-management-service
        List<OrganizationBranchDTO> nearbyBranches = accessManagementClient.searchNearbyBranches(
            criteria.getLatitude(),
            criteria.getLongitude(),
            criteria.getRadiusKm() != null ? criteria.getRadiusKm() : 50
        );
        
        // 3. Filter to only eligible branches
        List<OrganizationBranchDTO> availableBranches = nearbyBranches.stream()
            .filter(branch -> eligibleBranchIds.contains(branch.getOrganizationBranchId()))
            .filter(OrganizationBranchDTO::getIsActive)
            .collect(Collectors.toList());
        
        if (availableBranches.isEmpty()) {
            log.warn("No nearby active centers provide serviceTypeId={}", criteria.getServiceTypeId());
            return Collections.emptyList();
        }
        
        // 4. For each branch, find available slots
        List<AppointmentSuggestionDTO> suggestions = new ArrayList<>();
        
        LocalDate searchStartDate = criteria.getPreferredDate() != null ? 
            criteria.getPreferredDate() : LocalDate.now().plusDays(1);
        
        for (OrganizationBranchDTO branch : availableBranches) {
            List<AvailableSlot> slots = findAvailableSlots(
                branch.getOrganizationBranchId(),
                criteria.getServiceTypeId(),
                searchStartDate,
                DEFAULT_SEARCH_DAYS
            );
            
            if (!slots.isEmpty()) {
                // Get service type name
                String serviceTypeName = getServiceTypeName(criteria.getServiceTypeId(), "ar");
                
                // Calculate distance
                double distance = calculateDistance(
                    criteria.getLatitude(),
                    criteria.getLongitude(),
                    branch.getLatitude(),
                    branch.getLongitude()
                );
                
                // Add first available slot for this branch
                AvailableSlot firstSlot = slots.get(0);
                suggestions.add(AppointmentSuggestionDTO.builder()
                    .organizationBranchId(branch.getOrganizationBranchId())
                    .branchName(branch.getName())
                    .branchAddress(branch.getAddress())
                    .branchLatitude(branch.getLatitude())
                    .branchLongitude(branch.getLongitude())
                    .distanceInKm(distance)
                    .availableDate(firstSlot.getDate())
                    .availableTime(firstSlot.getTime())
                    .slotDurationMinutes(firstSlot.getDurationMinutes())
                    .serviceTypeId(criteria.getServiceTypeId())
                    .serviceTypeName(serviceTypeName)
                    .availableSlotsCount(slots.size())
                    .build());
            }
        }
        
        // 5. Sort and limit results
        Comparator<AppointmentSuggestionDTO> comparator;
        if ("NEAREST_CENTER".equals(criteria.getPreferenceType())) {
            comparator = Comparator.comparing(AppointmentSuggestionDTO::getDistanceInKm);
        } else {
            comparator = Comparator.comparing(AppointmentSuggestionDTO::getAvailableDate)
                .thenComparing(AppointmentSuggestionDTO::getAvailableTime);
        }
        
        int maxResults = criteria.getMaxResults() != null ? criteria.getMaxResults() : DEFAULT_MAX_RESULTS;
        
        return suggestions.stream()
            .sorted(comparator)
            .limit(maxResults)
            .collect(Collectors.toList());
    }
    
    /**
     * Find available slots for a specific branch
     */
    private List<AvailableSlot> findAvailableSlots(
        UUID branchId,
        UUID serviceTypeId,
        LocalDate startDate,
        int daysToSearch
    ) {
        List<AvailableSlot> availableSlots = new ArrayList<>();
        
        // Get weekly schedule for this branch
        List<CenterWeeklyScheduleEntity> weeklySchedules = weeklyScheduleRepository
            .findByOrganizationBranchIdAndIsActiveTrue(branchId);
        
        if (weeklySchedules.isEmpty()) {
            return availableSlots;
        }
        
        // Get holidays for this branch
        Set<LocalDate> holidays = holidayRepository
            .findByOrganizationBranchIdAndHolidayDateBetween(
                branchId,
                startDate,
                startDate.plusDays(daysToSearch)
            )
            .stream()
            .map(CenterHolidayEntity::getHolidayDate)
            .collect(Collectors.toSet());
        
        // Search through days
        for (int i = 0; i < daysToSearch; i++) {
            LocalDate date = startDate.plusDays(i);
            
            // Skip if holiday
            if (holidays.contains(date)) {
                continue;
            }
            
            // Get day of week (0=Sunday, 1=Monday, ..., 6=Saturday)
            int dayOfWeek = date.getDayOfWeek().getValue() % 7;
            
            // Find schedule for this day
            Optional<CenterWeeklyScheduleEntity> scheduleOpt = weeklySchedules.stream()
                .filter(s -> s.getDayOfWeek().equals(dayOfWeek))
                .findFirst();
            
            if (scheduleOpt.isEmpty()) {
                continue;  // No schedule for this day
            }
            
            CenterWeeklyScheduleEntity schedule = scheduleOpt.get();
            
            // Generate time slots for this day
            List<LocalTime> timeSlots = generateTimeSlots(
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getSlotDurationMinutes()
            );
            
            // Check each slot for availability
            for (LocalTime time : timeSlots) {
                if (isSlotAvailable(branchId, date, time)) {
                    availableSlots.add(new AvailableSlot(
                        date,
                        time,
                        schedule.getSlotDurationMinutes()
                    ));
                }
            }
        }
        
        return availableSlots;
    }
    
    /**
     * Generate time slots between start and end time
     */
    private List<LocalTime> generateTimeSlots(LocalTime startTime, LocalTime endTime, int durationMinutes) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime currentTime = startTime;
        
        while (currentTime.plusMinutes(durationMinutes).isBefore(endTime) || 
               currentTime.plusMinutes(durationMinutes).equals(endTime)) {
            slots.add(currentTime);
            currentTime = currentTime.plusMinutes(durationMinutes);
        }
        
        return slots;
    }
    
    /**
     * Check if a specific slot is available
     */
    private boolean isSlotAvailable(UUID branchId, LocalDate date, LocalTime time) {
        // Check if already booked
        return !appointmentRepository.existsByOrganizationBranchIdAndAppointmentDateAndAppointmentTime(
            branchId, date, time
        );
    }
    
    /**
     * Calculate distance between two coordinates using Haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in km
    }
    
    /**
     * Get service type name in specified language
     */
    private String getServiceTypeName(UUID serviceTypeId, String languageCode) {
        return serviceTypeLangRepository
            .findByServiceTypeIdAndLanguageCode(serviceTypeId, languageCode)
            .map(ServiceTypeLangEntity::getName)
            .orElse("Unknown Service");
    }
    
    /**
     * Inner class for available slot
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    private static class AvailableSlot {
        private LocalDate date;
        private LocalTime time;
        private Integer durationMinutes;
    }
}

