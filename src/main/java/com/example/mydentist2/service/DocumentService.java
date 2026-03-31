package com.example.mydentist2.service;

import com.example.mydentist2.dto.document.DocumentRequest;
import com.example.mydentist2.dto.document.DocumentResponse;
import com.example.mydentist2.model.MedicalDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    DocumentResponse create(DocumentRequest request);

    DocumentResponse uploadFile(MultipartFile file, Long patientId, Long uploadedById, MedicalDocument.DocumentType type);

    org.springframework.core.io.Resource downloadFile(Long id);

    DocumentResponse getById(Long id);

    List<DocumentResponse> getByPatient(Long patientId);

    List<DocumentResponse> getByPatientAndType(Long patientId, MedicalDocument.DocumentType type);

    void delete(Long id);
}
