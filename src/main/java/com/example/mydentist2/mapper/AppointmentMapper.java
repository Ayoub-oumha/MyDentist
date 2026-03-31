package com.example.mydentist2.mapper;

import com.example.mydentist2.dto.appointment.AppointmentRequest;
import com.example.mydentist2.dto.appointment.AppointmentResponse;
import com.example.mydentist2.model.Appointment;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(source = "patient.id",   target = "patientId")
    @Mapping(source = "patient.name", target = "patientName")
    @Mapping(source = "dentist.id",   target = "dentistId")
    @Mapping(source = "dentist.name", target = "dentistName")
    @Mapping(target = "serviceLabel", ignore = true)
    AppointmentResponse toResponse(Appointment appointment);

    @AfterMapping
    default void setServiceLabel(Appointment appointment, @MappingTarget AppointmentResponse response) {
        if (appointment.getServiceType() != null) {
            response.setServiceLabel(appointment.getServiceType().getLabel());
        }
    }

    @Mapping(target = "id",              ignore = true)
    @Mapping(target = "patient",         ignore = true)
    @Mapping(target = "dentist",         ignore = true)
    @Mapping(target = "status",          ignore = true)
    @Mapping(target = "payment",         ignore = true)
    @Mapping(target = "endTime",         ignore = true)
    @Mapping(target = "durationMinutes", ignore = true)
    Appointment toEntity(AppointmentRequest request);

    @Mapping(target = "id",              ignore = true)
    @Mapping(target = "patient",         ignore = true)
    @Mapping(target = "dentist",         ignore = true)
    @Mapping(target = "status",          ignore = true)
    @Mapping(target = "payment",         ignore = true)
    @Mapping(target = "endTime",         ignore = true)
    @Mapping(target = "durationMinutes", ignore = true)
    void updateEntity(AppointmentRequest request, @MappingTarget Appointment appointment);
}
