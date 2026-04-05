// AssignUserRequest.java
package com.banelethabede.clear_path_parent.organization.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class AssignUserRequest {

    @NotNull
    private UUID organizationId;

    @NotNull
    private UUID userId;
}