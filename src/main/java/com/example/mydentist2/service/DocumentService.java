package com.example.mydentist2.service;

import com.example.mydentist2.dto.document.DocumentRequest;
import com.example.mydentist2.dto.document.DocumentResponse;
import com.example.mydentist2.model.MedicalDocument;

import java.util.List;

public interface DocumentService {

    DocumentResponse create(DocumentRequest request);

    DocumentResponse getById(Long id);

    List<DocumentResponse> getByPatient(Long patientId);

    List<DocumentResponse> getByPatientAndType(Long patientId, MedicalDocument.DocumentType type);

    void delete(Long id);
}
