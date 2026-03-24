package com.example.mydentist2.dto.payment;

import com.example.mydentist2.model.Payment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResponse {

    private Long id;
    private Long appointmentId;
    private Double amount;
    private Payment.Status status;
    private String stripePaymentId;
    private LocalDateTime paidAt;
}
