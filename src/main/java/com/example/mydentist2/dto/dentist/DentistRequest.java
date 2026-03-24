package com.example.mydentist2.dto.dentist;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DentistRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    private String phone;
    private String specialty;
    private String address;
    private Double latitude;
    private Double longitude;
}
