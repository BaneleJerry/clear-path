package com.banelethabede.clear_path_parent.organization;

import com.banelethabede.clear_path_parent.invite.InviteTokenRepository;
import com.banelethabede.clear_path_parent.organization.dto.AssignUserRequest;
import com.banelethabede.clear_path_parent.organization.dto.OrganizationEnums;
import com.banelethabede.clear_path_parent.organization.dto.OrganizationRequest;
import com.banelethabede.clear_path_parent.organization.dto.OrganizationResponse;
import com.banelethabede.clear_path_parent.user.User;
import com.banelethabede.clear_path_parent.user.UserRepository;
import com.banelethabede.clear_path_parent.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserService userService;
    private final InviteTokenRepository  inviteTokenRepository;
    private final OrganizationMapper     mapper;

    // -------------------------------------------------------------------------
    // Create
    // -------------------------------------------------------------------------

    @Transactional
    public OrganizationResponse create(OrganizationRequest request) {
        if (organizationRepository.existsByNameIgnoreCase(request.getName())) {
            throw new OrganizationAlreadyExistsException(
                    "An organisation named '" + request.getName() + "' already exists"
            );
        }

        Organization org = Organization.builder()
                .name(request.getName().strip())
                .type(request.getType())
                .build();

        organizationRepository.save(org);
        log.info("Organisation created: id={}, name={}, type={}", org.getId(), org.getName(), org.getType());

        return mapper.toResponse(org);
    }

    // -------------------------------------------------------------------------
    // Read
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public OrganizationResponse findById(UUID id) {
        Organization org = getOrThrow(id);
        int memberCount = userService.countByOrganisationId(id);
        return mapper.toResponse(org);
    }

    @Transactional(readOnly = true)
    public Organization findEntityById(UUID id){
        return getOrThrow(id);
    }


    @Transactional(readOnly = true)
    public List<OrganizationResponse> findAll() {
        return organizationRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    // -------------------------------------------------------------------------
    // Search / filter
    // -------------------------------------------------------------------------

    /**
     * Both params are optional — pass null to skip that filter.
     * <p>
     * Examples:
     *   search(null, null)           → all orgs
     *   search("clear", null)        → name contains "clear"
     *   search(null, COMPANY)        → all companies
     *   search("clear", COMPANY)     → name contains "clear" AND type = COMPANY
     */
    @Transactional(readOnly = true)
    public List<OrganizationResponse> search(String name, OrganizationEnums type) {
        return organizationRepository.search(name, type)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    @Transactional
    public OrganizationResponse update(UUID id, OrganizationRequest request) {
        Organization org = getOrThrow(id);

        boolean nameConflict = organizationRepository
                .findByNameIgnoreCase(request.getName())
                .filter(found -> !found.getId().equals(id))
                .isPresent();

        if (nameConflict) {
            throw new OrganizationAlreadyExistsException(
                    "Another organisation is already named '" + request.getName() + "'"
            );
        }

        org.setName(request.getName().strip());
        org.setType(request.getType());

        organizationRepository.save(org);
        log.info("Organisation updated: id={}", id);

        int memberCount = userService.countByOrganisationId(id);
        return mapper.toResponse(org);
    }

    // -------------------------------------------------------------------------
    // Delete
    // -------------------------------------------------------------------------

    @Transactional
    public void delete(UUID id) {
        Organization org = getOrThrow(id);

        int memberCount = userService.countByOrganisationId(id);
        if (memberCount > 0) {
            throw new OrganizationNotEmptyException(
                    "Cannot delete organisation with " + memberCount + " active member(s). " +
                            "Reassign or remove them first."
            );
        }

        organizationRepository.delete(org);
        log.info("Organisation deleted: id={}", id);
    }

    // -------------------------------------------------------------------------
    // Assign user to organisation
    // -------------------------------------------------------------------------

    @Transactional
    public void assignUser(AssignUserRequest request) {
        getOrThrow(request.getOrganizationId()); // verify org exists

        User user = userService.findUserById(request.getUserId());


        if (request.getOrganizationId().equals(user.getOrganization().getId())) {
            throw new IllegalStateException("User is already a member of this organisation");
        }

        Organization org = this.getOrThrow(request.getOrganizationId());

        user.setOrganization(org);
        userService.updateOrganization(user);

        log.info("User {} assigned to organisation {}", request.getUserId(), request.getOrganizationId());
    }

    // -------------------------------------------------------------------------
    // Remove user from organisation
    // -------------------------------------------------------------------------

    @Transactional
    public void removeUser(UUID organisationId, UUID userId) {
        getOrThrow(organisationId);

        User user = userService.findUserById(userId);

        if (!organisationId.equals(user.getOrganization().getId())) {
            throw new IllegalStateException("User is not a member of this organisation");
        }

        Organization org =  new OrganizationFactory().createBaseOrganization(user.getEmail());

        user.setOrganization(org);
        userService.updateOrganization(user);

        log.info("User {} removed from organisation {}", userId, organisationId);
    }

    // -------------------------------------------------------------------------
    // Invite integration — list pending invites scoped to this org
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public long countPendingInvites(UUID organisationId) {
        getOrThrow(organisationId);
        return inviteTokenRepository.countByOrganizationIdAndUsedFalse(organisationId);
    }

    // -------------------------------------------------------------------------
    // Internal helper
    // -------------------------------------------------------------------------

    private Organization getOrThrow(UUID id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new OrganizationNotFoundException("Organisation not found: " + id));
    }
}