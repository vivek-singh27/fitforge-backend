package com.fitforge.fitforge_backend.tracking.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CalorieSummaryResponse {
    private Integer totalCaloriesToday;
    private Double totalProteinToday;
    private Double totalCarbsToday;
    private Double totalFatToday;
    private Integer targetCalories;        // from diet plan
    private Integer remainingCalories;
    private List<MealEntry> meals;

    @Data
    @Builder
    public static class MealEntry {
        private Long id;
        private String mealName;
        private Integer calories;
        private Double proteinGrams;
        private Double carbsGrams;
        private Double fatGrams;
    }
}