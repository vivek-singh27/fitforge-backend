package com.fitforge.fitforge_backend.tracking.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class WorkoutLogResponse {
    private Long id;
    private LocalDate workoutDate;
    private String dayLabel;
    private Integer durationMinutes;
    private String notes;
    private Boolean completed;
}