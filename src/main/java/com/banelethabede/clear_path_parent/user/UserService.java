package com.banelethabede.clear_path_parent.user;

import com.banelethabede.clear_path_parent.role.Role;
import com.banelethabede.clear_path_parent.user.dto.UpdateUserProfileDTO;
import com.banelethabede.clear_path_parent.user.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .active(user.isActive())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public UserResponseDTO findUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "users", allEntries = true)
    public void deleteUserById(UUID id) {
        userRepository.deleteById(id);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "users", key = "#email")
    public void updateUserRole(String email, Role newRole) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(newRole);
        userRepository.save(user);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @CacheEvict(value = "users", key = "#id")
    public UserResponseDTO updateUserStatus(UUID id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(active);
        return mapToDTO(userRepository.save(user));
    }

    @Transactional
    @PreAuthorize("#email == authentication.name or hasRole('ADMIN')")
    @CacheEvict(value = "users", key = "#email")
    public UserResponseDTO updateProfile(String email, UpdateUserProfileDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        return mapToDTO(userRepository.save(user));
    }
}