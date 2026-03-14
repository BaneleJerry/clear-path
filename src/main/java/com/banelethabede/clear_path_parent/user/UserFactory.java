package com.banelethabede.clear_path_parent.user;

import com.banelethabede.clear_path_parent.organization.Organization;
import com.banelethabede.clear_path_parent.role.Role;
import com.banelethabede.clear_path_parent.user.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {
    public User createBaseUser(UserDTO dto, Role role, String encodedPassword, Organization organization) {
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(encodedPassword)
                .organization(organization)
                .role(role)
                .isActive(true)
                .build();
    }
}
