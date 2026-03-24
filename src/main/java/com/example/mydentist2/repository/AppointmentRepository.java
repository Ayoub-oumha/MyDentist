package com.example.mydentist2.repository;

import com.example.mydentist2.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDentistId(Long dentistId);

    List<Appointment> findByStatus(Appointment.Status status);

    List<Appointment> findByDentistIdAndDateTimeBetween(Long dentistId, LocalDateTime from, LocalDateTime to);

    boolean existsByDentistIdAndDateTime(Long dentistId, LocalDateTime dateTime);
}
