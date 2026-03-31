package com.example.mydentist2.dto.appointment;

import com.example.mydentist2.model.DentalServiceType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DentalServiceResponse {
    private DentalServiceType type;
    private String label;
    private int durationMinutes;
}
