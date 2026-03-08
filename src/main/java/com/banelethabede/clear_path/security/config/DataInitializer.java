package com.banelethabede.clear_path.security.config;

import com.banelethabede.clear_path.roles.Role;
import com.banelethabede.clear_path.roles.RoleName;
import com.banelethabede.clear_path.roles.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, RoleName.ROLE_USER));
            roleRepository.save(new Role(null, RoleName.ROLE_MODERATOR));
            roleRepository.save(new Role(null, RoleName.ROLE_ADMIN));
            roleRepository.save(new Role(null, RoleName.ROLE_STAFF));
            System.out.println(">> Database Seeded: Roles created.");
        }
    }
}