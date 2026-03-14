package com.banelethabede.clear_path_parent.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}