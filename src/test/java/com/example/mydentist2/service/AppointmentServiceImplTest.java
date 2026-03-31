package com.example.mydentist2.service;

import com.example.mydentist2.dto.appointment.AppointmentRequest;
import com.example.mydentist2.dto.appointment.AppointmentResponse;
import com.example.mydentist2.exception.AppointmentConflictException;
import com.example.mydentist2.exception.InvalidOperationException;
import com.example.mydentist2.exception.ResourceNotFoundException;
import com.example.mydentist2.mapper.AppointmentMapper;
import com.example.mydentist2.model.Appointment;
import com.example.mydentist2.model.Dentist;
import com.example.mydentist2.model.DentalServiceType;
import com.example.mydentist2.model.Patient;
import com.example.mydentist2.model.User;
import com.example.mydentist2.repository.AppointmentRepository;
import com.example.mydentist2.repository.DentistRepository;
import com.example.mydentist2.repository.PatientRepository;
import com.example.mydentist2.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PatientRepository     patientRepository;
    @Mock private DentistRepository     dentistRepository;
    @Mock private AppointmentMapper     appointmentMapper;

    @InjectMocks private AppointmentServiceImpl appointmentService;

    private Patient           patient;
    private Dentist           dentist;
    private Appointment       appointment;
    private AppointmentRequest  request;
    private AppointmentResponse response;
    private LocalDateTime       startTime;
    private LocalDateTime       endTime;

    @BeforeEach
    void setUp() {
        // Monday 2025-09-15 at 10:00 — within working hours
        startTime = LocalDateTime.of(2025, 9, 15, 10, 0);
        endTime   = startTime.plusMinutes(DentalServiceType.CLEANING.getDurationMinutes()); // +45 min

        patient = new Patient();
        patient.setId(1L);
        patient.setName("Alice Martin");
        patient.setRole(User.Role.PATIENT);

        dentist = new Dentist();
        dentist.setId(2L);
        dentist.setName("Dr. Karim");
        dentist.setRole(User.Role.DENTIST);

        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(patient);
        appointment.setDentist(dentist);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setServiceType(DentalServiceType.CLEANING);
        appointment.setDurationMinutes(DentalServiceType.CLEANING.getDurationMinutes());
        appointment.setStatus(Appointment.Status.PENDING);

        request = new AppointmentRequest();
        request.setPatientId(1L);
        request.setDentistId(2L);
        request.setStartTime(startTime);
        request.setServiceType(DentalServiceType.CLEANING);
        request.setNotes("First consultation");

        response = new AppointmentResponse();
        response.setId(1L);
        response.setPatientId(1L);
        response.setDentistId(2L);
        response.setStartTime(startTime);
        response.setEndTime(endTime);
        response.setServiceType(DentalServiceType.CLEANING);
        response.setServiceLabel(DentalServiceType.CLEANING.getLabel());
        response.setDurationMinutes(DentalServiceType.CLEANING.getDurationMinutes());
        response.setStatus(Appointment.Status.PENDING);
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Test
    void create_success() {
        when(appointmentRepository.findOverlapping(eq(2L), eq(startTime), eq(endTime)))
                .thenReturn(List.of());
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(2L)).thenReturn(Optional.of(dentist));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment)).thenReturn(response);

        AppointmentResponse result = appointmentService.create(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(Appointment.Status.PENDING);
        assertThat(result.getServiceType()).isEqualTo(DentalServiceType.CLEANING);
        assertThat(result.getDurationMinutes()).isEqualTo(45);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void create_throwsConflict_whenOverlapping() {
        when(appointmentRepository.findOverlapping(eq(2L), eq(startTime), eq(endTime)))
                .thenReturn(List.of(appointment));

        assertThatThrownBy(() -> appointmentService.create(request))
                .isInstanceOf(AppointmentConflictException.class)
                .hasMessageContaining("overlapping");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void create_throwsInvalidOperation_whenOutsideWorkingHours() {
        request.setStartTime(LocalDateTime.of(2025, 9, 15, 19, 0)); // after 18:00

        assertThatThrownBy(() -> appointmentService.create(request))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("working hours");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void create_throwsInvalidOperation_whenSunday() {
        // 2025-09-14 is a Sunday
        request.setStartTime(LocalDateTime.of(2025, 9, 14, 10, 0));

        assertThatThrownBy(() -> appointmentService.create(request))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Sunday");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void create_throwsResourceNotFoundException_whenPatientNotFound() {
        when(appointmentRepository.findOverlapping(eq(2L), eq(startTime), eq(endTime)))
                .thenReturn(List.of());
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient not found");
    }

    @Test
    void create_throwsResourceNotFoundException_whenDentistNotFound() {
        when(appointmentRepository.findOverlapping(eq(2L), eq(startTime), eq(endTime)))
                .thenReturn(List.of());
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Dentist not found");
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    @Test
    void getById_success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toResponse(appointment)).thenReturn(response);

        AppointmentResponse result = appointmentService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Appointment not found with id: 99");
    }

    @Test
    void getByPatient_returnsAppointmentList() {
        when(appointmentRepository.findByPatientId(1L)).thenReturn(List.of(appointment));
        when(appointmentMapper.toResponse(appointment)).thenReturn(response);

        List<AppointmentResponse> result = appointmentService.getByPatient(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getByDentist_returnsAppointmentList() {
        when(appointmentRepository.findByDentistId(2L)).thenReturn(List.of(appointment));
        when(appointmentMapper.toResponse(appointment)).thenReturn(response);

        List<AppointmentResponse> result = appointmentService.getByDentist(2L);

        assertThat(result).hasSize(1);
    }

    // ── UPDATE STATUS ─────────────────────────────────────────────────────────

    @Test
    void updateStatus_success() {
        AppointmentResponse confirmedResponse = new AppointmentResponse();
        confirmedResponse.setId(1L);
        confirmedResponse.setStatus(Appointment.Status.CONFIRMED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment)).thenReturn(confirmedResponse);

        AppointmentResponse result = appointmentService.updateStatus(1L, Appointment.Status.CONFIRMED);

        assertThat(result.getStatus()).isEqualTo(Appointment.Status.CONFIRMED);
        verify(appointmentRepository).save(appointment);
    }

    // ── CANCEL ────────────────────────────────────────────────────────────────

    @Test
    void cancel_success() {
        appointment.setStatus(Appointment.Status.CONFIRMED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        appointmentService.cancel(1L);

        assertThat(appointment.getStatus()).isEqualTo(Appointment.Status.CANCELLED);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void cancel_throwsInvalidOperation_whenCompleted() {
        appointment.setStatus(Appointment.Status.COMPLETED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.cancel(1L))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Cannot cancel a completed appointment");
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    void delete_success() {
        when(appointmentRepository.existsById(1L)).thenReturn(true);

        appointmentService.delete(1L);

        verify(appointmentRepository).deleteById(1L);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(appointmentRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> appointmentService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Appointment not found with id: 99");
    }

    // ── AVAILABLE SLOTS ───────────────────────────────────────────────────────

    @Test
    void getAvailableSlots_returnsSlotsForWorkingDay() {
        when(appointmentRepository.findDailySchedule(any(), any(), any()))
                .thenReturn(List.of());

        var slots = appointmentService.getAvailableSlots(
                2L, startTime.toLocalDate(), DentalServiceType.CONSULTATION);

        // 09:00–18:00 with 30-min service on 15-min grid = many slots
        assertThat(slots).isNotEmpty();
        assertThat(slots.get(0).getStartTime().toLocalTime().toString()).isEqualTo("09:00");
    }

    @Test
    void getAvailableSlots_returnsEmpty_whenSunday() {
        // 2025-09-14 is Sunday
        var slots = appointmentService.getAvailableSlots(
                2L, java.time.LocalDate.of(2025, 9, 14), DentalServiceType.CONSULTATION);

        assertThat(slots).isEmpty();
    }

    @Test
    void getAvailableSlots_excludesBookedSlots() {
        // Book 09:00–09:45 (CLEANING)
        Appointment booked = new Appointment();
        booked.setStartTime(LocalDateTime.of(2025, 9, 15, 9, 0));
        booked.setEndTime(LocalDateTime.of(2025, 9, 15, 9, 45));
        booked.setStatus(Appointment.Status.CONFIRMED);

        when(appointmentRepository.findDailySchedule(any(), any(), any()))
                .thenReturn(List.of(booked));

        var slots = appointmentService.getAvailableSlots(
                2L, java.time.LocalDate.of(2025, 9, 15), DentalServiceType.CONSULTATION);

        // 09:00 slot (30 min → ends 09:30) overlaps booked 09:00–09:45, so must not appear
        boolean hasNineAM = slots.stream()
                .anyMatch(s -> s.getStartTime().getHour() == 9 && s.getStartTime().getMinute() == 0);
        assertThat(hasNineAM).isFalse();
    }
}
