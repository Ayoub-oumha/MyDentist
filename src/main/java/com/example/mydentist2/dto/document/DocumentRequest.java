package com.example.mydentist2.dto.document;

import com.example.mydentist2.model.MedicalDocument;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentRequest {

    @NotNull
    private Long patientId;

    @NotNull
    private Long uploadedById;

    @NotBlank
    private String fileName;

    @NotBlank
    private String s3Key;

    private MedicalDocument.DocumentType type;
}
