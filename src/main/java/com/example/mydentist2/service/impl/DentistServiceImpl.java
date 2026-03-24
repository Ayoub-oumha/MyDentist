package com.example.mydentist2.service.impl;

import com.example.mydentist2.dto.dentist.DentistRequest;
import com.example.mydentist2.dto.dentist.DentistResponse;
import com.example.mydentist2.exception.ResourceNotFoundException;
import com.example.mydentist2.mapper.DentistMapper;
import com.example.mydentist2.model.Dentist;
import com.example.mydentist2.repository.DentistRepository;
import com.example.mydentist2.service.DentistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DentistServiceImpl implements DentistService {

    private final DentistRepository dentistRepository;
    private final DentistMapper dentistMapper;

    @Override
    public DentistResponse getById(Long id) {
        return dentistMapper.toResponse(findById(id));
    }

    @Override
    public List<DentistResponse> getAll() {
        return dentistRepository.findAll().stream()
                .map(dentistMapper::toResponse)
                .toList();
    }

    @Override
    public List<DentistResponse> getBySpecialty(String specialty) {
        return dentistRepository.findBySpecialty(specialty).stream()
                .map(dentistMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DentistResponse update(Long id, DentistRequest request) {
        Dentist dentist = findById(id);
        dentistMapper.updateEntity(request, dentist);
        return dentistMapper.toResponse(dentistRepository.save(dentist));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        dentistRepository.delete(findById(id));
    }

    private Dentist findById(Long id) {
        return dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with id: " + id));
    }
}
