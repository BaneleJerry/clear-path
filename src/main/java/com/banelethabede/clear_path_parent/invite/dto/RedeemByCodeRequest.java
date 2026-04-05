package com.banelethabede.clear_path_parent.invite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RedeemByCodeRequest {

    @NotBlank
    private String email;   // must match the invite's email

    @NotBlank @Size(min = 8, max = 8)
    private String code;
}