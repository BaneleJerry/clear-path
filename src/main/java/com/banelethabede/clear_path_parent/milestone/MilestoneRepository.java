package com.banelethabede.clear_path_parent.milestone;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    boolean existsByProjectIdAndTitle(UUID projectID, @NotBlank(message = "Title is required") String title);

    List<Milestone> findAllByProjectId(Long projectId);
    List<Milestone> findAllByProjectIdOrderByDueDateAsc(UUID projectId);
    List<Milestone> findAllByProjectIdOrderByDueDateDesc(UUID projectId);
}
