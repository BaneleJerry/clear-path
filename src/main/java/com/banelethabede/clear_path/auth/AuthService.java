package com.banelethabede.clear_path.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;


        /**
     * Registers a new individual client in the system.
     *
     * <p>This method creates a new {@link User} account for an individual client and
     * automatically provisions a dedicated {@link Organization} for that user.
     * Each individual user receives their own organization to ensure strict
     * tenant isolation and prevent data leakage between unrelated users.</p>
     *
     * <p>The user is assigned the default role {@code ROLE_USER} and linked to the
     * newly created organization of type {@code INDIVIDUAL}.</p>
     *
     * <p>The entire operation runs inside a transactional boundary. If any step
     * fails (for example during user persistence), the transaction will roll back
     * and the organization record will not be persisted.</p>
     *
     * <p>Default account state:</p>
     * <ul>
     *   <li>{@code isActive = false} – account requires activation</li>
     *   <li>{@code lastLoginAt = null} – no login yet</li>
     * </ul>
     *
     * @param request DTO containing registration information such as email,
     *                password, first name, and last name
     *
     * @throws EntityExistsException if a user with the provided email already exists
     * @throws RuntimeException if the default role {@code ROLE_USER} cannot be found
     *
     * @see User
     * @see Organization
     * @see Role
     */
    @Transactional
    public void registerIndividual(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EntityExistsException(
                    "User with email " + request.getEmail() + " already exists"
            );
        }

        // Create organization for the individual user
        Organization organization = Organization.builder()
                .name(request.getEmail())
                .type(OrganizationEnums.INDIVIDUAL)
                .build();

        organizationRepository.save(organization);

        Role role = roleRepository.findByName(RoleName.ROLE_USER)
        .orElseThrow(() -> new IllegalStateException("ROLE_USER missing"));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .isActive(false)
                .lastLoginAt(null)
                .role(role)
                .organization(organization)
                .build();

        userRepository.save(user);
    }

        /**
     * Registers a new organization and its initial moderator user.
     *
     * <p>This method provisions a new {@link Organization} and creates the first
     * associated {@link User} who will act as the organization's moderator.
     * The moderator is assigned the {@code ROLE_MODERATOR} role and becomes the
     * primary administrative user responsible for managing the organization
     * and its resources.</p>
     *
     * <p>The organization name must be unique. If an organization with the same
     * name already exists, registration will fail.</p>
     *
     * <p>The entire operation runs within a transactional boundary. If any part
     * of the process fails (e.g., database constraint violation), the transaction
     * will be rolled back and neither the organization nor the user will be
     * persisted.</p>
     *
     * <p>Default account state:</p>
     * <ul>
     *   <li>{@code isActive = false} – account requires activation</li>
     *   <li>{@code lastLoginAt = null} – no login recorded yet</li>
     * </ul>
     *
     * @param request DTO containing organization and moderator registration data
     *                such as organization name, type, user email, password,
     *                first name, and last name
     *
     * @throws EntityExistsException if a user with the provided email already exists
     * @throws EntityExistsException if an organization with the provided name already exists
     * @throws RuntimeException if the {@code ROLE_MODERATOR} role cannot be found
     *
     * @see Organization
     * @see User
     * @see Role
     */
    @Transactional
    public void registerOrgAndModeratorUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EntityExistsException(
                    "User with email " + request.getEmail() + " already exists"
            );
        }

        if (organizationRepository.existsByName(request.getOrganizationName())) {
            throw new EntityExistsException(
                    "Organization with name " + request.getOrganizationName() + " already exists"
            );
        }

        // Create organization
        Organization org = Organization.builder()
                .name(request.getOrganizationName())
                .type(request.getOrganizationType())
                .build();

        // Create moderator user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .isActive(false)
                .lastLoginAt(null)
                .organization(org)
                .build();

        Role role = roleRepository.findByName(RoleName.ROLE_USER)
        .orElseThrow(() -> new IllegalStateException("ROLE_USER missing"));
        user.setRole(role);

        organizationRepository.save(org);
        userRepository.save(user);
    }


    /**
     * Adds a new user to an existing organization.
     * 
     * <p>This method creates a new {@link User} account and associates it with an
     * existing {@link Organization}. The user is assigned the default role
     * {@code ROLE_USER} and is linked to the specified organization. The organization
     * must already exist in the system; otherwise, an exception will be thrown.</p>
     * 
     * <p>The entire operation runs within a transactional boundary. If any part of the
     * process fails (e.g., user already exists, organization not found), the transaction
     * will be rolled back and the user will not be persisted.</p>
     * 
     * @param request DTO containing user registration information such as email, password, first name,
     *               last name, and the name of the organization to which the user should be added
     * @throws EntityExistsException if a user with the provided email already exists
     * @throws EntityExistsException if the specified organization does not exist
     * @throws RuntimeException if the default role {@code ROLE_USER} cannot be found
     * 
     * @see User
     * @see Organization
     * @see Role
     */
    @Transactional
    public void addUserToOrganization(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EntityExistsException(
                    "User with email " + request.getEmail() + " already exists"
            );
        }

        Organization org = organizationRepository.findByName(request.getOrganizationName());
        if (org == null) {
            throw new EntityExistsException(
                    "Organization with name " + request.getOrganizationName() + " does not exist"
            );
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .isActive(false)
                .lastLoginAt(null)
                .organization(org)
                .build();

        Role role = roleRepository.findByName(RoleName.ROLE_USER)
        .orElseThrow(() -> new IllegalStateException("ROLE_USER missing"));
        user.setRole(role);

        userRepository.save(user);
    }
    
    
     /**
 * Authenticates a user and issues a JWT authentication token.
 *
 * <p>This method validates the provided user credentials using the
 * {@link AuthenticationManager}. If authentication succeeds, the authenticated
 * {@link Authentication} object is stored in the {@link SecurityContextHolder}
 * for the current request context.</p>
 *
 * <p>A JWT token is then generated and attached to the HTTP response as a
 * secure cookie. The token will be used by the client for subsequent
 * authenticated requests.</p>
 *
 * @param request  DTO containing the user's login credentials (email and password)
 * @param response HTTP response used to attach the generated JWT token cookie
 *
 * @return the authenticated user's username (email)
 *
 * @throws org.springframework.security.core.AuthenticationException
 *         if the provided credentials are invalid
 *
 * @see AuthenticationManager
 * @see UsernamePasswordAuthenticationToken
 * @see SecurityContextHolder
 * @see UserDetails
 */
public String login(LoginRequest request, HttpServletResponse response) {

    UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            );

    Authentication authResult = authenticationManager.authenticate(authToken);


    User userEntity = (User) authResult.getPrincipal();


    return jwtService.generateToken(userEntity);
}


// /**
//  * Logs out the currently authenticated user by removing the JWT cookie.
//  *
//  * <p>This method invalidates the authentication token stored in the client's
//  * cookie by instructing the browser to delete it. After logout, the client
//  * must authenticate again to access protected resources.</p>
//  *
//  * @param response HTTP response used to remove the authentication cookie
//  *
//  * @see JwtService
//  */
// public void logout(HttpServletResponse response) {
//     jwtService.removeTokenFromCookie(response);
// }

}
