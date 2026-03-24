package com.example.mydentist2.mapper;

import com.example.mydentist2.dto.payment.PaymentRequest;
import com.example.mydentist2.dto.payment.PaymentResponse;
import com.example.mydentist2.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "appointment.id", target = "appointmentId")
    PaymentResponse toResponse(Payment payment);

    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paidAt", ignore = true)
    Payment toEntity(PaymentRequest request);

    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paidAt", ignore = true)
    void updateEntity(PaymentRequest request, @MappingTarget Payment payment);
}
