package com.banelethabede.clear_path.auth;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThat;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.banelethabede.clear_path.auth.dto.RegisterRequest;
import com.banelethabede.clear_path.organization.Organization;
import com.banelethabede.clear_path.organization.OrganizationRepository;
import com.banelethabede.clear_path.organization.dto.OrganizationEnums;
import com.banelethabede.clear_path.roles.Role;
import com.banelethabede.clear_path.roles.RoleName;
import com.banelethabede.clear_path.roles.RoleRepository;
import com.banelethabede.clear_path.security.jwt.JwtService;
import com.banelethabede.clear_path.user.UserRepository;

import jakarta.persistence.EntityExistsException;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AuthService.class)
class AuthServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = 
            new PostgreSQLContainer<>("postgres:16-alpine");

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

        Role role = new Role();
        role.setName(RoleName.ROLE_USER);
        roleRepository.save(role);

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
    public class RegisterIndividualTests  {
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
            assertThatThrownBy(() ->
                    authService.registerIndividual(request)
            ).isInstanceOf(EntityExistsException.class);
        }
        
    }

    
    

   
}