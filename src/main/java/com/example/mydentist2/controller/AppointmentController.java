package com.example.mydentist2.controller;

import com.example.mydentist2.dto.appointment.*;
import com.example.mydentist2.model.Appointment;
import com.example.mydentist2.model.DentalServiceType;
import com.example.mydentist2.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // ── CRUD ──────────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getById(id));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponse>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getByPatient(patientId));
    }

    @GetMapping("/dentist/{dentistId}")
    public ResponseEntity<List<AppointmentResponse>> getByDentist(@PathVariable Long dentistId) {
        return ResponseEntity.ok(appointmentService.getByDentist(dentistId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> update(@PathVariable Long id,
                                                       @Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse> updateStatus(@PathVariable Long id,
                                                             @RequestParam Appointment.Status status) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, status));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        appointmentService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── CALENDAR ──────────────────────────────────────────────────────────────

    @GetMapping("/dentist/{dentistId}/daily")
    public ResponseEntity<List<AppointmentResponse>> getDailySchedule(
            @PathVariable Long dentistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getDailySchedule(dentistId, date));
    }

    @GetMapping("/dentist/{dentistId}/weekly")
    public ResponseEntity<List<AppointmentResponse>> getWeeklySchedule(
            @PathVariable Long dentistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        return ResponseEntity.ok(appointmentService.getWeeklySchedule(dentistId, weekStart));
    }

    // ── AVAILABLE SLOTS ───────────────────────────────────────────────────────

    @GetMapping("/dentist/{dentistId}/slots")
    public ResponseEntity<List<TimeSlotResponse>> getAvailableSlots(
            @PathVariable Long dentistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam DentalServiceType serviceType) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(dentistId, date, serviceType));
    }

    // ── SERVICES CATALOG ──────────────────────────────────────────────────────

    @GetMapping("/services")
    public ResponseEntity<List<DentalServiceResponse>> getAllServices() {
        return ResponseEntity.ok(appointmentService.getAllServices());
    }
}
