package com.banelethabede.clear_path_parent.auth.dto;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
public class TokenValidateResponse {
    private boolean isAuthenticated;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;
}
