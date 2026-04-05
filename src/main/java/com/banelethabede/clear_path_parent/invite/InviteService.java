package com.banelethabede.clear_path_parent.invite;

import com.banelethabede.clear_path_parent.invite.dto.InviteRequest;
import com.banelethabede.clear_path_parent.invite.dto.InviteResponse;
import com.banelethabede.clear_path_parent.invite.dto.RedeemByCodeRequest;
import com.banelethabede.clear_path_parent.organization.Organization;
import com.banelethabede.clear_path_parent.organization.OrganizationService;
import com.banelethabede.clear_path_parent.role.RoleName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteTokenRepository inviteTokenRepository;
    private final OrganizationService   organizationService;
    private final InviteCodeGenerator   codeGenerator;
    private final JavaMailSender        mailSender;

    @Value("${app.invite.expiry-hours:48}")
    private int expiryHours;

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public InviteResponse sendInvite(Long inviterId, RoleName inviterRole, InviteRequest request) {

        if (!InvitePermissionPolicy.canInvite(inviterRole, request.getAssignedRole())) {
            throw new InvitePermissionException(
                    inviterRole + " cannot invite role " + request.getAssignedRole()
            );
        }

        // 2. MODERATOR must scope to an organisation
        if (inviterRole == RoleName.ROLE_MODERATOR && request.getOrganisationId() == null) {
            throw new IllegalArgumentException("Moderators must provide an organisationId");
        }

        // 3. No duplicate pending invites to the same email
        if (inviteTokenRepository.existsByInviteeEmailAndUsedFalse(request.getEmail())) {
            throw new IllegalStateException(
                    "A pending invite already exists for " + request.getEmail()
            );
        }

        Organization organization = organizationService.findEntityById(request.getOrganisationId());

        // 4. Generate a unique short code (retry on collision — statistically rare)
        String code = generateUniqueCode();

        // 5. Persist
        InviteToken invite = new InviteToken();
        invite.setCode(code);
        invite.setInviteeEmail(request.getEmail());
        invite.setAssignedRole(request.getAssignedRole());
        invite.setInvitedById(inviterId);
        invite.setInvitedByRole(inviterRole);
        invite.setExpiresAt(Instant.now().plus(expiryHours, ChronoUnit.HOURS));
        invite.setOrganization(organization);

        inviteTokenRepository.save(invite);

        // 6. Send email with both the link and the code
        sendInviteEmail(invite);

        log.info("Invite sent to {} by {} (role={}) — code={}",
                invite.getInviteeEmail(), inviterId, inviterRole, code);

        return InviteResponse.builder()
                .token(invite.getToken())
                .code(invite.getCode())
                .inviteeEmail(invite.getInviteeEmail())
                .assignedRole(invite.getAssignedRole())
                .expiresAt(invite.getExpiresAt())
                .build();
    }

    // -------------------------------------------------------------------------
    // Redemption path 1: clicked the email link  →  /register?token=<uuid>
    // -------------------------------------------------------------------------

    @Transactional
    public InviteToken validateAndConsumeByToken(String token) {
        InviteToken invite = inviteTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidInviteTokenException("Invite link is invalid"));

        assertRedeemable(invite);
        markUsed(invite);
        return invite;
    }

    // -------------------------------------------------------------------------
    // Redemption path 2: typed the code on the register page
    // -------------------------------------------------------------------------

    @Transactional
    public InviteToken validateAndConsumeByCode(RedeemByCodeRequest request) {
        InviteToken invite = inviteTokenRepository
                .findByCodeIgnoreCase(request.getCode())
                .orElseThrow(() -> new InvalidInviteTokenException("Invite code not found"));

        // Email must match — prevents code guessing attacks
        if (!invite.getInviteeEmail().equalsIgnoreCase(request.getEmail())) {
            throw new InvalidInviteTokenException("Email does not match this invite code");
        }

        assertRedeemable(invite);
        markUsed(invite);
        return invite;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void assertRedeemable(InviteToken invite) {
        if (invite.isUsed()) {
            throw new InvalidInviteTokenException("This invite has already been used");
        }
        if (Instant.now().isAfter(invite.getExpiresAt())) {
            throw new InvalidInviteTokenException("This invite has expired");
        }
    }

    private void markUsed(InviteToken invite) {
        invite.setUsed(true);
        inviteTokenRepository.save(invite);
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < 5; attempt++) {
            String code = codeGenerator.generate();
            if (!inviteTokenRepository.existsByCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Failed to generate a unique invite code — try again");
    }

    private void sendInviteEmail(InviteToken invite) {
        String link = baseUrl + "/register?token=" + invite.getToken();
        String roleFriendly = invite.getAssignedRole().name().replace("ROLE_", "").toLowerCase();

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(invite.getInviteeEmail());
        msg.setSubject("You've been invited to ClearPath");
        msg.setText("""
            Hi,

            You've been invited to join ClearPath as a %s.

            OPTION 1 — Click the link below to register instantly:
            %s

            OPTION 2 — Go to %s/register and enter this code:
            Code: %s
            (You'll also need to enter your email address)

            This invite expires in %d hours.

            If you didn't expect this email, you can safely ignore it.
            """.formatted(roleFriendly, link, baseUrl, invite.getCode(), expiryHours));

        mailSender.send(msg);
    }
}