package com.example.mydentist2.dto.auth;

import com.example.mydentist2.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    private User.Role role;

    // Patient-specific (optional)
    private String phone;
    private String address;

    // Dentist-specific (optional)
    private String specialty;
    private Double latitude;
    private Double longitude;
}
