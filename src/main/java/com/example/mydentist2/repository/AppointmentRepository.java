package com.example.mydentist2.repository;

import com.example.mydentist2.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDentistId(Long dentistId);

    List<Appointment> findByStatus(Appointment.Status status);

    // Daily view: all appointments for a dentist on a given day
    @Query("SELECT a FROM Appointment a WHERE a.dentist.id = :dentistId " +
           "AND a.startTime >= :dayStart AND a.startTime < :dayEnd " +
           "AND a.status <> 'CANCELLED' ORDER BY a.startTime")
    List<Appointment> findDailySchedule(@Param("dentistId") Long dentistId,
                                        @Param("dayStart") LocalDateTime dayStart,
                                        @Param("dayEnd") LocalDateTime dayEnd);

    // Weekly view
    @Query("SELECT a FROM Appointment a WHERE a.dentist.id = :dentistId " +
           "AND a.startTime >= :weekStart AND a.startTime < :weekEnd " +
           "AND a.status <> 'CANCELLED' ORDER BY a.startTime")
    List<Appointment> findWeeklySchedule(@Param("dentistId") Long dentistId,
                                         @Param("weekStart") LocalDateTime weekStart,
                                         @Param("weekEnd") LocalDateTime weekEnd);

    // Overlap detection: any active appointment that overlaps [newStart, newEnd)
    @Query("SELECT a FROM Appointment a WHERE a.dentist.id = :dentistId " +
           "AND a.status <> 'CANCELLED' " +
           "AND a.startTime < :endTime AND a.endTime > :startTime")
    List<Appointment> findOverlapping(@Param("dentistId") Long dentistId,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);

    // Same but exclude a specific appointment (for updates)
    @Query("SELECT a FROM Appointment a WHERE a.dentist.id = :dentistId " +
           "AND a.id <> :excludeId " +
           "AND a.status <> 'CANCELLED' " +
           "AND a.startTime < :endTime AND a.endTime > :startTime")
    List<Appointment> findOverlappingExcluding(@Param("dentistId") Long dentistId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               @Param("excludeId") Long excludeId);
}
