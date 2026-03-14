package com.banelethabede.clear_path_parent.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO
{
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email Format")
    private String email;


}