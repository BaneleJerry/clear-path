package com.banelethabede.clear_path_parent.invite;

import com.banelethabede.clear_path_parent.organization.Organization;
import com.banelethabede.clear_path_parent.role.RoleName;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invite_tokens")
@Getter @Setter @NoArgsConstructor
public class InviteToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** UUID-based token — used in the email signup link */
    @Column(nullable = false, unique = true)
    private String token = UUID.randomUUID().toString();

    /**
     * 8-character alphanumeric code — human-readable, used on the register page.
     * Both token and code resolve to the same invite.
     */
    @Column(nullable = false, unique = true, length = 8)
    private String code;

    @Column(nullable = false)
    private String inviteeEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleName assignedRole;

    @Column(nullable = false)
    private Long invitedById;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleName invitedByRole;

    @Column(nullable = false)
    private Instant expiresAt;

    private boolean used = false;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    /** Optional: org scope for MODERATOR invites */
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;
}