package com.banelethabede.clear_path.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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


    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        try {
            // Assuming your service now returns the email or a success message
            String userEmail = authService.login(loginRequest, response);

            return new ResponseEntity<>(userEmail + " signed in", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Login attempt failed: {}", e.getMessage());
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        authService.logout(response);
        return new ResponseEntity<>("You've been signed out!", HttpStatus.OK);
    }
}