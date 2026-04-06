package com.banelethabede.clear_path_parent.invite;

import com.banelethabede.clear_path_parent.invite.dto.InviteRequest;
import com.banelethabede.clear_path_parent.invite.dto.InviteResponse;
import com.banelethabede.clear_path_parent.invite.dto.RedeemByCodeRequest;
import com.banelethabede.clear_path_parent.organization.OrganizationService;
import com.banelethabede.clear_path_parent.role.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Tells JUnit to use Mockito — creates all @Mock fields automatically
@ExtendWith(MockitoExtension.class)
class InviteServiceTest {

    // @Mock creates a fake version of the dependency — it does nothing by default
    @Mock private InviteTokenRepository inviteTokenRepository;
    @Mock private InviteCodeGenerator   codeGenerator;
    @Mock private JavaMailSender        mailSender;
    @Mock private OrganizationService   organizationService;

    // @InjectMocks creates the real InviteService and injects the mocks above into it
    @InjectMocks
    private InviteService inviteService;

    // Common test data — built fresh before each test
    private InviteRequest validRequest;

    @BeforeEach
    void setUp() {
        // Runs before EVERY test — reset your test data here
        validRequest = new InviteRequest();
        validRequest.setEmail("newuser@example.com");
        validRequest.setAssignedRole(RoleName.ROLE_USER);

        // Inject @Value fields manually since there's no Spring context
        org.springframework.test.util.ReflectionTestUtils.setField(inviteService, "expiryHours", 48);
        org.springframework.test.util.ReflectionTestUtils.setField(inviteService, "baseUrl", "https://clearpath.com");
    }

    // -------------------------------------------------------------------------
    // @Nested groups related tests — keeps things readable
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("sendInvite()")
    class SendInvite {

