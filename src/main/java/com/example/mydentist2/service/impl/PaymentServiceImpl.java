package com.example.mydentist2.service.impl;

import com.example.mydentist2.dto.payment.PaymentRequest;
import com.example.mydentist2.dto.payment.PaymentResponse;
import com.example.mydentist2.exception.DuplicateResourceException;
import com.example.mydentist2.exception.InvalidOperationException;
import com.example.mydentist2.exception.ResourceNotFoundException;
import com.example.mydentist2.mapper.PaymentMapper;
import com.example.mydentist2.model.Appointment;
import com.example.mydentist2.model.Payment;
import com.example.mydentist2.repository.AppointmentRepository;
import com.example.mydentist2.repository.PaymentRepository;
import com.example.mydentist2.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse create(PaymentRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + request.getAppointmentId()));

        if (paymentRepository.findByAppointmentId(request.getAppointmentId()).isPresent()) {
            throw new DuplicateResourceException("Payment already exists for appointment id: " + request.getAppointmentId());
        }

        Payment payment = paymentMapper.toEntity(request);
        payment.setAppointment(appointment);
        payment.setStatus(Payment.Status.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());

        // TODO: integrate with Stripe API using request.getStripePaymentId()

        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    @Override
    public PaymentResponse getById(Long id) {
        return paymentMapper.toResponse(findById(id));
    }

    @Override
    public PaymentResponse getByAppointment(Long appointmentId) {
        return paymentRepository.findByAppointmentId(appointmentId)
                .map(paymentMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for appointment id: " + appointmentId));
    }

    @Override
    public List<PaymentResponse> getAll() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PaymentResponse refund(Long id) {
        Payment payment = findById(id);
        if (payment.getStatus() != Payment.Status.COMPLETED) {
            throw new InvalidOperationException("Only completed payments can be refunded");
        }

        // TODO: call Stripe refund API using payment.getStripePaymentId()

        payment.setStatus(Payment.Status.REFUNDED);
        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    private Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }
}
