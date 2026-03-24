package com.banelethabede.clear_path_parent.milestone;

import com.banelethabede.clear_path_parent.common.ApiResponse;
import com.banelethabede.clear_path_parent.common.ApiResponseUtil;
import com.banelethabede.clear_path_parent.milestone.dto.MilestoneCreateRequestDTO;
import com.banelethabede.clear_path_parent.milestone.dto.MilestoneResponse;
import com.banelethabede.clear_path_parent.milestone.dto.MilestoneUpdateRequest;
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

    @GetMapping("/milestone/{id}")
    ResponseEntity<MilestoneResponse> getMilestone(@PathVariable Long id){
        return ResponseEntity.ok(milestoneService.getMilestone(id));
    }

    @PutMapping("/milestones/{id}")
    ResponseEntity<MilestoneResponse> updateMilestone(@PathVariable Long id,
                                                      @RequestBody @Valid MilestoneUpdateRequest request){
        MilestoneResponse response = milestoneService.updateMilestone(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/milestone/{id}")
    ResponseEntity<?> deleteMilestone(@PathVariable Long id){
        milestoneService.deleteMilestone(id);
        return ResponseEntity.noContent().build();
    }
}
