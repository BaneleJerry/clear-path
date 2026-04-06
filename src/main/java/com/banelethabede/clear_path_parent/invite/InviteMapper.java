package com.banelethabede.clear_path_parent.invite;

import com.banelethabede.clear_path_parent.invite.dto.InviteRequest;
import com.banelethabede.clear_path_parent.invite.dto.InviteResponse;
import org.springframework.stereotype.Component;

@Component
public class InviteMapper {
    public InviteResponse toInviteResponse(InviteToken iniInviteToken) {
        return InviteResponse
                .builder()
                .token(iniInviteToken.getToken())
                .code(iniInviteToken.getCode())
                .inviteeEmail(iniInviteToken.getInviteeEmail())
                .assignedRole(iniInviteToken.getAssignedRole())
                .used(iniInviteToken.isUsed())
                .expiresAt(iniInviteToken.getExpiresAt())
                .build();
    }
}
