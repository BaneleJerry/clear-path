package com.banelethabede.clear_path_parent.project.dto;

import com.banelethabede.clear_path_parent.project.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Setter
@Getter
@Data
@AllArgsConstructor
public class ProjectResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private ProjectStatus status;
    private int progressPercentage;
    private LocalDate startDate;
    private LocalDate deadline;
    private UUID organizationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
