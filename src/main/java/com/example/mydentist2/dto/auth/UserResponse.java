package com.example.mydentist2.dto.auth;

import com.example.mydentist2.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private User.Role role;
}
