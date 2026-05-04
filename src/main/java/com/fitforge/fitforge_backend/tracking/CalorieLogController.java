package com.fitforge.fitforge_backend.tracking;

import com.fitforge.fitforge_backend.tracking.dto.CalorieLogRequest;
import com.fitforge.fitforge_backend.tracking.dto.CalorieSummaryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calories")
@RequiredArgsConstructor
public class CalorieLogController {

    private final CalorieLogService calorieLogService;

    @PostMapping("/log")
    public ResponseEntity<CalorieSummaryResponse> logMeal(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CalorieLogRequest request) {
        return ResponseEntity.ok(
                calorieLogService.logMeal(userDetails.getUsername(), request));
    }

    @GetMapping("/today")
    public ResponseEntity<CalorieSummaryResponse> getTodaySummary(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                calorieLogService.getTodaySummary(userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        calorieLogService.deleteMeal(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}