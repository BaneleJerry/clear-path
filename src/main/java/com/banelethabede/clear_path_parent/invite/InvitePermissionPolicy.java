package com.banelethabede.clear_path_parent.invite;

import com.banelethabede.clear_path_parent.role.RoleName;
import java.util.Map;
import java.util.Set;

public final class InvitePermissionPolicy {

    private static final Map<RoleName, Set<RoleName>> ALLOWED = Map.of(
            RoleName.ROLE_ADMIN,     Set.of(RoleName.ROLE_ADMIN, RoleName.ROLE_STAFF,
                    RoleName.ROLE_MODERATOR, RoleName.ROLE_USER),
            RoleName.ROLE_STAFF,     Set.of(RoleName.ROLE_MODERATOR, RoleName.ROLE_USER),
            RoleName.ROLE_MODERATOR, Set.of(RoleName.ROLE_USER),
            RoleName.ROLE_USER,      Set.of()
    );

    private InvitePermissionPolicy() {}

    public static boolean canInvite(RoleName inviterRole, RoleName targetRole) {
        return ALLOWED.getOrDefault(inviterRole, Set.of()).contains(targetRole);
    }

    public static Set<RoleName> allowedRolesFor(RoleName inviterRole) {
        return ALLOWED.getOrDefault(inviterRole, Set.of());
    }
}