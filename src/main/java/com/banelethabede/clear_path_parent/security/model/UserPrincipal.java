package com.banelethabede.clear_path_parent.security.model;

import com.banelethabede.clear_path_parent.auth.dto.UserAuthCache;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    // We swap the heavy 'User' entity for the light 'UserAuthCache' DTO
    private UserAuthCache cachedUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(new SimpleGrantedAuthority(cachedUser.getRole()));
    }

    @Override
    public String getPassword() {
        return cachedUser.getPassword();
    }

    @Override
    public String getUsername() {
        return cachedUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Use the active status from the DTO
        return cachedUser.isActive();
    }
}