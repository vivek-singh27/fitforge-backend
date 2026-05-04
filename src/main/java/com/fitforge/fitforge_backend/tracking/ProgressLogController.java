package com.fitforge.fitforge_backend.tracking;

import com.fitforge.fitforge_backend.tracking.dto.ProgressLogRequest;
import com.fitforge.fitforge_backend.tracking.dto.ProgressLogResponse;
import com.fitforge.fitforge_backend.tracking.dto.ProgressSummaryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressLogController {

    private final ProgressLogService progressLogService;

    // Log today's weight
    @PostMapping
    public ResponseEntity<ProgressLogResponse> logProgress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ProgressLogRequest request) {
        return ResponseEntity.ok(
                progressLogService.logProgress(userDetails.getUsername(), request));
    }

    // Get summary + chart data
    @GetMapping("/summary")
    public ResponseEntity<ProgressSummaryResponse> getSummary(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                progressLogService.getSummary(userDetails.getUsername()));
    }

    // Get all logs
    @GetMapping
    public ResponseEntity<List<ProgressLogResponse>> getAllLogs(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                progressLogService.getAllLogs(userDetails.getUsername()));
    }
}