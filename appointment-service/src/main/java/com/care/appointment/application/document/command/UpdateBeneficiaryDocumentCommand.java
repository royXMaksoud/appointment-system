package com.care.appointment.application.document.command;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBeneficiaryDocumentCommand {
    private UUID documentId;
    private String documentName;
    private String documentType;
    private String documentDescription;
    private String fileName;
    private UUID updatedById;
}

