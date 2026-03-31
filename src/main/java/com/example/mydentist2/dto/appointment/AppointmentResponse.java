package com.example.mydentist2.dto.appointment;

import com.example.mydentist2.model.Appointment;
import com.example.mydentist2.model.DentalServiceType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentResponse {

    private Long id;
    private Long patientId;
    private String patientName;
    private Long dentistId;
    private String dentistName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private DentalServiceType serviceType;
    private String serviceLabel;
    private int durationMinutes;
    private Appointment.Status status;
    private String notes;
}
