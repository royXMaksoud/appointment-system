package com.care.appointment.application.service;

import com.care.appointment.domain.enums.RequestStatus;
import com.care.appointment.infrastructure.db.entities.AppointmentRequestEntity;
import com.care.appointment.infrastructure.db.repositories.AppointmentRequestRepository;
import com.care.appointment.web.dto.AppointmentRequestDTO;
import com.care.appointment.web.dto.AppointmentSearchCriteriaDTO;
import com.care.appointment.web.dto.AppointmentSuggestionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing appointment requests from mobile app
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentRequestService {
    
    private final AppointmentRequestRepository requestRepository;
    private final AppointmentSearchService searchService;
    
    /**
     * Create a new appointment request and return suggestions
     */
    @Transactional
    public AppointmentRequestDTO createRequest(AppointmentRequestDTO dto) {
        log.info("Creating appointment request for beneficiary={}, serviceType={}", 
            dto.getBeneficiaryId(), dto.getServiceTypeId());
        
        AppointmentRequestEntity entity = AppointmentRequestEntity.builder()
            .beneficiaryId(dto.getBeneficiaryId())
            .serviceTypeId(dto.getServiceTypeId())
            .preferredDate(dto.getPreferredDate() != null ? dto.getPreferredDate() : LocalDate.now().plusDays(1))
            .priority(dto.getPriority() != null ? dto.getPriority() : "NORMAL")
            .preferenceType(dto.getPreferenceType() != null ? dto.getPreferenceType() : "NEAREST_CENTER")
            .locationLatitude(dto.getLocationLatitude())
            .locationLongitude(dto.getLocationLongitude())
            .mobileNumber(dto.getMobileNumber())
            .status(RequestStatus.PENDING.name())
            .build();
        
        AppointmentRequestEntity saved = requestRepository.save(entity);
        log.info("Appointment request created: id={}", saved.getAppointmentRequestId());
        
        return mapToDTO(saved);
    }
    
    /**
     * Get suggestions for an appointment request
     */
    @Transactional(readOnly = true)
    public List<AppointmentSuggestionDTO> getSuggestionsForRequest(UUID requestId) {
        AppointmentRequestEntity request = requestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));
        
        // Build search criteria from request
        AppointmentSearchCriteriaDTO criteria = AppointmentSearchCriteriaDTO.builder()
            .serviceTypeId(request.getServiceTypeId())
            .latitude(request.getLocationLatitude())
            .longitude(request.getLocationLongitude())
            .preferredDate(request.getPreferredDate())
            .preferenceType(request.getPreferenceType())
            .priority(request.getPriority())
            .radiusKm(50)
            .maxResults(5)
            .build();
        
        return searchService.searchAvailableAppointments(criteria);
    }
    
    /**
     * Get requests for a beneficiary
     */
    @Transactional(readOnly = true)
    public List<AppointmentRequestDTO> getRequestsByBeneficiary(UUID beneficiaryId) {
        return requestRepository.findByBeneficiaryIdOrderByCreatedAtDesc(beneficiaryId)
            .stream()
            .map(this::mapToDTO)
            .toList();
    }
    
    /**
     * Cancel a request
     */
    @Transactional
    public void cancelRequest(UUID requestId) {
        AppointmentRequestEntity entity = requestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));
        
        entity.setStatus(RequestStatus.CANCELLED.name());
        requestRepository.save(entity);
        log.info("Request cancelled: id={}", requestId);
    }
    
    /**
     * Approve request (after booking appointment)
     */
    @Transactional
    public void approveRequest(UUID requestId) {
        AppointmentRequestEntity entity = requestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));
        
        entity.setStatus(RequestStatus.APPROVED.name());
        requestRepository.save(entity);
        log.info("Request approved: id={}", requestId);
    }
    
    private AppointmentRequestDTO mapToDTO(AppointmentRequestEntity entity) {
        return AppointmentRequestDTO.builder()
            .appointmentRequestId(entity.getAppointmentRequestId())
            .beneficiaryId(entity.getBeneficiaryId())
            .serviceTypeId(entity.getServiceTypeId())
            .preferredDate(entity.getPreferredDate())
            .priority(entity.getPriority())
            .preferenceType(entity.getPreferenceType())
            .locationLatitude(entity.getLocationLatitude())
            .locationLongitude(entity.getLocationLongitude())
            .mobileNumber(entity.getMobileNumber())
            .status(entity.getStatus())
            .rejectionReason(entity.getRejectionReason())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}

