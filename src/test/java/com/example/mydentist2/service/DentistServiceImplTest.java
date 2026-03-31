package com.example.mydentist2.service;

import com.example.mydentist2.dto.dentist.DentistRequest;
import com.example.mydentist2.dto.dentist.DentistResponse;
import com.example.mydentist2.exception.ResourceNotFoundException;
import com.example.mydentist2.mapper.DentistMapper;
import com.example.mydentist2.model.Dentist;
import com.example.mydentist2.model.User;
import com.example.mydentist2.repository.DentistRepository;
import com.example.mydentist2.service.impl.DentistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DentistServiceImplTest {

    @Mock private DentistRepository dentistRepository;
    @Mock private DentistMapper dentistMapper;

    @InjectMocks private DentistServiceImpl dentistService;

    private Dentist dentist;
    private DentistResponse dentistResponse;

    @BeforeEach
    void setUp() {
        dentist = new Dentist();
        dentist.setId(1L);
        dentist.setName("Dr. Karim Benali");
        dentist.setEmail("karim@example.com");
        dentist.setSpecialty("Orthodontie");
        dentist.setRole(User.Role.DENTIST);

        dentistResponse = new DentistResponse();
        dentistResponse.setId(1L);
        dentistResponse.setName("Dr. Karim Benali");
        dentistResponse.setEmail("karim@example.com");
        dentistResponse.setSpecialty("Orthodontie");
    }

    @Test
    void getById_success() {
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(dentistMapper.toResponse(dentist)).thenReturn(dentistResponse);

        DentistResponse result = dentistService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSpecialty()).isEqualTo("Orthodontie");
    }

    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(dentistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dentistService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Dentist not found with id: 99");
    }

    @Test
    void getAll_returnsListOfDentists() {
        when(dentistRepository.findAll()).thenReturn(List.of(dentist));
        when(dentistMapper.toResponse(dentist)).thenReturn(dentistResponse);

        List<DentistResponse> result = dentistService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Dr. Karim Benali");
    }

    @Test
    void getBySpecialty_returnsFilteredList() {
        when(dentistRepository.findBySpecialty("Orthodontie")).thenReturn(List.of(dentist));
        when(dentistMapper.toResponse(dentist)).thenReturn(dentistResponse);

        List<DentistResponse> result = dentistService.getBySpecialty("Orthodontie");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSpecialty()).isEqualTo("Orthodontie");
    }

    @Test
    void getBySpecialty_returnsEmptyList_whenNoMatch() {
        when(dentistRepository.findBySpecialty("Unknown")).thenReturn(List.of());

        List<DentistResponse> result = dentistService.getBySpecialty("Unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void update_success() {
        DentistRequest request = new DentistRequest();
        request.setName("Dr. Karim Updated");
        request.setSpecialty("Implantologie");

        DentistResponse updatedResponse = new DentistResponse();
        updatedResponse.setId(1L);
        updatedResponse.setName("Dr. Karim Updated");
        updatedResponse.setSpecialty("Implantologie");

        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(dentistRepository.save(dentist)).thenReturn(dentist);
        when(dentistMapper.toResponse(dentist)).thenReturn(updatedResponse);

        DentistResponse result = dentistService.update(1L, request);

        assertThat(result.getSpecialty()).isEqualTo("Implantologie");
        verify(dentistMapper).updateEntity(request, dentist);
        verify(dentistRepository).save(dentist);
    }

    @Test
    void delete_success() {
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));

        dentistService.delete(1L);

        verify(dentistRepository).delete(dentist);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(dentistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dentistService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
