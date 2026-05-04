package com.fitforge.fitforge_backend.tracking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class WorkoutLogRequest {

    @NotBlank(message = "Day label is required")
    private String dayLabel;

    private Integer durationMinutes;

    private String notes;

    private LocalDate workoutDate;

    private Boolean completed = true;
}