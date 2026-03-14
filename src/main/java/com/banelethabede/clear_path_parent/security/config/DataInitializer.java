package com.banelethabede.clear_path_parent.security.config;


import com.banelethabede.clear_path_parent.role.Role;
import com.banelethabede.clear_path_parent.role.RoleName;
import com.banelethabede.clear_path_parent.role.RoleRepository;
import com.banelethabede.clear_path_parent.user.User;
import com.banelethabede.clear_path_parent.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        addRoles();
        addUsers();
    }

    private void addRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, RoleName.ROLE_USER));
            roleRepository.save(new Role(null, RoleName.ROLE_MODERATOR));
            roleRepository.save(new Role(null, RoleName.ROLE_ADMIN));
            roleRepository.save(new Role(null, RoleName.ROLE_STAFF));
            System.out.println(">> Database Seeded: Roles created.");
        }
    }

    private void addUsers() {
        if (userRepository.count() == 0) {
            // Fetch roles from the DB to ensure they are managed by JPA
            Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role Admin not found."));
            Role clientRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role Client not found."));

            // 1. Create Admin User
            User admin = User.builder()
                    .email("admin@clearpath.co.za")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Banele")
                    .lastName("Thabede")
                    .role(adminRole)
                    .isActive(true)
                    .build();
            userRepository.save(admin);

            // 2. Create Dummy Client
            User client = User.builder()
                    .email("joe.doe@client.com")
                    .password(passwordEncoder.encode("client123"))
                    .firstName("Joe")
                    .lastName("Doe")
                    .role(clientRole)
                    .isActive(true)
                    .build();
            userRepository.save(client);

            System.out.println(">> Database Seeded: Initial users created.");
        }
    }
}