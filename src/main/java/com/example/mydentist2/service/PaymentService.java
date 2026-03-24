package com.example.mydentist2.service;

import com.example.mydentist2.dto.payment.PaymentRequest;
import com.example.mydentist2.dto.payment.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse create(PaymentRequest request);

    PaymentResponse getById(Long id);

    PaymentResponse getByAppointment(Long appointmentId);

    List<PaymentResponse> getAll();

    PaymentResponse refund(Long id);
}
