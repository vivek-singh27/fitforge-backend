package com.fitforge.fitforge_backend.calculator;

import com.fitforge.fitforge_backend.goal.Goal.GoalType;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public class MacroCalculator {

    // Returns calorie target adjusted for goal
    public int calculateTargetCalories(double tdee, GoalType goalType) {
        return (int) Math.round(switch (goalType) {
            case CUT      -> tdee - 500;  // caloric deficit
            case BULK     -> tdee + 300;  // caloric surplus
            case MAINTAIN -> tdee;        // maintenance
        });
    }

    public MacroResult calculateMacros(double weightKg, int targetCalories) {
        // Protein: 2g per kg of bodyweight
        int proteinG = (int) Math.round(2.0 * weightKg);
        int proteinCalories = proteinG * 4;

        // Fat: 25% of total calories (1g fat = 9 calories)
        int fatG = (int) Math.round((targetCalories * 0.25) / 9);
        int fatCalories = fatG * 9;

        // Carbs: whatever calories remain (1g carb = 4 calories)
        int carbCalories = targetCalories - proteinCalories - fatCalories;
        int carbG = (int) Math.round((double) carbCalories / 4);

        return MacroResult.builder()
                .targetCalories(targetCalories)
                .proteinGrams(proteinG)
                .carbsGrams(carbG)
                .fatGrams(fatG)
                .build();
    }

    // Inner class to hold macro results
    @Data
    @Builder
    public static class MacroResult {
        private int targetCalories;
        private int proteinGrams;
        private int carbsGrams;
        private int fatGrams;
    }
}