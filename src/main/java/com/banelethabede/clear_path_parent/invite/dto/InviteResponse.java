package com.banelethabede.clear_path_parent.invite.dto;

import com.banelethabede.clear_path_parent.role.RoleName;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data @Builder
public class InviteResponse {
    private String token;       // full UUID — embedded in the email link
    private String code;        // short code — shown in the email body + UI
    private String inviteeEmail;
    private RoleName assignedRole;
    private boolean  used;
    private Instant expiresAt;
}