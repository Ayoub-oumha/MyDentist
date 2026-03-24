package com.example.mydentist2.mapper;

import com.example.mydentist2.dto.dentist.DentistRequest;
import com.example.mydentist2.dto.dentist.DentistResponse;
import com.example.mydentist2.model.Dentist;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DentistMapper {

    DentistResponse toResponse(Dentist dentist);

    Dentist toEntity(DentistRequest request);

    void updateEntity(DentistRequest request, @MappingTarget Dentist dentist);
}
