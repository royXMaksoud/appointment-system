package com.care.appointment.web.controller;

import com.care.appointment.application.family.command.CreateFamilyMemberCommand;
import com.care.appointment.application.family.command.UpdateFamilyMemberCommand;
import com.care.appointment.application.family.service.FamilyMemberService;
import com.care.appointment.domain.model.FamilyMember;
import com.care.appointment.web.dto.FamilyMemberDTO;
import com.care.appointment.web.mapper.FamilyMemberWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Family Member management
 * 
 * Provides CRUD operations for managing family members linked to beneficiaries.
 */
@RestController
@RequestMapping("/api/family-members")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Family Members", description = "Family member management APIs")
public class FamilyMemberController {

    private final FamilyMemberService familyMemberService;
    private final FamilyMemberWebMapper familyMemberWebMapper;

    @PostMapping
    @Operation(summary = "Create a new family member")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Family member created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error or duplicate national ID")
    })
    public ResponseEntity<FamilyMemberDTO> create(@Valid @RequestBody FamilyMemberDTO request) {
        log.info("Creating family member for beneficiary: {}", request.getBeneficiaryId());
        
        CreateFamilyMemberCommand command = familyMemberWebMapper.toCreateCommand(request);
        FamilyMember created = familyMemberService.create(command);
        FamilyMemberDTO response = familyMemberWebMapper.toDTO(created);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing family member")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Family member updated successfully"),
        @ApiResponse(responseCode = "404", description = "Family member not found")
    })
    public ResponseEntity<FamilyMemberDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody FamilyMemberDTO request) {
        log.info("Updating family member: {}", id);
        
        UpdateFamilyMemberCommand command = familyMemberWebMapper.toUpdateCommand(id, request);
        FamilyMember updated = familyMemberService.update(id, command);
        FamilyMemberDTO response = familyMemberWebMapper.toDTO(updated);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get family member by ID")
    public ResponseEntity<FamilyMemberDTO> getById(@PathVariable UUID id) {
        FamilyMember familyMember = familyMemberService.getById(id);
        FamilyMemberDTO response = familyMemberWebMapper.toDTO(familyMember);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/beneficiary/{beneficiaryId}")
    @Operation(summary = "Get all family members for a beneficiary")
    public ResponseEntity<List<FamilyMemberDTO>> getByBeneficiaryId(@PathVariable UUID beneficiaryId) {
        log.info("Getting family members for beneficiary: {}", beneficiaryId);
        
        List<FamilyMember> familyMembers = familyMemberService.getByBeneficiaryId(beneficiaryId);
        List<FamilyMemberDTO> response = familyMembers.stream()
                .map(familyMemberWebMapper::toDTO)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/beneficiary/{beneficiaryId}/emergency-contacts")
    @Operation(summary = "Get emergency contacts for a beneficiary")
    public ResponseEntity<List<FamilyMemberDTO>> getEmergencyContacts(@PathVariable UUID beneficiaryId) {
        log.info("Getting emergency contacts for beneficiary: {}", beneficiaryId);
        
        List<FamilyMember> contacts = familyMemberService.getEmergencyContacts(beneficiaryId);
        List<FamilyMemberDTO> response = contacts.stream()
                .map(familyMemberWebMapper::toDTO)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete (soft delete) a family member")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Family member deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Family member not found")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("Deleting family member: {}", id);
        familyMemberService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/beneficiary/{beneficiaryId}/count")
    @Operation(summary = "Get count of family members for a beneficiary")
    public ResponseEntity<Long> getCount(@PathVariable UUID beneficiaryId) {
        long count = familyMemberService.countByBeneficiaryId(beneficiaryId);
        return ResponseEntity.ok(count);
    }
}

