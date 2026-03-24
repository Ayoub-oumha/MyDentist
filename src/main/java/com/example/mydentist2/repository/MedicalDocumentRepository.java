package com.example.mydentist2.repository;

import com.example.mydentist2.model.MedicalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalDocumentRepository extends JpaRepository<MedicalDocument, Long> {

    List<MedicalDocument> findByPatientId(Long patientId);

    List<MedicalDocument> findByUploadedById(Long uploadedById);

    List<MedicalDocument> findByPatientIdAndType(Long patientId, MedicalDocument.DocumentType type);
}
