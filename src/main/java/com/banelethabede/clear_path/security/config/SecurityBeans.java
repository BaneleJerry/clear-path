package com.banelethabede.clear_path.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor 
public class SecurityBeans {
    
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            // 1. ADD THIS: Allows H2 Console frames to display
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exceptionHandling ->
                            exceptionHandling
                            .authenticationEntryPoint(new RestAuthenticationEntryPoint())
            )
            .authorizeHttpRequests((auth) ->
                    // 2. Wildcard (/**) is correctly added here
                    auth.requestMatchers("/api/auth/**", "/h2-console/**").permitAll() 
                        .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
                        .anyRequest().authenticated()
            )
            .sessionManagement(manager -> manager
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
}
}