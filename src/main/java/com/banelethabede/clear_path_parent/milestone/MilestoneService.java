package com.banelethabede.clear_path_parent.milestone;

import com.banelethabede.clear_path_parent.exception.BadRequestException;
import com.banelethabede.clear_path_parent.exception.ResourceNotFoundException;
import com.banelethabede.clear_path_parent.milestone.dto.MilestoneCreateRequestDTO;
import com.banelethabede.clear_path_parent.milestone.dto.MilestoneResponse;
import com.banelethabede.clear_path_parent.milestone.dto.MilestoneUpdateRequest;
import com.banelethabede.clear_path_parent.project.Project;
import com.banelethabede.clear_path_parent.project.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MilestoneService {

    ProjectService projectService;
    MilestoneRepository milestoneRepository;

    @Transactional
    public MilestoneResponse createMilestone(MilestoneCreateRequestDTO requestDTO, UUID projectID) {

        Project project = projectService.getByID(projectID);

        if (requestDTO.getDueDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Due date Must be in the future");
        }

        boolean exists  = milestoneRepository.existsByProjectIdAndTitle(projectID, requestDTO.getTitle());
        if (exists) {
            throw new ResourceNotFoundException("Milestone with title " + requestDTO.getTitle() + " already exists");
        }

        Milestone milestone = Milestone.builder()
                .project(project)
                .title(requestDTO.getTitle())
                .status(MilestoneStatus.PENDING)
                .dueDate(requestDTO.getDueDate())
                .build();

        milestoneRepository.save(milestone);

        return toResponse(milestone);
    }

    public List<MilestoneResponse> getProjectMilestones(UUID projectID) {
        // Validate project existence; throws if not found
        Project project = projectService.getByID(projectID);

        return milestoneRepository
                .findAllByProjectIdOrderByDueDateAsc(project.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MilestoneResponse getMilestone(Long id) {
        return toResponse(getMilestoneById(id));
    }

    @Transactional
    public MilestoneResponse updateMilestone(Long milestoneId, MilestoneUpdateRequest request){
        if (request == null) {
            throw new BadRequestException("Milestone update request is null");
        }

        Milestone milestone = getMilestoneById(milestoneId);

        if (milestone.getStatus() == MilestoneStatus.COMPLETED &&
                request.getStatus() != MilestoneStatus.COMPLETED) {
            throw new BadRequestException("Cannot change status of a completed milestone");
        }

        milestone.setTitle(request.getTitle());
        milestone.setDueDate(request.getDueDate());
        milestone.setStatus(request.getStatus());
        milestone.setUpdatedAt(LocalDateTime.now());

        return toResponse(milestoneRepository.save(milestone));
    }

    @Transactional
    public void deleteMilestone(Long milestoneId) {
        Milestone milestone = getMilestoneById(milestoneId);
        milestoneRepository.delete(milestone);
    }

    public Milestone getMilestoneById(Long id) {
        if (id == null) {
            throw new BadRequestException("Milestone id is null");
        }

        return milestoneRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Milestone with id " + id + " not found"));
    }


    public MilestoneResponse toResponse(Milestone milestone) {
        return MilestoneResponse.builder()
                .id(milestone.getId())
                .title(milestone.getTitle())
                .status(milestone.getStatus().name())
                .dueDate(milestone.getDueDate())
                .build();
    }
}
