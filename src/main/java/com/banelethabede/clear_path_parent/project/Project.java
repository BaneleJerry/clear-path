package com.banelethabede.clear_path_parent.project;

import com.banelethabede.clear_path_parent.organization.Organization;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
@Builder
@Table(name = "projects", indexes = {
        @Index(name = "idx_projects_org", columnList = "organization_id"),
        @Index(name = "idx_projects_status", columnList = "status"),
        @Index(name = "idx_projects_deadline", columnList = "deadline")
})
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status;

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
