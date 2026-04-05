package com.banelethabede.clear_path_parent.organization;

import com.banelethabede.clear_path_parent.organization.dto.OrganizationEnums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Optional<Organization> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    long countByOrganisationIdAndUsedFalse(UUID organisationId);

    List<Organization> findByType(OrganizationEnums type);

    @Query("""
        SELECT o FROM Organization o
        WHERE (:name IS NULL OR LOWER(o.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND   (:type IS NULL OR o.type = :type)
        ORDER BY o.createdAt DESC
    """)
    List<Organization> search(@Param("name") String name,
                              @Param("type") OrganizationEnums type);
}