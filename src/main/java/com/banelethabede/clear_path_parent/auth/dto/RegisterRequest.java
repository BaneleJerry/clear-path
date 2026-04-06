package com.banelethabede.clear_path_parent.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.banelethabede.clear_path_parent.user.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest extends UserDTO {

    @NotBlank
    @Size(min = 8)
    private String password;
    private String inviteToken;
    private String inviteCode;
}