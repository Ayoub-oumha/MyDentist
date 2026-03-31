package com.example.mydentist2.service;

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
import com.example.mydentist2.service.impl.DocumentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

    @Mock private MedicalDocumentRepository documentRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private UserRepository userRepository;
    @Mock private DocumentMapper documentMapper;

    @InjectMocks private DocumentServiceImpl documentService;

    private Patient patient;
    private User uploader;
    private MedicalDocument document;
    private DocumentRequest request;
    private DocumentResponse response;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setName("Alice Martin");
        patient.setRole(User.Role.PATIENT);

        uploader = new Patient();
        uploader.setId(2L);
        uploader.setName("Dr. Karim");
        uploader.setRole(User.Role.DENTIST);

        document = new MedicalDocument();
        document.setId(1L);
        document.setPatient(patient);
        document.setUploadedBy(uploader);
        document.setFileName("radio.jpg");
        document.setS3Key("documents/patient-1/radio.jpg");
        document.setType(MedicalDocument.DocumentType.XRAY);

        request = new DocumentRequest();
        request.setPatientId(1L);
        request.setUploadedById(2L);
        request.setFileName("radio.jpg");
        request.setS3Key("documents/patient-1/radio.jpg");
        request.setType(MedicalDocument.DocumentType.XRAY);

        response = new DocumentResponse();
        response.setId(1L);
        response.setPatientId(1L);
        response.setFileName("radio.jpg");
        response.setType(MedicalDocument.DocumentType.XRAY);
    }

    @Test
    void create_success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(uploader));
        when(documentMapper.toEntity(request)).thenReturn(document);
        when(documentRepository.save(document)).thenReturn(document);
        when(documentMapper.toResponse(document)).thenReturn(response);

        DocumentResponse result = documentService.create(request);

        assertThat(result.getFileName()).isEqualTo("radio.jpg");
        assertThat(result.getType()).isEqualTo(MedicalDocument.DocumentType.XRAY);
        verify(documentRepository).save(document);
    }

    @Test
    void create_throwsResourceNotFoundException_whenPatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient not found");

        verify(documentRepository, never()).save(any());
    }

    @Test
    void create_throwsResourceNotFoundException_whenUploaderNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(documentRepository, never()).save(any());
    }

    @Test
    void getById_success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        when(documentMapper.toResponse(document)).thenReturn(response);

        DocumentResponse result = documentService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Document not found with id: 99");
    }

    @Test
    void getByPatient_returnsDocumentList() {
        when(documentRepository.findByPatientId(1L)).thenReturn(List.of(document));
        when(documentMapper.toResponse(document)).thenReturn(response);

        List<DocumentResponse> result = documentService.getByPatient(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFileName()).isEqualTo("radio.jpg");
    }

    @Test
    void getByPatientAndType_returnsFilteredList() {
        when(documentRepository.findByPatientIdAndType(1L, MedicalDocument.DocumentType.XRAY))
                .thenReturn(List.of(document));
        when(documentMapper.toResponse(document)).thenReturn(response);

        List<DocumentResponse> result = documentService.getByPatientAndType(1L, MedicalDocument.DocumentType.XRAY);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(MedicalDocument.DocumentType.XRAY);
    }

    @Test
    void delete_success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        documentService.delete(1L);

        verify(documentRepository).delete(document);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
