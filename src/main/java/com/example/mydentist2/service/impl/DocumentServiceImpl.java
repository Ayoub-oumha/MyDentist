package com.example.mydentist2.service.impl;

import com.example.mydentist2.dto.document.DocumentRequest;
import com.example.mydentist2.dto.document.DocumentResponse;
import com.example.mydentist2.exception.FileStorageException;
import com.example.mydentist2.exception.InvalidFileException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentServiceImpl implements DocumentService {

    private final MedicalDocumentRepository documentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final DocumentMapper documentMapper;

    @Value("${app.upload.dir}")
    private String uploadDir;

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
    public DocumentResponse uploadFile(MultipartFile file, Long patientId, Long uploadedById, MedicalDocument.DocumentType type) {
        if (file.isEmpty()) throw new InvalidFileException("File is empty");

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".pdf") && !originalFilename.endsWith(".jpg")
                && !originalFilename.endsWith(".jpeg") && !originalFilename.endsWith(".png"))) {
            throw new InvalidFileException("Unsupported file type. Allowed: pdf, jpg, jpeg, png");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));
        User uploadedBy = userRepository.findById(uploadedById)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + uploadedById));

        try {
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath);

            MedicalDocument document = new MedicalDocument();
            document.setPatient(patient);
            document.setUploadedBy(uploadedBy);
            document.setFileName(file.getOriginalFilename());
            document.setS3Key(filePath.toString());
            document.setType(type);
            document.setUploadedAt(LocalDateTime.now());

            return documentMapper.toResponse(documentRepository.save(document));
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public Resource downloadFile(Long id) {
        MedicalDocument document = findById(id);
        try {
            Path filePath = Paths.get(document.getS3Key());
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable())
                throw new FileStorageException("File not found or not readable: " + document.getFileName());
            return resource;
        } catch (MalformedURLException e) {
            throw new FileStorageException("Could not resolve file path for: " + document.getFileName(), e);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        MedicalDocument document = findById(id);
        Path filePath = Paths.get(document.getS3Key());
        try { Files.deleteIfExists(filePath); } catch (IOException ignored) {}
        documentRepository.delete(document);
    }

    private MedicalDocument findById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
    }
}