        @Test
        @DisplayName("ADMIN can invite ROLE_USER — saves token and sends email")
        void admin_can_invite_user() {
            // ARRANGE
            when(inviteTokenRepository.existsByInviteeEmailAndUsedFalse(any())).thenReturn(false);
            when(codeGenerator.generate()).thenReturn("ABCD1234");
            when(inviteTokenRepository.existsByCode("ABCD1234")).thenReturn(false);
            // save() just returns whatever was passed in
            when(inviteTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // ACT
            InviteResponse response = inviteService.sendInvite(UUID.randomUUID(), RoleName.ROLE_ADMIN, validRequest);

            // ASSERT
            assertThat(response.getInviteeEmail()).isEqualTo("newuser@example.com");
            assertThat(response.getAssignedRole()).isEqualTo(RoleName.ROLE_USER);
            assertThat(response.getCode()).isEqualTo("ABCD1234");

            // Verify save was called exactly once
            verify(inviteTokenRepository, times(1)).save(any(InviteToken.class));

            // Verify email was sent exactly once
            verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        }

        @Test
        @DisplayName("STAFF can invite ROLE_MODERATOR")
        void staff_can_invite_moderator() {
            // ARRANGE
            InviteRequest request = new InviteRequest();
            request.setEmail("mod@example.com");
            request.setAssignedRole(RoleName.ROLE_MODERATOR);

            when(inviteTokenRepository.existsByInviteeEmailAndUsedFalse(any())).thenReturn(false);
            when(codeGenerator.generate()).thenReturn("WXYZ5678");
            when(inviteTokenRepository.existsByCode(any())).thenReturn(false);
            when(inviteTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // ACT
            InviteResponse response = inviteService.sendInvite(UUID.randomUUID(), RoleName.ROLE_STAFF, request);

            // ASSERT
            assertThat(response.getAssignedRole()).isEqualTo(RoleName.ROLE_MODERATOR);
        }

        @Test
        @DisplayName("STAFF cannot invite ROLE_ADMIN — throws InvitePermissionException")
        void staff_cannot_invite_admin() {
            // ARRANGE
            InviteRequest request = new InviteRequest();
            request.setEmail("admin@example.com");
            request.setAssignedRole(RoleName.ROLE_ADMIN);

            // ACT + ASSERT — assertThatThrownBy checks that an exception IS thrown
            assertThatThrownBy(() -> inviteService.sendInvite(UUID.randomUUID(), RoleName.ROLE_STAFF, request))
                    .isInstanceOf(InvitePermissionException.class)
                    .hasMessageContaining("ROLE_STAFF");

            // Verify nothing was saved and no email was sent
            verify(inviteTokenRepository, never()).save(any());
            verify(mailSender, never()).send(any(SimpleMailMessage.class));
        }

        @Test
        @DisplayName("STAFF cannot invite another ROLE_STAFF")
        void staff_cannot_invite_staff() {
            InviteRequest request = new InviteRequest();
            request.setEmail("staff2@example.com");
            request.setAssignedRole(RoleName.ROLE_STAFF);

            assertThatThrownBy(() -> inviteService.sendInvite(UUID.randomUUID(), RoleName.ROLE_STAFF, request))
                    .isInstanceOf(InvitePermissionException.class);

            verify(mailSender, never()).send(any(SimpleMailMessage.class));
        }

        @Test
        @DisplayName("ROLE_USER cannot invite anyone")
        void user_cannot_invite() {
            assertThatThrownBy(() -> inviteService.sendInvite(UUID.randomUUID(), RoleName.ROLE_USER, validRequest))
                    .isInstanceOf(InvitePermissionException.class);
        }

        @Test
        @DisplayName("Duplicate pending invite for same email — throws IllegalStateException")
        void duplicate_pending_invite_throws() {
            // ARRANGE — simulate a pending invite already existing
            when(inviteTokenRepository.existsByInviteeEmailAndUsedFalse("newuser@example.com"))
                    .thenReturn(true);

            // ACT + ASSERT
            assertThatThrownBy(() -> inviteService.sendInvite(UUID.randomUUID(), RoleName.ROLE_ADMIN, validRequest))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("newuser@example.com");

            verify(mailSender, never()).send(any(SimpleMailMessage.class));
        }

        @Test
        @DisplayName("MODERATOR invite without organisationId — throws IllegalArgumentException")
        void moderator_invite_without_org_throws() {
            // ARRANGE
            InviteRequest request = new InviteRequest();
            request.setEmail("user@example.com");
            request.setAssignedRole(RoleName.ROLE_USER);
            request.setOrganisationId(null); // missing org

            // ACT + ASSERT
            assertThatThrownBy(() -> inviteService.sendInvite(UUID.randomUUID(), RoleName.ROLE_MODERATOR, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("organisationId");
        }

        @Test
        @DisplayName("Email contains both the link and the short code")
        void email_contains_link_and_code() {
            // ARRANGE
            when(inviteTokenRepository.existsByInviteeEmailAndUsedFalse(any())).thenReturn(false);
            when(codeGenerator.generate()).thenReturn("LINK1234");
            when(inviteTokenRepository.existsByCode(any())).thenReturn(false);
            when(inviteTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // ACT
            inviteService.sendInvite(UUID.randomUUID(), RoleName.ROLE_ADMIN, validRequest);

            // ASSERT — capture the actual email that was sent and inspect it
            ArgumentCaptor<SimpleMailMessage> emailCaptor =
                    ArgumentCaptor.forClass(SimpleMailMessage.class);

            verify(mailSender).send(emailCaptor.capture());
            SimpleMailMessage sentEmail = emailCaptor.getValue();

            assertThat(sentEmail.getTo()).contains("newuser@example.com");
            assertThat(sentEmail.getText()).contains("LINK1234");        // code present
            assertThat(sentEmail.getText()).contains("https://clearpath.com/register?token="); // link present
        }
    }

    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("validateAndConsumeByCode()")
    class ValidateAndConsumeByCode {

        @Test
        @DisplayName("Valid code + matching email — marks token as used and returns invite")
        void valid_code_and_email_succeeds() {
            // ARRANGE
            InviteToken invite = buildInvite("ABCD1234", "user@example.com", false, 48);
            when(inviteTokenRepository.findByCodeIgnoreCase("ABCD1234")).thenReturn(Optional.of(invite));
            when(inviteTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            RedeemByCodeRequest request = new RedeemByCodeRequest();
            request.setCode("ABCD1234");
            request.setEmail("user@example.com");

            // ACT
            InviteToken result = inviteService.validateAndConsumeByCode(request);

            // ASSERT
            assertThat(result.isUsed()).isTrue();
            verify(inviteTokenRepository).save(invite);
        }

        @Test
        @DisplayName("Code not found — throws InvalidInviteTokenException")
        void code_not_found_throws() {
            when(inviteTokenRepository.findByCodeIgnoreCase(any())).thenReturn(Optional.empty());

            RedeemByCodeRequest request = new RedeemByCodeRequest();
            request.setCode("notfound");
            request.setEmail("user@example.com");

            assertThatThrownBy(() -> inviteService.validateAndConsumeByCode(request))
                    .isInstanceOf(InvalidInviteTokenException.class);
        }

        @Test
        @DisplayName("Email mismatch — throws InvalidInviteTokenException")
        void email_mismatch_throws() {
            InviteToken invite = buildInvite("ABCD1234", "real@example.com", false, 48);
            when(inviteTokenRepository.findByCodeIgnoreCase("ABCD1234")).thenReturn(Optional.of(invite));

            RedeemByCodeRequest request = new RedeemByCodeRequest();
            request.setCode("ABCD1234");
            request.setEmail("wrong@example.com"); // different email

            assertThatThrownBy(() -> inviteService.validateAndConsumeByCode(request))
                    .isInstanceOf(InvalidInviteTokenException.class)
                    .hasMessageContaining("Email does not match");
        }

        @Test
        @DisplayName("Already used token — throws InvalidInviteTokenException")
        void already_used_token_throws() {
            InviteToken invite = buildInvite("ABCD1234", "user@example.com", true, 48); // used=true
            when(inviteTokenRepository.findByCodeIgnoreCase("ABCD1234")).thenReturn(Optional.of(invite));

            RedeemByCodeRequest request = new RedeemByCodeRequest();
            request.setCode("ABCD1234");
            request.setEmail("user@example.com");

            assertThatThrownBy(() -> inviteService.validateAndConsumeByCode(request))
                    .isInstanceOf(InvalidInviteTokenException.class)
                    .hasMessageContaining("already been used");
        }

        @Test
        @DisplayName("Expired token — throws InvalidInviteTokenException")
        void expired_token_throws() {
            InviteToken invite = buildInvite("ABCD1234", "user@example.com", false, -1); // expired
            when(inviteTokenRepository.findByCodeIgnoreCase("ABCD1234")).thenReturn(Optional.of(invite));

            RedeemByCodeRequest request = new RedeemByCodeRequest();
            request.setCode("ABCD1234");
            request.setEmail("user@example.com");

            assertThatThrownBy(() -> inviteService.validateAndConsumeByCode(request))
                    .isInstanceOf(InvalidInviteTokenException.class)
                    .hasMessageContaining("expired");
        }
    }

    // -------------------------------------------------------------------------
    // Test data builder — keeps test setup clean and readable
    // -------------------------------------------------------------------------

    /**
     * @param expiryHours positive = expires in the future, negative = already expired
     */
    private InviteToken buildInvite(String code, String email, boolean used, int expiryHours) {
        InviteToken invite = new InviteToken();
        invite.setCode(code);
        invite.setInviteeEmail(email);
        invite.setAssignedRole(RoleName.ROLE_USER);
        invite.setUsed(used);
        invite.setExpiresAt(Instant.now().plus(expiryHours, ChronoUnit.HOURS));
        return invite;
    }
}