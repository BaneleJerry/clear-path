package com.banelethabede.clear_path_parent.auth;

import com.banelethabede.clear_path_parent.auth.dto.AuthResponse;
import com.banelethabede.clear_path_parent.auth.dto.LoginRequest;
import com.banelethabede.clear_path_parent.auth.dto.RegisterRequest;
import com.banelethabede.clear_path_parent.invite.InviteService;
import com.banelethabede.clear_path_parent.invite.InviteToken;
import com.banelethabede.clear_path_parent.invite.InvalidInviteTokenException;
import com.banelethabede.clear_path_parent.invite.dto.RedeemByCodeRequest;
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
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository       userRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationFactory  organizationFactory;
    private final PasswordEncoder      passwordEncoder;
    private final JwtService           jwtService;
    private final RoleService          roleService;
    private final AuthenticationManager authenticationManager;
    private final UserFactory          userFactory;
    private final InviteService        inviteService;

    @Transactional
    public void register(RegisterRequest request) {


        InviteToken invite = resolveInvite(request);

        if (!invite.getInviteeEmail().equalsIgnoreCase(request.getEmail())) {
            throw new InvalidInviteTokenException(
                    "Email does not match the invite — use " + invite.getInviteeEmail()
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EntityExistsException("An account with this email already exists");
        }

        Role role = roleService.getRole(invite.getAssignedRole());

        Organization organization = resolveOrganization(invite);
        if (organization != null) {
            organizationRepository.save(organization);
        }

        // 6. Create and save user
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = userFactory.createBaseUser(request, role, encodedPassword, organization);
        userRepository.save(user);

        log.info("User registered: email={}, role={}, org={}",
                request.getEmail(),
                invite.getAssignedRole(),
                organization != null ? organization.getId() : "none"
        );
    }

    private Organization resolveOrganization(InviteToken invite) {
        return switch (invite.getAssignedRole()) {
            case ROLE_USER -> organizationFactory.createBaseOrganization(invite.getInviteeEmail());
            default -> {
                if (invite.getOrganization() == null || invite.getOrganization().getId() == null) {
                    yield null; // ADMIN/STAFF don't need an org
                }
                yield organizationRepository.findById(invite.getOrganization().getId())
                        .orElseThrow(() -> new IllegalStateException(
                                "Organisation on invite no longer exists: " + invite.getOrganization().getId()
                        ));
            }
        };
    }

    // -------------------------------------------------------------------------
    // Auto-detect whether the caller supplied a token or a code
    // -------------------------------------------------------------------------

    private InviteToken resolveInvite(RegisterRequest request) {
        boolean hasToken = request.getInviteToken() != null
                && !request.getInviteToken().isBlank();
        boolean hasCode  = request.getInviteCode() != null
                && !request.getInviteCode().isBlank();

        if (!hasToken && !hasCode) {
            throw new InvalidInviteTokenException(
                    "An invite token or invite code is required to register"
            );
        }


        if (hasToken) {
            return inviteService.validateAndConsumeByToken(request.getInviteToken());
        }


        RedeemByCodeRequest codeRequest = new RedeemByCodeRequest();
        codeRequest.setCode(request.getInviteCode());
        codeRequest.setEmail(request.getEmail());
        return inviteService.validateAndConsumeByCode(codeRequest);
    }

    // -------------------------------------------------------------------------
    // Login — unchanged
    // -------------------------------------------------------------------------

    @Transactional
    public AuthResponse login(@NonNull LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException(
                    "Password mismatch for: " + request.getEmail() +
                            " | stored hash: " + user.getPassword()
            );
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