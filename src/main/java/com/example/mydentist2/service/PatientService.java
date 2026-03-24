package com.example.mydentist2.service;

import com.example.mydentist2.dto.patient.PatientRequest;
import com.example.mydentist2.dto.patient.PatientResponse;

import java.util.List;

public interface PatientService {

    PatientResponse getById(Long id);

    List<PatientResponse> getAll();

    PatientResponse update(Long id, PatientRequest request);

    void delete(Long id);
}
