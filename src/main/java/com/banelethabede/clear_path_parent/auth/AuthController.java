package com.banelethabede.clear_path_parent.auth;



import com.banelethabede.clear_path_parent.auth.dto.AuthResponse;
import com.banelethabede.clear_path_parent.auth.dto.IndividualRegistrationRequest;
import com.banelethabede.clear_path_parent.auth.dto.LoginRequest;
import com.banelethabede.clear_path_parent.auth.dto.TokenValidateResponse;
import com.banelethabede.clear_path_parent.common.ApiResponse;
import com.banelethabede.clear_path_parent.common.ApiResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



import lombok.RequiredArgsConstructor;

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
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {

        AuthResponse auth = authService.login(request);
        return ResponseEntity.ok(auth);
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenValidateResponse> validateToken(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        TokenValidateResponse tokenValidateResponse = TokenValidateResponse.builder()
                .username(authentication.getName())
                .isAuthenticated(true)
                .authorities(authentication.getAuthorities())
                .build();



        return ResponseEntity.ok(tokenValidateResponse);
    }


}