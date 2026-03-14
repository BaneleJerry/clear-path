package com.banelethabede.clear_path.project;
import java.time.LocalDate;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.banelethabede.clear_path.organization.Organization;
import com.banelethabede.clear_path.organization.OrganizationRepository;
import com.banelethabede.clear_path.organization.dto.OrganizationEnums;
import com.banelethabede.clear_path.project.dto.ProjectRequestDTO;
import com.banelethabede.clear_path.project.dto.ProjectResponseDTO;
import com.banelethabede.clear_path.project.dto.ProjectStatusEnum;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ProjectService.class)
public class ProjectServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private Organization testOrg;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
        organizationRepository.deleteAll();

        testOrg = new Organization();
        testOrg.setName("Test Org");
        testOrg.setType(OrganizationEnums.COMPANY);
        organizationRepository.save(testOrg);
    }

    @Test
    void createProject_ShouldInitializeWithZeroProgress() {
        ProjectRequestDTO dto = new ProjectRequestDTO("New project ", "Test project", testOrg.getId(), LocalDate.now(),
                LocalDate.now().plusMonths(5));

        ProjectResponseDTO savedProject = projectService.createProject(dto);
        
        assertThat(savedProject.progressPercentage()).isEqualTo(0);
        assertThat(savedProject.status()).isEqualTo(ProjectStatusEnum.PLANNING);

    }

    @Test
    void updateProgress_shouldUpdateStatusToCompleted_WhenProgressIs100() {
        // Arrange
        Project project = new Project();
        project.setName("Work in Progress");
        project.setOrganization(testOrg);
        project.setProgressPercentage(50);
        project.setStatus(ProjectStatusEnum.IN_PROGRESS);
        project = projectRepository.save(project);

        // Act
        projectService.updateProjectProgress(project.getId(), 100);

        // Assert
        Project updated = projectRepository.findById(project.getId()).orElseThrow();
        assertThat(updated.getProgressPercentage()).isEqualTo(100);
        assertThat(updated.getStatus()).isEqualTo(ProjectStatusEnum.COMPLETED);
    }

}