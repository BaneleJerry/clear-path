package com.banelethabede.clear_path_parent.milestone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MilestoneResponse {
    private Long id;
    private String title;
    private String status;
    private LocalDateTime dueDate;
}
