package com.banelethabede.clear_path.organization;

import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;

import com.banelethabede.clear_path.organization.dto.OrganizationEnums;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "organization")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "organization_type", nullable = false)
    private OrganizationEnums type;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private java.time.LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedBy
    private java.time.LocalDateTime updatedAt;
}

// Organizations (Tenants)
// CREATE TABLE organizations (
//     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
//     name VARCHAR(255) NOT NULL,
//     type organization_type NOT NULL,
//     created_at TIMESTAMP NOT NULL DEFAULT NOW(),
//     updated_at TIMESTAMP NOT NULL DEFAULT NOW()
// );
// Notes

// Individual clients → 1 user

// Company clients → multiple users

// Internal staff may belong to a special organization or be global