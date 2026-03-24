package com.example.mydentist2.mapper;

import com.example.mydentist2.dto.document.DocumentRequest;
import com.example.mydentist2.dto.document.DocumentResponse;
import com.example.mydentist2.model.MedicalDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "patient.name", target = "patientName")
    @Mapping(source = "uploadedBy.id", target = "uploadedById")
    @Mapping(source = "uploadedBy.name", target = "uploadedByName")
    DocumentResponse toResponse(MedicalDocument document);

    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "uploadedBy", ignore = true)
    @Mapping(target = "uploadedAt", ignore = true)
    MedicalDocument toEntity(DocumentRequest request);

    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "uploadedBy", ignore = true)
    @Mapping(target = "uploadedAt", ignore = true)
    void updateEntity(DocumentRequest request, @MappingTarget MedicalDocument document);
}
