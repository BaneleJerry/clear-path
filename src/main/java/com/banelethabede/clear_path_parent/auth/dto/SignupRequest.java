package com.banelethabede.clear_path_parent.auth.dto;


import com.banelethabede.clear_path_parent.user.dto.UserDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest extends UserDTO {
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 8 characters")
    private String password;

}
