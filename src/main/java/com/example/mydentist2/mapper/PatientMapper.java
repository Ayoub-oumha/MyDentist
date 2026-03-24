package com.example.mydentist2.mapper;

import com.example.mydentist2.dto.patient.PatientRequest;
import com.example.mydentist2.dto.patient.PatientResponse;
import com.example.mydentist2.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientResponse toResponse(Patient patient);

    Patient toEntity(PatientRequest request);

    void updateEntity(PatientRequest request, @MappingTarget Patient patient);
}
