package com.example.mydentist2.dto.dentist;

import lombok.Data;

@Data
public class DentistResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String specialty;
    private String address;
    private Double latitude;
    private Double longitude;
}
