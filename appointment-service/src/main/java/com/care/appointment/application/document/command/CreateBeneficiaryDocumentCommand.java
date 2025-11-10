package com.care.appointment.application.document.command;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBeneficiaryDocumentCommand {
    private UUID beneficiaryId;
    private String documentName;
    private String documentType;
    private String documentDescription;
    private String fileUrl;
    private String fileName;
    private Long fileSizeBytes;
    private String mimeType;
    private String storageProvider;
    private String storageKey;
    private UUID createdById;
}

