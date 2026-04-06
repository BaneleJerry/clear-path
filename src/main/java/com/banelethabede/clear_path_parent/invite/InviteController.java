package com.banelethabede.clear_path_parent.invite;

import com.banelethabede.clear_path_parent.invite.dto.InviteRequest;
import com.banelethabede.clear_path_parent.invite.dto.InviteResponse;
import com.banelethabede.clear_path_parent.invite.dto.RedeemByCodeRequest;
import com.banelethabede.clear_path_parent.role.RoleName;
import com.banelethabede.clear_path_parent.security.model.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invites")
public class InviteController {

    private final InviteService inviteService;
    private final InviteMapper mapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    ResponseEntity<List<InviteResponse>> findAll() {
        return ResponseEntity.ok(inviteService.getAllInvites());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MODERATOR')")
    ResponseEntity<?> sendInvite(@RequestBody InviteRequest inviteRequest,
                                 @AuthenticationPrincipal UserPrincipal userDetails) {
        // get inviter id and role from userDetails
        UUID inviterId = userDetails.getCachedUser().getUserID();
        RoleName inviterRole = RoleName.valueOf(userDetails.getCachedUser().getRole());

        return ResponseEntity.ok(inviteService.sendInvite(inviterId, inviterRole, inviteRequest));
    }

    @GetMapping("/validate")
    public ResponseEntity<InviteResponse> validateByToken(@RequestParam String token) {
        InviteToken invite = inviteService.validateByToken(token);
        return ResponseEntity.ok(mapper.toInviteResponse(invite));
    }

    @PostMapping("/validate/code")
    public ResponseEntity<InviteResponse> validateByCode(@RequestBody @Valid RedeemByCodeRequest request) {
        InviteToken invite = inviteService.validateByCodeOnly(request);
        return ResponseEntity.ok(mapper.toInviteResponse(invite));
    }
}
