package com.banelethabede.clear_path_parent.user.dto;

import com.banelethabede.clear_path_parent.role.Role;
import lombok.Data;

@Data
public class UpdateUserRoleDTO {
    private String email;
    private Role role;
}

@Data
public class UpdateUserStatusDTO {
    private String email;
    private boolean active;
}

@Data
public class UpdateProfileDTO {
    private String firstName;
    private String lastName;
}
