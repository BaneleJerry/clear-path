package com.banelethabede.clear_path.project;

import org.springframework.stereotype.Service;

import com.banelethabede.clear_path.organization.Organization;
import com.banelethabede.clear_path.organization.OrganizationRepository;
import com.banelethabede.clear_path.project.dto.ProjectDto;
import com.banelethabede.clear_path.project.dto.ProjectStatusEnum;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;

    public void createProject(ProjectDto request){

        Organization org = organizationRepository.findById(request.getOrganizationID())
        .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
        
        Project project = Project.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .projectStatusEnum(ProjectStatusEnum.PLANNING)
                    .progressPercentage(0)
                    .startDate(request.getStartDate())
                    .deadline(request.getEndDate())
                    .build();


        // Project savedProject = projectRepository.save(project);
    }
    
}
