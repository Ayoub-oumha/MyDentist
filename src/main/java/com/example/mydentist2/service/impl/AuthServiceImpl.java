package com.example.mydentist2.service.impl;

import com.example.mydentist2.dto.auth.AuthResponse;
import com.example.mydentist2.dto.auth.LoginRequest;
import com.example.mydentist2.dto.auth.RegisterRequest;
import com.example.mydentist2.exception.BusinessException;
import com.example.mydentist2.model.Dentist;
import com.example.mydentist2.model.Patient;
import com.example.mydentist2.model.User;
import com.example.mydentist2.repository.UserRepository;
import com.example.mydentist2.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already in use: " + request.getEmail());
        }

        User user = buildUser(request);
        userRepository.save(user);

        // TODO: generate real JWT token
        String token = "jwt-token-placeholder";
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid credentials"));

        // TODO: generate real JWT token
        String token = "jwt-token-placeholder";
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    private User buildUser(RegisterRequest request) {
        if (request.getRole() == User.Role.DENTIST) {
            Dentist dentist = new Dentist();
            dentist.setName(request.getName());
            dentist.setEmail(request.getEmail());
            dentist.setPassword(passwordEncoder.encode(request.getPassword()));
            dentist.setRole(User.Role.DENTIST);
            dentist.setPhone(request.getPhone());
            dentist.setSpecialty(request.getSpecialty());
            dentist.setAddress(request.getAddress());
            dentist.setLatitude(request.getLatitude());
            dentist.setLongitude(request.getLongitude());
            return dentist;
        }

        Patient patient = new Patient();
        patient.setName(request.getName());
        patient.setEmail(request.getEmail());
        patient.setPassword(passwordEncoder.encode(request.getPassword()));
        patient.setRole(User.Role.PATIENT);
        patient.setPhone(request.getPhone());
        patient.setAddress(request.getAddress());
        return patient;
    }
}
