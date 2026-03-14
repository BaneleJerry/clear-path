package com.banelethabede.clear_path_parent.user;

import com.banelethabede.clear_path_parent.role.Role;
import com.banelethabede.clear_path_parent.user.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {
    public User createBaseUser(UserDTO dto, Role role, String encodedPassword) {
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(encodedPassword)
                .role(role)
                .isActive(true)
                .build();
    }
}
