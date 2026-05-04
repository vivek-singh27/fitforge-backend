package com.fitforge.fitforge_backend.plan.workout.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class WorkoutPlanResponse {
    private Long id;
    private String planName;
    private Integer daysPerWeek;
    private String planType;
    private String notes;
    // Grouped by day: {"Push Day": [exercise1, exercise2], "Pull Day": [...]}
    private Map<String, List<ExerciseDetail>> weeklyPlan;

    @Data
    @Builder
    public static class ExerciseDetail {
        private String exerciseName;
        private String muscleGroup;
        private Integer sets;
        private String reps;
        private Integer restSeconds;
        private String notes;
    }
}