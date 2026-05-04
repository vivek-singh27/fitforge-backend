package com.fitforge.fitforge_backend.goal.dto;

import com.fitforge.fitforge_backend.goal.Goal.GoalType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class GoalRequest {

    @NotNull
    private GoalType goalType;

    // Optional — user may not know exact target weight
    private Double targetWeightKg;

    @NotNull @Min(4) @Max(52)
    private Integer durationWeeks;
}