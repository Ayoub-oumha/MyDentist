package com.example.mydentist2.service;

import com.example.mydentist2.config.JwtService;
import com.example.mydentist2.dto.auth.AuthResponse;
import com.example.mydentist2.dto.auth.LoginRequest;
import com.example.mydentist2.dto.auth.RegisterRequest;
import com.example.mydentist2.exception.BusinessException;
import com.example.mydentist2.model.Patient;
import com.example.mydentist2.model.User;
import com.example.mydentist2.repository.UserRepository;
import com.example.mydentist2.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;

    @InjectMocks private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private Patient patient;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Alice Martin");
        registerRequest.setEmail("alice@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole(User.Role.PATIENT);
        registerRequest.setPhone("0612345678");
        registerRequest.setAddress("12 Rue de Paris");

        patient = new Patient();
        patient.setId(1L);
        patient.setName("Alice Martin");
        patient.setEmail("alice@example.com");
        patient.setPassword("encoded_password");
        patient.setRole(User.Role.PATIENT);
    }

    @Test
    void register_success() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtService.generateToken(anyString(), anyMap())).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
        assertThat(response.getRole()).isEqualTo(User.Role.PATIENT);
        verify(userRepository).save(any());
    }

    @Test
    void register_throwsBusinessException_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email already in use");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_asDentist_success() {
        RegisterRequest dentistRequest = new RegisterRequest();
        dentistRequest.setName("Dr. Karim");
        dentistRequest.setEmail("karim@example.com");
        dentistRequest.setPassword("password123");
        dentistRequest.setRole(User.Role.DENTIST);
        dentistRequest.setSpecialty("Orthodontie");
        dentistRequest.setLatitude(34.02);
        dentistRequest.setLongitude(-6.84);

        when(userRepository.existsByEmail(dentistRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(2L);
            return u;
        });
        when(jwtService.generateToken(anyString(), anyMap())).thenReturn("jwt-token");

        AuthResponse response = authService.register(dentistRequest);

        assertThat(response.getRole()).isEqualTo(User.Role.DENTIST);
    }

    @Test
    void login_success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("alice@example.com");
        loginRequest.setPassword("password123");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(patient));
        when(jwtService.generateToken(anyString(), anyMap())).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_throwsBusinessException_whenUserNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("unknown@example.com");
        loginRequest.setPassword("password123");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid credentials");
    }
}
