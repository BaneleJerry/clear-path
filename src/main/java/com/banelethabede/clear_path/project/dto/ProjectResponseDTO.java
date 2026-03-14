package com.banelethabede.clear_path.project.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectResponseDTO(
    UUID id,
    String name,
    String description,
    ProjectStatusEnum status,
    int progressPercentage,
    LocalDate startDate,
    LocalDate deadline,
    UUID organizationId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}