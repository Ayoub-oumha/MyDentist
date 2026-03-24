package com.example.mydentist2.service.impl;

import com.example.mydentist2.dto.appointment.AppointmentRequest;
import com.example.mydentist2.dto.appointment.AppointmentResponse;
import com.example.mydentist2.exception.BusinessException;
import com.example.mydentist2.exception.ResourceNotFoundException;
import com.example.mydentist2.mapper.AppointmentMapper;
import com.example.mydentist2.model.Appointment;
import com.example.mydentist2.model.Dentist;
import com.example.mydentist2.model.Patient;
import com.example.mydentist2.repository.AppointmentRepository;
import com.example.mydentist2.repository.DentistRepository;
import com.example.mydentist2.repository.PatientRepository;
import com.example.mydentist2.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    @Transactional
    public AppointmentResponse create(AppointmentRequest request) {
        if (appointmentRepository.existsByDentistIdAndDateTime(request.getDentistId(), request.getDateTime())) {
            throw new BusinessException("Dentist already has an appointment at: " + request.getDateTime());
        }

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));

        Dentist dentist = dentistRepository.findById(request.getDentistId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with id: " + request.getDentistId()));

        Appointment appointment = appointmentMapper.toEntity(request);
        appointment.setPatient(patient);
        appointment.setDentist(dentist);
        appointment.setStatus(Appointment.Status.PENDING);

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponse getById(Long id) {
        return appointmentMapper.toResponse(findById(id));
    }

    @Override
    public List<AppointmentResponse> getByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    public List<AppointmentResponse> getByDentist(Long dentistId) {
        return appointmentRepository.findByDentistId(dentistId).stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AppointmentResponse updateStatus(Long id, Appointment.Status status) {
        Appointment appointment = findById(id);
        appointment.setStatus(status);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse update(Long id, AppointmentRequest request) {
        Appointment appointment = findById(id);
        appointmentMapper.updateEntity(request, appointment);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Appointment appointment = findById(id);
        if (appointment.getStatus() == Appointment.Status.COMPLETED) {
            throw new BusinessException("Cannot cancel a completed appointment");
        }
        appointment.setStatus(Appointment.Status.CANCELLED);
        appointmentRepository.save(appointment);
    }

    private Appointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }
}
