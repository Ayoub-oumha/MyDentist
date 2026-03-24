package com.example.mydentist2.controller;

import com.example.mydentist2.dto.dentist.DentistRequest;
import com.example.mydentist2.dto.dentist.DentistResponse;
import com.example.mydentist2.service.DentistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dentists")
@RequiredArgsConstructor
public class DentistController {

    private final DentistService dentistService;

    @GetMapping
    public ResponseEntity<List<DentistResponse>> getAll() {
        return ResponseEntity.ok(dentistService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DentistResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(dentistService.getById(id));
    }

    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<DentistResponse>> getBySpecialty(@PathVariable String specialty) {
        return ResponseEntity.ok(dentistService.getBySpecialty(specialty));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DentistResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody DentistRequest request) {
        return ResponseEntity.ok(dentistService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dentistService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
