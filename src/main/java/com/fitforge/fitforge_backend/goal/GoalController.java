package com.fitforge.fitforge_backend.goal;

import com.fitforge.fitforge_backend.goal.dto.GoalRequest;
import com.fitforge.fitforge_backend.goal.dto.GoalResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<GoalResponse> setGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody GoalRequest request) {

        return ResponseEntity.ok(
                goalService.setGoal(userDetails.getUsername(), request));
    }

    @GetMapping("/active")
    public ResponseEntity<GoalResponse> getActiveGoal(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                goalService.getActiveGoal(userDetails.getUsername()));
    }

    @GetMapping("/history")
    public ResponseEntity<List<GoalResponse>> getAllGoals(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                goalService.getAllGoals(userDetails.getUsername()));
    }
}