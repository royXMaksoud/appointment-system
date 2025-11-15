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
    private String legacyDocumentType;
    private UUID documentTypeCodeValueId;
    private String documentTypeCode;
    private String documentDescription;
    private String fileName;
    private String fileExtension;
    private String filePath;
    private String fileUrl;
    private Long fileSizeBytes;
    private String mimeType;
    private UUID updatedById;
}

