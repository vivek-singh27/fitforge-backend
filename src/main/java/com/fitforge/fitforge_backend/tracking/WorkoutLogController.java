package com.fitforge.fitforge_backend.tracking;

import com.fitforge.fitforge_backend.tracking.dto.WorkoutLogRequest;
import com.fitforge.fitforge_backend.tracking.dto.WorkoutLogResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workouts/log")
@RequiredArgsConstructor
public class WorkoutLogController {

    private final WorkoutLogService workoutLogService;

    @PostMapping
    public ResponseEntity<WorkoutLogResponse> logWorkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody WorkoutLogRequest request) {
        return ResponseEntity.ok(
                workoutLogService.logWorkout(userDetails.getUsername(), request));
    }

    @GetMapping
    public ResponseEntity<List<WorkoutLogResponse>> getAllLogs(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                workoutLogService.getAllLogs(userDetails.getUsername()));
    }

    @GetMapping("/weekly-count")
    public ResponseEntity<Map<String, Integer>> getWeeklyCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        int count = workoutLogService.getWeeklyCount(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("weeklyCount", count));
    }
}