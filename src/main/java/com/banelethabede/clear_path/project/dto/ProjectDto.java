package com.banelethabede.clear_path.project.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectDto {

    @NotBlank
    String name;

    String description;

    @NotNull
    UUID organizationID;

    LocalDate startDate;

    LocalDate endDate;

}
