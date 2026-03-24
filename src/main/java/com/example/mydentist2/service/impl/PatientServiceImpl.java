package com.example.mydentist2.service.impl;

import com.example.mydentist2.dto.patient.PatientRequest;
import com.example.mydentist2.dto.patient.PatientResponse;
import com.example.mydentist2.exception.ResourceNotFoundException;
import com.example.mydentist2.mapper.PatientMapper;
import com.example.mydentist2.model.Patient;
import com.example.mydentist2.repository.PatientRepository;
import com.example.mydentist2.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    public PatientResponse getById(Long id) {
        return patientMapper.toResponse(findById(id));
    }

    @Override
    public List<PatientResponse> getAll() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PatientResponse update(Long id, PatientRequest request) {
        Patient patient = findById(id);
        patientMapper.updateEntity(request, patient);
        return patientMapper.toResponse(patientRepository.save(patient));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        patientRepository.delete(findById(id));
    }

    private Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }
}
