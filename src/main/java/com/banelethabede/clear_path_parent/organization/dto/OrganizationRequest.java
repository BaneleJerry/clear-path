// OrganizationRequest.java
package com.banelethabede.clear_path_parent.organization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrganizationRequest {

    @NotBlank
    private String name;

    @NotNull
    private OrganizationEnums type;
}