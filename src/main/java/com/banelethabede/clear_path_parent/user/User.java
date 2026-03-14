package com.banelethabede.clear_path_parent.user;


//import com.banelethabede.clear_path.organization.Organization;
//import com.banelethabede.clear_path.roles.Role;
import com.banelethabede.clear_path_parent.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email")
})
@EntityListeners(AuditingEntityListener.class)
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "organization_id")
//    private Organization organization;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private boolean isActive;

    @Column
    private java.time.LocalDateTime lastLoginAt;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private java.time.LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private java.time.LocalDateTime updatedAt;


}

// CREATE TABLE users (
//     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
//     organization_id UUID REFERENCES organizations(id) ON DELETE SET NULL,
//     email VARCHAR(255) NOT NULL UNIQUE,
//     password_hash TEXT NOT NULL,
//     first_name VARCHAR(100) NOT NULL,
//     last_name VARCHAR(100) NOT NULL,
//     is_active BOOLEAN NOT NULL DEFAULT TRUE,
//     last_login_at TIMESTAMP,
//     created_at TIMESTAMP NOT NULL DEFAULT NOW(),
//     updated_at TIMESTAMP NOT NULL DEFAULT NOW()
// );