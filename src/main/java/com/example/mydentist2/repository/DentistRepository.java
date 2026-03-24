package com.example.mydentist2.repository;

import com.example.mydentist2.model.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DentistRepository extends JpaRepository<Dentist, Long> {

    Optional<Dentist> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Dentist> findBySpecialty(String specialty);
}
