package com.banelethabede.clear_path.project;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banelethabede.clear_path.project.dto.ProjectStatusEnum;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findAllByOrganizationId(UUID organizationId);

    List<Project> findAllByProjectStatusEnum(ProjectStatusEnum status);
    
}
