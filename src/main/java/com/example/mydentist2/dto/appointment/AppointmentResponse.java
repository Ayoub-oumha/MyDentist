package com.example.mydentist2.dto.appointment;

import com.example.mydentist2.model.Appointment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentResponse {

    private Long id;
    private Long patientId;
    private String patientName;
    private Long dentistId;
    private String dentistName;
    private LocalDateTime dateTime;
    private Appointment.Status status;
    private String notes;
}
