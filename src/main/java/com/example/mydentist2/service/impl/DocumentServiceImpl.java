package com.example.mydentist2.service.impl;

import com.example.mydentist2.dto.document.DocumentRequest;
import com.example.mydentist2.dto.document.DocumentResponse;
import com.example.mydentist2.exception.ResourceNotFoundException;
import com.example.mydentist2.mapper.DocumentMapper;
import com.example.mydentist2.model.MedicalDocument;
import com.example.mydentist2.model.Patient;
import com.example.mydentist2.model.User;
import com.example.mydentist2.repository.MedicalDocumentRepository;
import com.example.mydentist2.repository.PatientRepository;
import com.example.mydentist2.repository.UserRepository;
import com.example.mydentist2.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentServiceImpl implements DocumentService {

    private final MedicalDocumentRepository documentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final DocumentMapper documentMapper;

    @Override
    @Transactional
    public DocumentResponse create(DocumentRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));

        User uploadedBy = userRepository.findById(request.getUploadedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUploadedById()));

        MedicalDocument document = documentMapper.toEntity(request);
        document.setPatient(patient);
        document.setUploadedBy(uploadedBy);
        document.setUploadedAt(LocalDateTime.now());

        // TODO: verify s3Key exists in AWS S3 bucket before saving

        return documentMapper.toResponse(documentRepository.save(document));
    }

    @Override
    public DocumentResponse getById(Long id) {
        return documentMapper.toResponse(findById(id));
    }

    @Override
    public List<DocumentResponse> getByPatient(Long patientId) {
        return documentRepository.findByPatientId(patientId).stream()
                .map(documentMapper::toResponse)
                .toList();
    }

    @Override
    public List<DocumentResponse> getByPatientAndType(Long patientId, MedicalDocument.DocumentType type) {
        return documentRepository.findByPatientIdAndType(patientId, type).stream()
                .map(documentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        MedicalDocument document = findById(id);
        // TODO: delete file from AWS S3 using document.getS3Key()
        documentRepository.delete(document);
    }

    private MedicalDocument findById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
    }
}
