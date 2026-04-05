package com.banelethabede.clear_path_parent.invite;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface InviteTokenRepository extends JpaRepository<InviteToken, Long> {

    Optional<InviteToken> findByToken(String token);

    Optional<InviteToken> findByCodeIgnoreCase(String code);

    boolean existsByInviteeEmailAndUsedFalse(String email);

    boolean existsByCode(String code);

    long countByOrganisationIdAndUsedFalse(UUID organisationId);
}