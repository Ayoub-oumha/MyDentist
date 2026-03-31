package com.example.mydentist2.dto.appointment;

import com.example.mydentist2.model.DentalServiceType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequest {

    @NotNull
    private Long patientId;

    @NotNull
    private Long dentistId;

    @NotNull
    @Future
    private LocalDateTime startTime;

    @NotNull
    private DentalServiceType serviceType;

    private String notes;
}
