package com.banelethabede.clear_path_parent.auth.dto;

import com.banelethabede.clear_path_parent.role.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuthCache implements Serializable {
    private String username;
    private String password;
    private String role;
    private boolean isActive;
}