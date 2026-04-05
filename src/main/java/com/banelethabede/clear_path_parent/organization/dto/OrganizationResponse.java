// OrganizationResponse.java
package com.banelethabede.clear_path_parent.organization.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrganizationResponse {
    private UUID id;
    private String name;
    private OrganizationEnums type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}