package com.banelethabede.clear_path.project.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectRequestDTO(
    @NotBlank String name,
    String description,
    @NotNull UUID organizationId,
    LocalDate startDate,
    LocalDate deadline
) {}