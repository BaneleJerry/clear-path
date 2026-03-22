package com.banelethabede.clear_path_parent.security.service;



import com.banelethabede.clear_path_parent.auth.AuthService;
import com.banelethabede.clear_path_parent.auth.UserCacheService;
import com.banelethabede.clear_path_parent.auth.dto.UserAuthCache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.banelethabede.clear_path_parent.security.model.UserPrincipal;




import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {


    private final UserCacheService userCacheService;
    @Override
    public UserDetails loadUserByUsername(String username) {
        UserAuthCache cached = userCacheService.getCachedUser(username);
        // Reconstruct a UserPrincipal or SimpleUser from the flat DTO
        return new UserPrincipal(cached);
    }
}