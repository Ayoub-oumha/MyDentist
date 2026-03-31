package com.example.mydentist2.service.impl;

import com.example.mydentist2.dto.appointment.*;
import com.example.mydentist2.exception.AppointmentConflictException;
import com.example.mydentist2.exception.InvalidOperationException;
import com.example.mydentist2.exception.ResourceNotFoundException;
import com.example.mydentist2.mapper.AppointmentMapper;
import com.example.mydentist2.model.Appointment;
import com.example.mydentist2.model.Dentist;
import com.example.mydentist2.model.DentalServiceType;
import com.example.mydentist2.model.Patient;
import com.example.mydentist2.repository.AppointmentRepository;
import com.example.mydentist2.repository.DentistRepository;
import com.example.mydentist2.repository.PatientRepository;
import com.example.mydentist2.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentServiceImpl implements AppointmentService {

    private static final LocalTime CLINIC_OPEN  = LocalTime.of(9, 0);
    private static final LocalTime CLINIC_CLOSE = LocalTime.of(18, 0);
    private static final DayOfWeek CLINIC_CLOSED_DAY = DayOfWeek.SUNDAY;

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository     patientRepository;
    private final DentistRepository     dentistRepository;
    private final AppointmentMapper     appointmentMapper;

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public AppointmentResponse create(AppointmentRequest request) {
        LocalDateTime start = request.getStartTime();
        int duration        = request.getServiceType().getDurationMinutes();
        LocalDateTime end   = start.plusMinutes(duration);

        validateWorkingHours(start, end);

        if (!appointmentRepository.findOverlapping(request.getDentistId(), start, end).isEmpty()) {
            throw new AppointmentConflictException(
                "Dentist already has an appointment overlapping " + start + " – " + end);
        }

        Patient patient = findPatient(request.getPatientId());
        Dentist dentist = findDentist(request.getDentistId());

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDentist(dentist);
        appointment.setStartTime(start);
        appointment.setEndTime(end);
        appointment.setServiceType(request.getServiceType());
        appointment.setDurationMinutes(duration);
        appointment.setStatus(Appointment.Status.PENDING);
        appointment.setNotes(request.getNotes());

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    @Override
    public AppointmentResponse getById(Long id) {
        return appointmentMapper.toResponse(findById(id));
    }

    @Override
    public List<AppointmentResponse> getByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream().map(appointmentMapper::toResponse).toList();
    }

    @Override
    public List<AppointmentResponse> getByDentist(Long dentistId) {
        return appointmentRepository.findByDentistId(dentistId)
                .stream().map(appointmentMapper::toResponse).toList();
    }

    @Override
    public List<AppointmentResponse> getDailySchedule(Long dentistId, LocalDate date) {
        LocalDateTime start = date.atTime(CLINIC_OPEN);
        LocalDateTime end   = date.atTime(CLINIC_CLOSE);
        return appointmentRepository.findDailySchedule(dentistId, start, end)
                .stream().map(appointmentMapper::toResponse).toList();
    }

    @Override
    public List<AppointmentResponse> getWeeklySchedule(Long dentistId, LocalDate weekStart) {
        LocalDateTime start = weekStart.atTime(CLINIC_OPEN);
        LocalDateTime end   = weekStart.plusDays(6).atTime(CLINIC_CLOSE);
        return appointmentRepository.findWeeklySchedule(dentistId, start, end)
                .stream().map(appointmentMapper::toResponse).toList();
    }

    // ── AVAILABLE SLOTS ───────────────────────────────────────────────────────

    @Override
    public List<TimeSlotResponse> getAvailableSlots(Long dentistId, LocalDate date,
                                                     DentalServiceType serviceType) {
        if (date.getDayOfWeek() == CLINIC_CLOSED_DAY) {
            return List.of();
        }

        int duration = serviceType.getDurationMinutes();
        LocalDateTime dayStart = date.atTime(CLINIC_OPEN);
        LocalDateTime dayEnd   = date.atTime(CLINIC_CLOSE);

        List<Appointment> booked = appointmentRepository
                .findDailySchedule(dentistId, dayStart, dayEnd);

        List<TimeSlotResponse> slots = new ArrayList<>();
        LocalDateTime cursor = dayStart;

        while (!cursor.plusMinutes(duration).isAfter(dayEnd)) {
            final LocalDateTime slotStart = cursor;
            final LocalDateTime slotEnd   = cursor.plusMinutes(duration);
            boolean overlaps = booked.stream().anyMatch(a ->
                    a.getStartTime().isBefore(slotEnd) && a.getEndTime().isAfter(slotStart));

            if (!overlaps) {
                slots.add(new TimeSlotResponse(slotStart, slotEnd, duration));
            }
            cursor = cursor.plusMinutes(15);
        }
        return slots;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public AppointmentResponse update(Long id, AppointmentRequest request) {
        Appointment appointment = findById(id);

        if (appointment.getStatus() == Appointment.Status.COMPLETED ||
            appointment.getStatus() == Appointment.Status.CANCELLED) {
            throw new InvalidOperationException("Cannot edit a " + appointment.getStatus() + " appointment");
        }

        LocalDateTime start = request.getStartTime();
        int duration        = request.getServiceType().getDurationMinutes();
        LocalDateTime end   = start.plusMinutes(duration);

        validateWorkingHours(start, end);

        if (!appointmentRepository.findOverlappingExcluding(
                request.getDentistId(), start, end, id).isEmpty()) {
            throw new AppointmentConflictException(
                "Updated time overlaps an existing appointment: " + start + " – " + end);
        }

        appointment.setStartTime(start);
        appointment.setEndTime(end);
        appointment.setServiceType(request.getServiceType());
        appointment.setDurationMinutes(duration);
        appointment.setNotes(request.getNotes());

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse updateStatus(Long id, Appointment.Status status) {
        Appointment appointment = findById(id);
        appointment.setStatus(status);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    // ── CANCEL / DELETE ───────────────────────────────────────────────────────

    @Override
    @Transactional
    public void cancel(Long id) {
        Appointment appointment = findById(id);
        if (appointment.getStatus() == Appointment.Status.COMPLETED) {
            throw new InvalidOperationException("Cannot cancel a completed appointment");
        }
        appointment.setStatus(Appointment.Status.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Appointment not found with id: " + id);
        }
        appointmentRepository.deleteById(id);
    }

    // ── SERVICES CATALOG ──────────────────────────────────────────────────────

    @Override
    public List<DentalServiceResponse> getAllServices() {
        return Arrays.stream(DentalServiceType.values())
                .map(s -> new DentalServiceResponse(s, s.getLabel(), s.getDurationMinutes()))
                .toList();
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private void validateWorkingHours(LocalDateTime start, LocalDateTime end) {
        if (start.getDayOfWeek() == CLINIC_CLOSED_DAY) {
            throw new InvalidOperationException("Clinic is closed on Sundays");
        }
        if (start.toLocalTime().isBefore(CLINIC_OPEN) ||
            end.toLocalTime().isAfter(CLINIC_CLOSE)) {
            throw new InvalidOperationException(
                "Appointment must be within working hours 09:00–18:00. Requested: "
                + start.toLocalTime() + " – " + end.toLocalTime());
        }
    }

    private Appointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }

    private Patient findPatient(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    private Dentist findDentist(Long id) {
        return dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with id: " + id));
    }
}
