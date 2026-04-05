package com.banelethabede.clear_path_parent.organization;

import com.banelethabede.clear_path_parent.organization.dto.AssignUserRequest;
import com.banelethabede.clear_path_parent.organization.dto.OrganizationEnums;
import com.banelethabede.clear_path_parent.organization.dto.OrganizationRequest;
import com.banelethabede.clear_path_parent.organization.dto.OrganizationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<OrganizationResponse> create(@Valid @RequestBody OrganizationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(organizationService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MODERATOR')")
    public ResponseEntity<OrganizationResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(organizationService.findById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<OrganizationResponse>> findAll() {
        return ResponseEntity.ok(organizationService.findAll());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<OrganizationResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) OrganizationEnums type
    ) {
        return ResponseEntity.ok(organizationService.search(name, type));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<OrganizationResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody OrganizationRequest request
    ) {
        return ResponseEntity.ok(organizationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        organizationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign-user")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> assignUser(@Valid @RequestBody AssignUserRequest request) {
        organizationService.assignUser(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{orgId}/users/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> removeUser(@PathVariable UUID orgId, @PathVariable UUID userId) {
        organizationService.removeUser(orgId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pending-invites")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MODERATOR')")
    public ResponseEntity<Long> pendingInvites(@PathVariable UUID id) {
        return ResponseEntity.ok(organizationService.countPendingInvites(id));
    }
}