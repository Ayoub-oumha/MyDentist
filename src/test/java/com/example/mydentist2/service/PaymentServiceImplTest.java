package com.example.mydentist2.service;

import com.example.mydentist2.dto.payment.PaymentRequest;
import com.example.mydentist2.dto.payment.PaymentResponse;
import com.example.mydentist2.exception.BusinessException;
import com.example.mydentist2.exception.ResourceNotFoundException;
import com.example.mydentist2.mapper.PaymentMapper;
import com.example.mydentist2.model.Appointment;
import com.example.mydentist2.model.Payment;
import com.example.mydentist2.repository.AppointmentRepository;
import com.example.mydentist2.repository.PaymentRepository;
import com.example.mydentist2.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PaymentMapper paymentMapper;

    @InjectMocks private PaymentServiceImpl paymentService;

    private Appointment appointment;
    private Payment payment;
    private PaymentRequest request;
    private PaymentResponse response;

    @BeforeEach
    void setUp() {
        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(Appointment.Status.CONFIRMED);

        payment = new Payment();
        payment.setId(1L);
        payment.setAppointment(appointment);
        payment.setAmount(350.0);
        payment.setStatus(Payment.Status.COMPLETED);
        payment.setStripePaymentId("pi_stripe123");
        payment.setPaidAt(LocalDateTime.now());

        request = new PaymentRequest();
        request.setAppointmentId(1L);
        request.setAmount(350.0);
        request.setStripePaymentId("pi_stripe123");

        response = new PaymentResponse();
        response.setId(1L);
        response.setAppointmentId(1L);
        response.setAmount(350.0);
        response.setStatus(Payment.Status.COMPLETED);
    }

    @Test
    void create_success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(paymentRepository.findByAppointmentId(1L)).thenReturn(Optional.empty());
        when(paymentMapper.toEntity(request)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        PaymentResponse result = paymentService.create(request);

        assertThat(result.getAmount()).isEqualTo(350.0);
        assertThat(result.getStatus()).isEqualTo(Payment.Status.COMPLETED);
        verify(paymentRepository).save(payment);
    }

    @Test
    void create_throwsResourceNotFoundException_whenAppointmentNotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Appointment not found");

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void create_throwsBusinessException_whenPaymentAlreadyExists() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(paymentRepository.findByAppointmentId(1L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Payment already exists");

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void getById_success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        PaymentResponse result = paymentService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payment not found with id: 99");
    }

    @Test
    void getByAppointment_success() {
        when(paymentRepository.findByAppointmentId(1L)).thenReturn(Optional.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        PaymentResponse result = paymentService.getByAppointment(1L);

        assertThat(result.getAppointmentId()).isEqualTo(1L);
    }

    @Test
    void getByAppointment_throwsResourceNotFoundException_whenNotFound() {
        when(paymentRepository.findByAppointmentId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getByAppointment(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payment not found for appointment id: 99");
    }

    @Test
    void getAll_returnsPaymentList() {
        when(paymentRepository.findAll()).thenReturn(List.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        List<PaymentResponse> result = paymentService.getAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void refund_success() {
        PaymentResponse refundedResponse = new PaymentResponse();
        refundedResponse.setId(1L);
        refundedResponse.setStatus(Payment.Status.REFUNDED);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(refundedResponse);

        PaymentResponse result = paymentService.refund(1L);

        assertThat(result.getStatus()).isEqualTo(Payment.Status.REFUNDED);
        verify(paymentRepository).save(payment);
    }

    @Test
    void refund_throwsBusinessException_whenPaymentNotCompleted() {
        payment.setStatus(Payment.Status.REFUNDED);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.refund(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Only completed payments can be refunded");
    }
}
