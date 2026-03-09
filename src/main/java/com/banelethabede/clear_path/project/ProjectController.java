package com.banelethabede.clear_path.project;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.banelethabede.clear_path.project.dto.ProjectRequestDTO;
import com.banelethabede.clear_path.project.dto.ProjectResponseDTO;
import com.banelethabede.clear_path.project.dto.ProjectStatusEnum;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectRequestDTO dto) {
        return new ResponseEntity<>(projectService.createProject(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @GetMapping("/organization/{orgId}")
    public ResponseEntity<List<ProjectResponseDTO>> getProjectsByOrg(@PathVariable UUID orgId) {
        return ResponseEntity.ok(projectService.getProjectsByOrganization(orgId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjectResponseDTO> updateStatus(
            @PathVariable UUID id, 
            @RequestParam ProjectStatusEnum status) {
        return ResponseEntity.ok(projectService.updateProjectStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}