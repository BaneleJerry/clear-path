package com.banelethabede.clear_path_parent.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository  extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    int countByOrganizationId(UUID organisationId);

    long countByLastLoginAtAfter(LocalDateTime dateTime);
}
