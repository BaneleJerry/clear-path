package com.banelethabede.clear_path_parent.auth;



import com.banelethabede.clear_path_parent.auth.dto.AuthResponse;
import com.banelethabede.clear_path_parent.auth.dto.IndividualRegistrationRequest;
import com.banelethabede.clear_path_parent.auth.dto.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> registerIndividual(@Valid @RequestBody IndividualRegistrationRequest request) {
        authService.registerIndividual(request);
        return ResponseEntity.ok("Individual user registered successfully");
    }


    /**
     * Login endpoint.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login( @Valid @RequestBody LoginRequest request) {

        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(authResponse);
    }


}