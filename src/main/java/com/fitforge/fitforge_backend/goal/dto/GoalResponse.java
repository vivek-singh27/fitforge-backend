package com.fitforge.fitforge_backend.goal.dto;

import com.fitforge.fitforge_backend.goal.Goal.GoalType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class GoalResponse {
    private Long id;
    private GoalType goalType;
    private Double targetWeightKg;
    private Integer durationWeeks;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private Integer daysRemaining; // calculated field
}