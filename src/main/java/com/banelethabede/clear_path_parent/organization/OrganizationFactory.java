package com.banelethabede.clear_path_parent.organization;

import com.banelethabede.clear_path_parent.organization.dto.OrganizationEnums;
import org.springframework.stereotype.Component;

@Component
public class OrganizationFactory {
    public Organization createBaseOrganization(String email) {
        return Organization.builder()
                .name(email)
                .type(OrganizationEnums.INDIVIDUAL)
                .build();
    }

}
