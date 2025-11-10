package com.care.appointment.application.service;

import com.care.appointment.infrastructure.db.entities.*;
import com.care.appointment.infrastructure.db.repositories.*;
import com.care.appointment.web.dto.AppointmentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing appointments
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentManagementService {
    
    private final AppointmentRepository appointmentRepository;
    private final AppointmentStatusRepository statusRepository;
    private final AppointmentStatusHistoryRepository historyRepository;
    private final AppointmentRequestRepository requestRepository;
    private final ServiceTypeLangRepository serviceTypeLangRepository;
    
    /**
     * Book an appointment
     */
    @Transactional
    public AppointmentDTO bookAppointment(AppointmentDTO dto) {
        log.info("Booking appointment: beneficiary={}, branch={}, date={}, time={}", 
            dto.getBeneficiaryId(), dto.getOrganizationBranchId(), 
            dto.getAppointmentDate(), dto.getAppointmentTime());
        
        // Check if slot is available
        boolean slotTaken = appointmentRepository.existsByOrganizationBranchIdAndAppointmentDateAndAppointmentTime(
            dto.getOrganizationBranchId(), dto.getAppointmentDate(), dto.getAppointmentTime()
        );
        
        if (slotTaken) {
            throw new RuntimeException("Slot is already booked");
        }
        
        // Get status IDs for exclusion (CANCELLED and COMPLETED)
        UUID cancelledStatusId = getStatusIdByCode("CANCELLED");
        UUID completedStatusId = getStatusIdByCode("COMPLETED");
        List<UUID> excludedStatusIds = List.of(cancelledStatusId, completedStatusId);
        
        // DUPLICATE CHECK #1: Same person, same service, same day
        boolean sameDayDuplicate = appointmentRepository.hasExistingAppointmentForServiceAndDate(
            dto.getBeneficiaryId(),
            dto.getServiceTypeId(),
            dto.getAppointmentDate(),
            excludedStatusIds
        );
        
        if (sameDayDuplicate) {
            throw new RuntimeException("You already have an appointment for this service on this date");
        }
        
        // DUPLICATE CHECK #2: Same person, same service, different day (but active appointment exists)
        boolean activeServiceDuplicate = appointmentRepository.hasActiveAppointmentForService(
            dto.getBeneficiaryId(),
            dto.getServiceTypeId(),
            excludedStatusIds
        );
        
        if (activeServiceDuplicate) {
            throw new RuntimeException("You already have an active appointment for this service. Please wait until it is completed or cancelled");
        }
        
        // Get default status ID (REQUESTED or CONFIRMED)
        UUID statusId = dto.getAppointmentStatusId() != null ? 
            dto.getAppointmentStatusId() : 
            getStatusIdByCode("REQUESTED");
        
        AppointmentEntity entity = AppointmentEntity.builder()
            .appointmentRequestId(dto.getAppointmentRequestId())
            .beneficiaryId(dto.getBeneficiaryId())
            .organizationBranchId(dto.getOrganizationBranchId())
            .serviceTypeId(dto.getServiceTypeId())
            .appointmentDate(dto.getAppointmentDate())
            .appointmentTime(dto.getAppointmentTime())
            .slotDurationMinutes(dto.getSlotDurationMinutes() != null ? dto.getSlotDurationMinutes() : 30)
            .appointmentStatusId(statusId)
            .priority(dto.getPriority() != null ? dto.getPriority() : "NORMAL")
            .notes(dto.getNotes())
            .build();
        
        AppointmentEntity saved = appointmentRepository.save(entity);
        
        // Create history record
        createHistoryRecord(saved.getAppointmentId(), statusId, "Appointment created", null);
        
        // If this was from a request, mark request as approved
        if (dto.getAppointmentRequestId() != null) {
            requestRepository.findById(dto.getAppointmentRequestId())
                .ifPresent(req -> {
                    req.setStatus("APPROVED");
                    requestRepository.save(req);
                });
        }
        
        log.info("Appointment booked successfully: id={}", saved.getAppointmentId());
        
        return mapToDTO(saved);
    }
    
    /**
     * Get appointments for a beneficiary
     */
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsByBeneficiary(UUID beneficiaryId) {
        return appointmentRepository
            .findByBeneficiaryIdOrderByAppointmentDateDescAppointmentTimeDesc(beneficiaryId)
            .stream()
            .map(this::mapToDTO)
            .toList();
    }
    
    /**
     * Get upcoming appointments for a beneficiary
     */
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getUpcomingAppointments(UUID beneficiaryId) {
        return appointmentRepository
            .findUpcomingAppointments(beneficiaryId, LocalDate.now())
            .stream()
            .map(this::mapToDTO)
            .toList();
    }
    
    /**
     * Cancel an appointment
     */
    @Transactional
    public void cancelAppointment(UUID appointmentId, String reason, UUID cancelledByUserId) {
        AppointmentEntity entity = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found: " + appointmentId));
        
        UUID cancelledStatusId = getStatusIdByCode("CANCELLED");
        entity.setAppointmentStatusId(cancelledStatusId);
        entity.setCancelledAt(Instant.now());
        entity.setCancellationReason(reason);
        
        appointmentRepository.save(entity);
        
        // Create history record
        createHistoryRecord(appointmentId, cancelledStatusId, reason, cancelledByUserId);
        
        log.info("Appointment cancelled: id={}", appointmentId);
    }
    
    /**
     * Mark appointment as completed
     */
    @Transactional
    public void completeAppointment(UUID appointmentId, UUID actionTypeId, String actionNotes, UUID completedByUserId) {
        AppointmentEntity entity = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found: " + appointmentId));
        
        UUID completedStatusId = getStatusIdByCode("COMPLETED");
        entity.setAppointmentStatusId(completedStatusId);
        entity.setActionTypeId(actionTypeId);
        entity.setActionNotes(actionNotes);
        entity.setCompletedAt(Instant.now());
        entity.setAttendedAt(Instant.now());
        
        appointmentRepository.save(entity);
        
        // Create history record
        createHistoryRecord(appointmentId, completedStatusId, "Appointment completed", completedByUserId);
        
        log.info("Appointment completed: id={}", appointmentId);
    }
    
    /**
     * Get appointment by ID
     */
    @Transactional(readOnly = true)
    public AppointmentDTO getAppointmentById(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId)
            .map(this::mapToDTO)
            .orElseThrow(() -> new RuntimeException("Appointment not found: " + appointmentId));
    }
    
    private void createHistoryRecord(UUID appointmentId, UUID statusId, String reason, UUID changedByUserId) {
        AppointmentStatusHistoryEntity history = AppointmentStatusHistoryEntity.builder()
            .appointmentId(appointmentId)
            .appointmentStatusId(statusId)
            .reason(reason)
            .changedByUserId(changedByUserId)
            .build();
        
        historyRepository.save(history);
    }
    
    private UUID getStatusIdByCode(String code) {
        return statusRepository.findByCode(code)
            .map(AppointmentStatusEntity::getAppointmentStatusId)
            .orElseThrow(() -> new RuntimeException("Status not found: " + code));
    }
    
    private AppointmentDTO mapToDTO(AppointmentEntity entity) {
        return AppointmentDTO.builder()
            .appointmentId(entity.getAppointmentId())
            .appointmentRequestId(entity.getAppointmentRequestId())
            .beneficiaryId(entity.getBeneficiaryId())
            .organizationBranchId(entity.getOrganizationBranchId())
            .serviceTypeId(entity.getServiceTypeId())
            .appointmentDate(entity.getAppointmentDate())
            .appointmentTime(entity.getAppointmentTime())
            .slotDurationMinutes(entity.getSlotDurationMinutes())
            .appointmentStatusId(entity.getAppointmentStatusId())
            .priority(entity.getPriority())
            .notes(entity.getNotes())
            .actionTypeId(entity.getActionTypeId())
            .actionNotes(entity.getActionNotes())
            .attendedAt(entity.getAttendedAt())
            .completedAt(entity.getCompletedAt())
            .cancelledAt(entity.getCancelledAt())
            .cancellationReason(entity.getCancellationReason())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}

