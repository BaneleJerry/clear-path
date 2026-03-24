package com.banelethabede.clear_path_parent.milestone.dto;

import com.banelethabede.clear_path_parent.milestone.MilestoneStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MilestoneUpdateRequest {
    @NotBlank
    private String title;

    @NotNull
    private MilestoneStatus status;

    @NotBlank
    private LocalDateTime dueDate;
}
