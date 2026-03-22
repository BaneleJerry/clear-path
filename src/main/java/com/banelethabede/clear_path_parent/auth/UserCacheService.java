package com.banelethabede.clear_path_parent.auth;

import com.banelethabede.clear_path_parent.auth.dto.UserAuthCache;
import com.banelethabede.clear_path_parent.user.User;
import com.banelethabede.clear_path_parent.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCacheService {
    private final UserRepository userRepository;

    @Cacheable(value = "user_auth", key = "#username")
    public UserAuthCache getCachedUser(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new UserAuthCache(
                user.getEmail(),
                user.getPassword(),
                user.getRole().getName().name(),
                user.isActive()
        );
    }
}