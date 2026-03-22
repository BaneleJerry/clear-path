package com.banelethabede.clear_path_parent.user.dto;

import com.banelethabede.clear_path_parent.role.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserResponseDTO {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private boolean active;
}