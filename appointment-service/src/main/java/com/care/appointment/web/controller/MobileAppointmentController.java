package com.care.appointment.web.controller;

import com.care.appointment.application.service.*;
import com.care.appointment.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for mobile app appointment APIs
 */
@RestController
@RequestMapping("/api/mobile/appointments")
@RequiredArgsConstructor
@Tag(name = "Mobile Appointments", description = "APIs for mobile application")
public class MobileAppointmentController {
    
    private final BeneficiaryService beneficiaryService;
    private final AppointmentSearchService searchService;
    private final AppointmentRequestService requestService;
    private final AppointmentManagementService managementService;
    
    @PostMapping("/beneficiaries/register")
    @Operation(summary = "Register a new beneficiary")
    public ResponseEntity<BeneficiaryDTO> registerBeneficiary(@Valid @RequestBody BeneficiaryDTO dto) {
        BeneficiaryDTO registered = beneficiaryService.registerBeneficiary(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registered);
    }
    
    @GetMapping("/beneficiaries/mobile/{mobileNumber}")
    @Operation(summary = "Get beneficiary by mobile number")
    public ResponseEntity<BeneficiaryDTO> getBeneficiaryByMobile(@PathVariable String mobileNumber) {
        return beneficiaryService.getBeneficiaryByMobile(mobileNumber)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/search")
    @Operation(summary = "Search for available appointment slots")
    public ResponseEntity<List<AppointmentSuggestionDTO>> searchAppointments(
        @Valid @RequestBody AppointmentSearchCriteriaDTO criteria
    ) {
        List<AppointmentSuggestionDTO> suggestions = searchService.searchAvailableAppointments(criteria);
        return ResponseEntity.ok(suggestions);
    }
    
    @PostMapping("/requests")
    @Operation(summary = "Create an appointment request")
    public ResponseEntity<AppointmentRequestDTO> createRequest(@Valid @RequestBody AppointmentRequestDTO dto) {
        AppointmentRequestDTO created = requestService.createRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping("/requests/{requestId}/suggestions")
    @Operation(summary = "Get suggestions for an appointment request")
    public ResponseEntity<List<AppointmentSuggestionDTO>> getSuggestions(@PathVariable UUID requestId) {
        List<AppointmentSuggestionDTO> suggestions = requestService.getSuggestionsForRequest(requestId);
        return ResponseEntity.ok(suggestions);
    }
    
    @PostMapping("/book")
    @Operation(summary = "Book an appointment")
    public ResponseEntity<AppointmentDTO> bookAppointment(@Valid @RequestBody AppointmentDTO dto) {
        AppointmentDTO booked = managementService.bookAppointment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(booked);
    }
    
    @GetMapping("/beneficiaries/{beneficiaryId}/appointments")
    @Operation(summary = "Get all appointments for a beneficiary")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByBeneficiary(@PathVariable UUID beneficiaryId) {
        List<AppointmentDTO> appointments = managementService.getAppointmentsByBeneficiary(beneficiaryId);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/beneficiaries/{beneficiaryId}/upcoming")
    @Operation(summary = "Get upcoming appointments for a beneficiary")
    public ResponseEntity<List<AppointmentDTO>> getUpcomingAppointments(@PathVariable UUID beneficiaryId) {
        List<AppointmentDTO> appointments = managementService.getUpcomingAppointments(beneficiaryId);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/{appointmentId}")
    @Operation(summary = "Get appointment details")
    public ResponseEntity<AppointmentDTO> getAppointment(@PathVariable UUID appointmentId) {
        AppointmentDTO appointment = managementService.getAppointmentById(appointmentId);
        return ResponseEntity.ok(appointment);
    }
    
    @PostMapping("/{appointmentId}/cancel")
    @Operation(summary = "Cancel an appointment")
    public ResponseEntity<Void> cancelAppointment(
        @PathVariable UUID appointmentId,
        @RequestParam String reason
    ) {
        managementService.cancelAppointment(appointmentId, reason, null);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/beneficiaries/{beneficiaryId}/requests")
    @Operation(summary = "Get appointment requests for a beneficiary")
    public ResponseEntity<List<AppointmentRequestDTO>> getRequestsByBeneficiary(@PathVariable UUID beneficiaryId) {
        List<AppointmentRequestDTO> requests = requestService.getRequestsByBeneficiary(beneficiaryId);
        return ResponseEntity.ok(requests);
    }
}

