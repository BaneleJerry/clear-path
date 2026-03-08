package com.banelethabede.clear_path.project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.banelethabede.clear_path.organization.Organization;
import com.banelethabede.clear_path.project.dto.ProjectStatusEnum;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects", indexes = {
    @Index(name = "idx_projects_org", columnList = "organization_id"),
    @Index(name = "idx_projects_status", columnList = "status"),
    @Index(name = "idx_projects_deadline", columnList = "deadline")
})
@EntityListeners(AuditingEntityListener.class)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatusEnum projectStatusEnum;

    @Builder.Default
    @Column(name = "progress_percentage", nullable = false)
    private int progressPercentage = 0;

    private LocalDate startDate;
    
    private LocalDate deadline;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}