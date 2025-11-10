package com.care.appointment.application.beneficiary.command;

import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Command for bulk updating multiple beneficiaries
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkBeneficiaryUpdateCommand {
    
    /**
     * List of beneficiary IDs to update
     */
    private List<UUID> beneficiaryIds;
    
    /**
     * Map of field names to new values
     * Supports: preferredLanguageCodeValueId, profilePhotoUrl, registrationStatusCodeValueId, etc.
     * Example: { "preferredLanguageCodeValueId": "550e8400..." }
     */
    private Map<String, Object> updateFields;
    
    /**
     * Description of bulk update for audit trail
     * Example: "Bulk language preference update to Arabic"
     */
    private String description;
    
    /**
     * User ID who initiated the bulk update
     */
    private UUID updatedById;
}

