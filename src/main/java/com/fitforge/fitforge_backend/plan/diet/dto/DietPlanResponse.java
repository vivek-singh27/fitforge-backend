package com.fitforge.fitforge_backend.plan.diet.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DietPlanResponse {
    private Long id;
    private Integer dailyCalories;
    private Integer proteinGrams;
    private Integer carbsGrams;
    private Integer fatGrams;
    private Integer mealFrequency;
    private String notes;
    private List<MealSuggestion> mealSuggestions; // rule-based AI suggestions
    private Double proteinPercent;
    private Double carbsPercent;
    private Double fatPercent;

    @Data
    @Builder
    public static class MealSuggestion {
        private String mealName;      // "Breakfast", "Lunch" etc.
        private Integer calories;     // calories for this meal
        private String suggestion;    // food suggestion text
    }
}