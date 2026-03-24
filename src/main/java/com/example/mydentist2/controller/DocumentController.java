package com.example.mydentist2.controller;

import com.example.mydentist2.dto.document.DocumentRequest;
import com.example.mydentist2.dto.document.DocumentResponse;
import com.example.mydentist2.model.MedicalDocument;
import com.example.mydentist2.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentResponse> create(@Valid @RequestBody DocumentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.create(request));
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
