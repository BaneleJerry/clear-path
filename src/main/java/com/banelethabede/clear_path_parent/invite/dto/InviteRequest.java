package com.banelethabede.clear_path_parent.invite.dto;

import com.banelethabede.clear_path_parent.role.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class InviteRequest {

    @Email @NotNull
    private String email;

    @NotNull
    private RoleName assignedRole;

    /** Required when inviter is MODERATOR */
    private UUID organisationId;
}