package com.banelethabede.clear_path_parent.milestone;

import com.banelethabede.clear_path_parent.common.ApiResponse;
import com.banelethabede.clear_path_parent.common.ApiResponseUtil;
import com.banelethabede.clear_path_parent.milestone.dto.MilestoneCreateRequestDTO;
import com.banelethabede.clear_path_parent.milestone.dto.MilestoneResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MilestoneController {

    MilestoneService milestoneService;

    @PostMapping("/projects/{id}/milestones")
    ResponseEntity<MilestoneResponse> create (@PathVariable UUID id,
                                              @RequestBody @Valid MilestoneCreateRequestDTO milestoneCreateRequestDTO
                                        ){
        return ResponseEntity.ok(milestoneService.createMilestone(milestoneCreateRequestDTO,id));
    }

    @GetMapping("/projects/{id}/milestones")
    ResponseEntity<List<MilestoneResponse>> getMilestones(@PathVariable UUID id){
        return ResponseEntity.ok(
                milestoneService.getProjectMilestones(id));
    }
}
