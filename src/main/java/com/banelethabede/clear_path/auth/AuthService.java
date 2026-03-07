package com.banelethabede.clear_path.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.banelethabede.clear_path.auth.dto.LoginRequest;
import com.banelethabede.clear_path.auth.dto.RegisterRequest;
import com.banelethabede.clear_path.roles.Role;
import com.banelethabede.clear_path.roles.RoleName;
import com.banelethabede.clear_path.roles.RoleRepository;
import com.banelethabede.clear_path.security.jwt.JwtService;
import com.banelethabede.clear_path.user.User;
import com.banelethabede.clear_path.user.UserRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;

    public void register(RegisterRequest request) {
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EntityExistsException("User with email " + request.getEmail() + " already exists");
        }

        //Create new user 
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Role role = roleRepository.findByName(RoleName.ROLE_USER);
        user.setRole(role);
        userRepository.save(user);
    }
    
     public String login(LoginRequest request,  HttpServletResponse response) {

        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication authResult = authenticationManager.authenticate(authToken);

        SecurityContextHolder.getContext().setAuthentication(authResult);

        jwtService.generateToken(request.getEmail(), response);

        UserDetails userDetails = (UserDetails) authResult.getPrincipal();
        return userDetails.getUsername();
    }

    public void logout(HttpServletResponse response) {
        jwtService.removeTokenFromCookie(response);
    }

}
