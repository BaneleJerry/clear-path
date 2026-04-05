package com.banelethabede.clear_path_parent.project;

import com.banelethabede.clear_path_parent.exception.ResourceNotFoundException;
import com.banelethabede.clear_path_parent.organization.Organization;
import com.banelethabede.clear_path_parent.organization.OrganizationRepository;
import com.banelethabede.clear_path_parent.project.dto.ProjectRequestDTO;
import com.banelethabede.clear_path_parent.project.dto.ProjectResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final OrganizationRepository organizationRepository;

    /*
     * CREATE
     */
    @Transactional
    public ProjectResponseDTO createProject(ProjectRequestDTO dto) {
        Organization org = organizationRepository.findById(dto.organizationId())
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        Project project = Project.builder()
                .name(dto.name())
                .description(dto.description())
                .organization(org)
                .status(ProjectStatus.PLANNING)
                .progressPercentage(0)
                .startDate(dto.startDate())
                .deadline(dto.deadline())
                .build();

        return mapToResponseDTO(projectRepository.save(project));
    }

    /*
     * READ (Single)
     */
    public ProjectResponseDTO getProjectById(UUID projectId) {
        return projectRepository.findById(projectId)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    /*
     * READ (All)
     */
    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /*
     * READ (Projects by Organization)
     * Important for multi-tenant systems
     */
    public List<ProjectResponseDTO> getProjectsByOrganization(UUID organizationId) {
        return projectRepository.findByOrganizationId(organizationId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /*
     * UPDATE
     */
    public ProjectResponseDTO updateProject(UUID projectId, ProjectRequestDTO dto) {

        Project project = getByID(projectId);

        project.setName(dto.name());
        project.setDescription(dto.description());
        project.setStartDate(dto.startDate());
        project.setDeadline(dto.deadline());

        Project updatedProject = projectRepository.save(project);

        return mapToResponseDTO(updatedProject);
    }

    /*
     * UPDATE Project Status
     */
    public ProjectResponseDTO updateProjectStatus(UUID projectId, ProjectStatus status) {

        Project project = getByID(projectId);

        project.setStatus(status);

        Project updatedProject = projectRepository.save(project);

        return mapToResponseDTO(updatedProject);
    }

    /*
     * UPDATE Project Progress
     */
    public ProjectResponseDTO updateProjectProgress(UUID projectId, Integer progress) {

        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100");
        }

        Project project = getByID(projectId);

        project.setProgressPercentage(progress);

        Project updatedProject = projectRepository.save(project);

        return mapToResponseDTO(updatedProject);
    }

    /*
     * DELETE
     */
    @Transactional
    public void deleteProject(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project not found");
        }
        projectRepository.deleteById(projectId);
    }

    public Project getByID(UUID projectID) {
        return  projectRepository.findById(projectID)
                .orElseThrow(() -> new ResourceNotFoundException("Project with id: " + projectID + " not found"));
    }

    private ProjectResponseDTO mapToResponseDTO(Project project) {
        return new ProjectResponseDTO(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                project.getProgressPercentage(),
                project.getStartDate(),
                project.getDeadline(),
                project.getOrganization().getId(),
                project.getCreatedAt(),
                project.getUpdatedAt());
    }
}
