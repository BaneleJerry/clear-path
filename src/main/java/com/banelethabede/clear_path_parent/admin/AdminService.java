package com.banelethabede.clear_path_parent.admin;

import com.banelethabede.clear_path_parent.invite.InviteTokenRepository;
import com.banelethabede.clear_path_parent.organization.OrganizationRepository;
import com.banelethabede.clear_path_parent.user.UserRepository;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final InviteTokenRepository  inviteTokenRepository;


    public DashboardStats getDashboardStats() {
        return new DashboardStats(
                userRepository.count(),
                organizationRepository.count(),
                inviteTokenRepository.countByUsedFalseAndExpiresAtAfter(Instant.now()),
                userRepository.countByLastLoginAtAfter(LocalDateTime.now().minusDays(1))
        );
    }
}
