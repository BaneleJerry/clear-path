package com.banelethabede.clear_path_parent.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Automatically injects the repository
public class RoleService {

    private final RoleRepository roleRepository;

    /**
     * Safely retrieves a role by its name.
     * Throws a clear exception if the database hasn't been seeded.
     */
    public Role getRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException(
                        "Required role " + roleName + " was not found in the database. " +
                                "Ensure DataInitializer has run."
                ));
    }
}