package com.banelethabede.clear_path.auth.dto;

import com.banelethabede.clear_path.organization.dto.OrganizationEnums;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    // --- User Details ---
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    // --- Organization Details ---
    @NotBlank(message = "Organization name is required")
    private String organizationName;

    @NotNull(message = "Organization type is required")
    private OrganizationEnums organizationType;
}