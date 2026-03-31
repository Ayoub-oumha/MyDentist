package com.example.mydentist2.controller;

import com.example.mydentist2.dto.document.DocumentRequest;
import com.example.mydentist2.dto.document.DocumentResponse;
import com.example.mydentist2.model.MedicalDocument;
import com.example.mydentist2.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("patientId") Long patientId,
            @RequestParam("uploadedById") Long uploadedById,
            @RequestParam(value = "type", required = false) MedicalDocument.DocumentType type) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentService.uploadFile(file, patientId, uploadedById, type));
    }

    @PostMapping
    public ResponseEntity<DocumentResponse> create(@Valid @RequestBody DocumentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.create(request));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Resource resource = documentService.downloadFile(id);
        String filename = resource.getFilename();
        String contentType = "application/octet-stream";
        if (filename != null) {
            if (filename.endsWith(".pdf")) contentType = "application/pdf";
            else if (filename.endsWith(".png")) contentType = "image/png";
            else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) contentType = "image/jpeg";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getById(id));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<DocumentResponse>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(documentService.getByPatient(patientId));
    }

    @GetMapping("/patient/{patientId}/type/{type}")
    public ResponseEntity<List<DocumentResponse>> getByPatientAndType(@PathVariable Long patientId,
                                                                        @PathVariable MedicalDocument.DocumentType type) {
        return ResponseEntity.ok(documentService.getByPatientAndType(patientId, type));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
