package com.fitforge.fitforge_backend.plan;

import com.fitforge.fitforge_backend.plan.diet.DietPlanService;
import com.fitforge.fitforge_backend.plan.diet.dto.DietPlanResponse;
import com.fitforge.fitforge_backend.plan.workout.WorkoutPlanService;
import com.fitforge.fitforge_backend.plan.workout.dto.WorkoutPlanResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final DietPlanService dietPlanService;
    private final WorkoutPlanService workoutPlanService;

    @GetMapping("/diet")
    public ResponseEntity<DietPlanResponse> getDietPlan(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                dietPlanService.generateOrGetDietPlan(userDetails.getUsername()));
    }

    @GetMapping("/workout")
    public ResponseEntity<WorkoutPlanResponse> getWorkoutPlan(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                workoutPlanService.generateOrGetWorkoutPlan(userDetails.getUsername()));
    }
}