package com.banelethabede.clear_path_parent.organization.dto;

import com.banelethabede.clear_path_parent.organization.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    boolean existsByName(String name);
    Organization findByName(String name);
}
