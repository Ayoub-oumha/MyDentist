package com.example.mydentist2.dto.document;

import com.example.mydentist2.model.MedicalDocument;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentResponse {

    private Long id;
    private Long patientId;
    private String patientName;
    private Long uploadedById;
    private String uploadedByName;
    private String fileName;
    private String s3Key;
    private MedicalDocument.DocumentType type;
    private LocalDateTime uploadedAt;
}
