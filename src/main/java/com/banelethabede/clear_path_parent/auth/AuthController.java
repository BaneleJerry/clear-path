package com.banelethabede.clear_path_parent.auth;

import com.banelethabede.clear_path_parent.auth.dto.AuthResponse;
import com.banelethabede.clear_path_parent.auth.dto.LoginRequest;
import com.banelethabede.clear_path_parent.auth.dto.RegisterRequest;
import com.banelethabede.clear_path_parent.auth.dto.TokenValidateResponse;
import com.banelethabede.clear_path_parent.common.ApiResponse;
import com.banelethabede.clear_path_parent.common.ApiResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Requires a valid invite token (from email link) OR invite code + email (manual).
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest
    ) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseUtil.success(
                        "Registration successful, Try to Login",
                        httpRequest.getRequestURI(),
                        null
                )
        );
    }

    /**
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * GET /api/auth/validate
     */
    @GetMapping("/validate")
    public ResponseEntity<TokenValidateResponse> validateToken(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(
                TokenValidateResponse.builder()
                        .username(authentication.getName())
                        .isAuthenticated(true)
                        .authorities(authentication.getAuthorities())
                        .build()
        );
    }
}