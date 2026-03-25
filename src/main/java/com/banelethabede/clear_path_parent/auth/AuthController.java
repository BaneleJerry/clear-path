package com.banelethabede.clear_path_parent.auth;



import com.banelethabede.clear_path_parent.auth.dto.AuthResponse;
import com.banelethabede.clear_path_parent.auth.dto.IndividualRegistrationRequest;
import com.banelethabede.clear_path_parent.auth.dto.LoginRequest;
import com.banelethabede.clear_path_parent.common.ApiResponse;
import com.banelethabede.clear_path_parent.common.ApiResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register an individual user.
     */
    @PostMapping("/register/individual")
    public ResponseEntity<ApiResponse<Void>> registerIndividual(
            @Valid @RequestBody IndividualRegistrationRequest request,
            HttpServletRequest httpRequest
    ) {
        authService.registerIndividual(request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Individual user registered successfully",
                        httpRequest.getRequestURI(),
                        null
                )
        );
    }


    /**
     * Login endpoint.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {

        AuthResponse auth = authService.login(request);

        return ResponseEntity.ok(ApiResponseUtil.success(
                "Login Succesfully",
                httpRequest.getRequestURI(),
                auth
        ));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("username", authentication.getName());
        // .getAuthorities() returns a collection; good for checking roles like 'ADMIN'
        response.put("authorities", authentication.getAuthorities());

        return ResponseEntity.ok(response);
    }


}