package com.example.mydentist2.dto.patient;

import lombok.Data;

@Data
public class PatientResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
}
