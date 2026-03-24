package com.example.mydentist2.service;

import com.example.mydentist2.dto.appointment.AppointmentRequest;
import com.example.mydentist2.dto.appointment.AppointmentResponse;
import com.example.mydentist2.model.Appointment;

import java.util.List;

public interface AppointmentService {

    AppointmentResponse create(AppointmentRequest request);

    AppointmentResponse getById(Long id);

    List<AppointmentResponse> getByPatient(Long patientId);

    List<AppointmentResponse> getByDentist(Long dentistId);

    AppointmentResponse updateStatus(Long id, Appointment.Status status);

    AppointmentResponse update(Long id, AppointmentRequest request);

    void cancel(Long id);
}
