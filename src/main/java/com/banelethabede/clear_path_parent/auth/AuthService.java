package com.banelethabede.clear_path_parent.auth;


import com.banelethabede.clear_path_parent.auth.dto.AuthResponse;
import com.banelethabede.clear_path_parent.auth.dto.IndividualRegistrationRequest;
import com.banelethabede.clear_path_parent.auth.dto.LoginRequest;
import com.banelethabede.clear_path_parent.organization.Organization;
import com.banelethabede.clear_path_parent.organization.OrganizationFactory;
import com.banelethabede.clear_path_parent.organization.OrganizationRepository;
import com.banelethabede.clear_path_parent.role.Role;
import com.banelethabede.clear_path_parent.role.RoleName;
import com.banelethabede.clear_path_parent.role.RoleService;
import com.banelethabede.clear_path_parent.security.jwt.JwtService;
import com.banelethabede.clear_path_parent.user.User;
import com.banelethabede.clear_path_parent.user.UserFactory;
import com.banelethabede.clear_path_parent.user.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationFactory organizationFactory;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final UserFactory userFactory;

    @Transactional
    public void registerIndividual(IndividualRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EntityExistsException("Email already exists");
        }

        Organization organization = organizationFactory.createBaseOrganization(request.getEmail());

        Role role = roleService.getRole(RoleName.ROLE_USER);

        organizationRepository.save(organization);

        String password = passwordEncoder.encode(request.getPassword());
        User user = userFactory.createBaseUser(request,role, password,organization);
        userRepository.save(user);
    }

    @Transactional
    public AuthResponse login(@NonNull LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        user.setActive(true);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), user.getRole().getName());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .build();
    }
}

