package com.example.mydentist2.service;

import com.example.mydentist2.dto.appointment.*;
import com.example.mydentist2.model.Appointment;
import com.example.mydentist2.model.DentalServiceType;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    AppointmentResponse create(AppointmentRequest request);

    AppointmentResponse getById(Long id);

    List<AppointmentResponse> getByPatient(Long patientId);

    List<AppointmentResponse> getByDentist(Long dentistId);

    AppointmentResponse update(Long id, AppointmentRequest request);

    AppointmentResponse updateStatus(Long id, Appointment.Status status);

    void cancel(Long id);

    void delete(Long id);

    List<TimeSlotResponse> getAvailableSlots(Long dentistId, LocalDate date, DentalServiceType serviceType);

    List<AppointmentResponse> getDailySchedule(Long dentistId, LocalDate date);

    List<AppointmentResponse> getWeeklySchedule(Long dentistId, LocalDate weekStart);

    List<DentalServiceResponse> getAllServices();
}
