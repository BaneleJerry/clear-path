package com.banelethabede.clear_path.auth;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.banelethabede.clear_path.auth.dto.LoginRequest;
import com.banelethabede.clear_path.auth.dto.RegisterRequest;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    // @SuppressWarnings(value = { "notUsed" })
    // private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /**
     * Register an individual user.
     */
    @PostMapping("/register/individual")
    public ResponseEntity<?> registerIndividual(@RequestBody RegisterRequest request) {

        authService.registerIndividual(request);

        return ResponseEntity.ok("Individual user registered successfully");
    }

    /**
     * Register a new organization and moderator user.
     */
    @PostMapping("/register/organization")
    public ResponseEntity<?> registerOrganization(@RequestBody RegisterRequest request) {

        authService.registerOrgAndModeratorUser(request);

        return ResponseEntity.ok("Organization and moderator registered successfully");
    }

    /**
     * Add a user to an existing organization.
     */
    @PostMapping("/register/user")
    public ResponseEntity<?> addUserToOrganization(@RequestBody RegisterRequest request) {

        authService.addUserToOrganization(request);

        return ResponseEntity.ok("User added to organization successfully");
    }

    /**
     * Login endpoint.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response) {

        String username = authService.login(request, response);

        return ResponseEntity.ok(username);
    }

    /**
     * Logout endpoint.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        authService.logout(response);

        return ResponseEntity.ok("Logged out successfully");
    }
}