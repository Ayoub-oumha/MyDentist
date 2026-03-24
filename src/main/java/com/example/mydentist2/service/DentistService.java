package com.example.mydentist2.service;

import com.example.mydentist2.dto.dentist.DentistRequest;
import com.example.mydentist2.dto.dentist.DentistResponse;

import java.util.List;

public interface DentistService {

    DentistResponse getById(Long id);

    List<DentistResponse> getAll();

    List<DentistResponse> getBySpecialty(String specialty);

    DentistResponse update(Long id, DentistRequest request);

    void delete(Long id);
}
