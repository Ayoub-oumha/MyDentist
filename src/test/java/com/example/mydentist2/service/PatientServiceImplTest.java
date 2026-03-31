package com.example.mydentist2.service;

import com.example.mydentist2.dto.patient.PatientRequest;
import com.example.mydentist2.dto.patient.PatientResponse;
import com.example.mydentist2.exception.ResourceNotFoundException;
import com.example.mydentist2.mapper.PatientMapper;
import com.example.mydentist2.model.Patient;
import com.example.mydentist2.model.User;
import com.example.mydentist2.repository.PatientRepository;
import com.example.mydentist2.service.impl.PatientServiceImpl;
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
class PatientServiceImplTest {

    @Mock private PatientRepository patientRepository;
    @Mock private PatientMapper patientMapper;

    @InjectMocks private PatientServiceImpl patientService;

    private Patient patient;
    private PatientResponse patientResponse;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setName("Alice Martin");
        patient.setEmail("alice@example.com");
        patient.setPhone("0612345678");
        patient.setRole(User.Role.PATIENT);

        patientResponse = new PatientResponse();
        patientResponse.setId(1L);
        patientResponse.setName("Alice Martin");
        patientResponse.setEmail("alice@example.com");
        patientResponse.setPhone("0612345678");
    }

    @Test
    void getById_success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponse(patient)).thenReturn(patientResponse);

        PatientResponse result = patientService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Alice Martin");
    }

    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient not found with id: 99");
    }

    @Test
    void getAll_returnsListOfPatients() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));
        when(patientMapper.toResponse(patient)).thenReturn(patientResponse);

        List<PatientResponse> result = patientService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void update_success() {
        PatientRequest request = new PatientRequest();
        request.setName("Alice Updated");
        request.setEmail("alice@example.com");
        request.setPhone("0699999999");

        PatientResponse updatedResponse = new PatientResponse();
        updatedResponse.setId(1L);
        updatedResponse.setName("Alice Updated");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponse(patient)).thenReturn(updatedResponse);

        PatientResponse result = patientService.update(1L, request);

        assertThat(result.getName()).isEqualTo("Alice Updated");
        verify(patientMapper).updateEntity(request, patient);
        verify(patientRepository).save(patient);
    }

    @Test
    void update_throwsResourceNotFoundException_whenNotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.update(99L, new PatientRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        patientService.delete(1L);

        verify(patientRepository).delete(patient);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
