package com.banelethabede.clear_path.auth;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.banelethabede.clear_path.auth.dto.LoginRequest;
import com.banelethabede.clear_path.auth.dto.RegisterRequest;
import com.banelethabede.clear_path.organization.Organization;
import com.banelethabede.clear_path.organization.OrganizationRepository;
import com.banelethabede.clear_path.organization.dto.OrganizationEnums;
import com.banelethabede.clear_path.roles.Role;
import com.banelethabede.clear_path.roles.RoleName;
import com.banelethabede.clear_path.roles.RoleRepository;
import com.banelethabede.clear_path.security.jwt.JwtService;
import com.banelethabede.clear_path.user.User;
import com.banelethabede.clear_path.user.UserRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AuthService.class)
class AuthServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setup() {
        // Clear repositories to ensure a fresh state for every test
        userRepository.deleteAll();
        organizationRepository.deleteAll();
        roleRepository.deleteAll();

        Role userRole = new Role();
        userRole.setName(RoleName.ROLE_USER);

        Role moderatorRole = new Role();
        moderatorRole.setName(RoleName.ROLE_MODERATOR);

        roleRepository.save(userRole);
        roleRepository.save(moderatorRole);

        // Mock the encoder behavior
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
    }

    RegisterRequest createIndividualRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@company.com");
        request.setPassword("password");
        request.setFirstName("John");
        request.setLastName("Doe");
        return request;
    }

    RegisterRequest createOrganizationRequest() {
        RegisterRequest request = createIndividualRequest();
        request.setOrganizationName("TestOrg");
        request.setOrganizationType(OrganizationEnums.COMPANY);
        return request;
    }

    @Nested
    public class RegisterIndividualTests {
        @Test
        void shouldRegisterIndividualUserWithOrganization() {

            RegisterRequest request = createIndividualRequest();
            authService.registerIndividual(request);
            assertThat(userRepository.existsByEmail(request.getEmail())).isTrue();
            Organization org = organizationRepository.findAll().get(0);
            assertThat(org.getType()).isEqualTo(OrganizationEnums.INDIVIDUAL);
        }

        @Test
        void shouldThrowExceptionWhenUserAlreadyExists() {

            RegisterRequest request = createIndividualRequest();
            authService.registerIndividual(request);
            assertThatThrownBy(() -> authService.registerIndividual(request)).isInstanceOf(EntityExistsException.class);
        }

    }

    @Nested
    public class RegisterOrgAndModeratorTest {

        @Test
        void ShouldCreaOrgAndModerator() {

            RegisterRequest registerRequest = createOrganizationRequest();

            authService.registerOrgAndModeratorUser(registerRequest);

            assertThat(userRepository.existsByEmail(registerRequest.getEmail())).isTrue();

            Organization org = organizationRepository.findByName("TestOrg");
            assertThat(org).isNotNull();

        }

        @Test
        void shouldFailWhenOrganizationAlreadyExists() {

            RegisterRequest first = createOrganizationRequest();
            authService.registerOrgAndModeratorUser(first);

            RegisterRequest second = createOrganizationRequest();
            second.setEmail("another@company.com"); // different user

            assertThatThrownBy(() -> authService.registerOrgAndModeratorUser(second))
                    .isInstanceOf(EntityExistsException.class);
        }
    }

    @Nested
    class AddUserToOrganizationTests {

        @Test
        void shouldAddUserToExistingOrganization() {

            Organization org = Organization.builder()
                    .name("TestOrg")
                    .type(OrganizationEnums.COMPANY)
                    .build();

            organizationRepository.save(org);

            RegisterRequest request = createIndividualRequest();
            request.setOrganizationName("TestOrg");

            authService.addUserToOrganization(request);

            User user = userRepository
                    .findByEmail(request.getEmail())
                    .orElseThrow(() -> new AssertionError("User not created"));

            assertThat(user.getOrganization().getName())
                    .isEqualTo("TestOrg");
        }

        @Test
        void shouldFailWhenOrganizationDoesNotExist() {

            RegisterRequest request = createIndividualRequest();
            request.setOrganizationName("MissingOrg");

            assertThatThrownBy(() -> authService.addUserToOrganization(request))
                    .isInstanceOf(EntityExistsException.class);
        }
    }

    @Nested
    class LoginTests {

        @Test
        void shouldAuthenticateAndGenerateJwt() {
            // Arrange
            LoginRequest request = new LoginRequest();
            request.setEmail("test@company.com");
            request.setPassword("password");

            Authentication authentication = mock(Authentication.class);
            UserDetails userDetails = mock(UserDetails.class);
            HttpServletResponse response = mock(HttpServletResponse.class);

            // Mocking the authentication flow
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);

            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("test@company.com");

            // Act
            String result = authService.login(request, response);

            // Assert & Verify
            // Ensure the token generation was actually triggered with the right email
            verify(jwtService).generateToken(eq("test@company.com"), eq(response));

            assertThat(result).isEqualTo("test@company.com");
        }

        @Test
        void shouldThrowException_WhenCredentialsAreInvalid() {
            // Arrange
            LoginRequest request = new LoginRequest();
            request.setEmail("test@company.com");
            request.setPassword("wrong-password");

            // Tell the mock to throw an error when this specific call happens
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Invalid username or password"));

            HttpServletResponse response = mock(HttpServletResponse.class);

            // Act & Assert
            assertThatThrownBy(() -> authService.login(request, response))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessageContaining("Invalid username or password");

            // Verify: The JWT service should NEVER be called if authentication fails
            verifyNoInteractions(jwtService);
        }
    }


    @Nested
    class logoutTest{

        @Test
        void shouldRemoveJwtCookie() {
            // Arrange
            HttpServletResponse response = mock(HttpServletResponse.class);

            // Act
            authService.logout(response);

            // Assert (Corrected Syntax)
            verify(jwtService).removeTokenFromCookie(response);
        }
    }

}