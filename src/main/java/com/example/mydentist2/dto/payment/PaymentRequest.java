package com.example.mydentist2.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull
    private Long appointmentId;

    @NotNull
    @Positive
    private Double amount;

    @NotNull
    private String stripePaymentId;
}
