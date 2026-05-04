package com.fitforge.fitforge_backend.plan.diet;

import com.fitforge.fitforge_backend.calculator.BmrCalculator;
import com.fitforge.fitforge_backend.calculator.MacroCalculator;
import com.fitforge.fitforge_backend.calculator.MacroCalculator.MacroResult;
import com.fitforge.fitforge_backend.calculator.TdeeCalculator;
import com.fitforge.fitforge_backend.exception.ResourceNotFoundException;
import com.fitforge.fitforge_backend.goal.Goal;
import com.fitforge.fitforge_backend.goal.GoalRepository;
import com.fitforge.fitforge_backend.plan.diet.dto.DietPlanResponse;
import com.fitforge.fitforge_backend.plan.diet.dto.DietPlanResponse.MealSuggestion;
import com.fitforge.fitforge_backend.user.FitnessProfile;
import com.fitforge.fitforge_backend.user.FitnessProfileRepository;
import com.fitforge.fitforge_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DietPlanService {

    private final DietPlanRepository dietPlanRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final FitnessProfileRepository profileRepository;
    private final BmrCalculator bmrCalculator;
    private final TdeeCalculator tdeeCalculator;
    private final MacroCalculator macroCalculator;

    public DietPlanResponse generateOrGetDietPlan(String email) {
        // Step 1: Get user
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Step 2: Get their fitness profile (need weight, height, age, gender)
        FitnessProfile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Please complete your fitness profile first"));

        // Step 3: Get their active goal (need goalType)
        Goal goal = goalRepository.findByUserIdAndIsActiveTrue(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Please set a fitness goal first"));

        // Step 4: Return existing plan if already generated
        return dietPlanRepository.findByGoalId(goal.getId())
                .map(this::mapToResponse)
                .orElseGet(() -> generateNewPlan(profile, goal));
    }

    private DietPlanResponse generateNewPlan(FitnessProfile profile, Goal goal) {
        // Chain of calculations
        double bmr = bmrCalculator.calculate(
                profile.getWeightKg(),
                profile.getHeightCm(),
                profile.getAge(),
                profile.getGender()
        );

        double tdee = tdeeCalculator.calculate(bmr, profile.getActivityLevel());

        int targetCalories = macroCalculator.calculateTargetCalories(tdee, goal.getGoalType());

        MacroResult macros = macroCalculator.calculateMacros(
                profile.getWeightKg(), targetCalories);

        // Meal frequency based on goal
        int mealFrequency = goal.getGoalType() == Goal.GoalType.BULK ? 5 : 3;

        String notes = generateDietNotes(goal.getGoalType());

        DietPlan plan = DietPlan.builder()
                .goal(goal)
                .dailyCalories(macros.getTargetCalories())
                .proteinGrams(macros.getProteinGrams())
                .carbsGrams(macros.getCarbsGrams())
                .fatGrams(macros.getFatGrams())
                .mealFrequency(mealFrequency)
                .notes(notes)
                .build();

        dietPlanRepository.save(plan);
        return mapToResponse(plan);
    }

    private DietPlanResponse mapToResponse(DietPlan plan) {
        int calories = plan.getDailyCalories();

        // Calculate macro percentages for UI pie chart
        double proteinPercent = Math.round((plan.getProteinGrams() * 4.0 / calories) * 100);
        double carbsPercent   = Math.round((plan.getCarbsGrams()   * 4.0 / calories) * 100);
        double fatPercent     = Math.round((plan.getFatGrams()      * 9.0 / calories) * 100);

        return DietPlanResponse.builder()
                .id(plan.getId())
                .dailyCalories(calories)
                .proteinGrams(plan.getProteinGrams())
                .carbsGrams(plan.getCarbsGrams())
                .fatGrams(plan.getFatGrams())
                .mealFrequency(plan.getMealFrequency())
                .notes(plan.getNotes())
                .proteinPercent(proteinPercent)
                .carbsPercent(carbsPercent)
                .fatPercent(fatPercent)
                .mealSuggestions(generateMealSuggestions(plan))
                .build();
    }

    // Rule-based AI: splits daily calories across meals intelligently
    private List<MealSuggestion> generateMealSuggestions(DietPlan plan) {
        List<MealSuggestion> meals = new ArrayList<>();
        int calories = plan.getDailyCalories();
        int freq = plan.getMealFrequency();

        if (freq == 3) {
            meals.add(MealSuggestion.builder()
                    .mealName("Breakfast")
                    .calories((int)(calories * 0.30))
                    .suggestion("Oats with banana + 4 egg whites + black coffee")
                    .build());
            meals.add(MealSuggestion.builder()
                    .mealName("Lunch")
                    .calories((int)(calories * 0.40))
                    .suggestion("Rice + grilled chicken/paneer + vegetables + dal")
                    .build());
            meals.add(MealSuggestion.builder()
                    .mealName("Dinner")
                    .calories((int)(calories * 0.30))
                    .suggestion("Roti + sabzi + curd + salad")
                    .build());
        } else { // 5 meals for bulk
            meals.add(MealSuggestion.builder()
                    .mealName("Breakfast")
                    .calories((int)(calories * 0.25))
                    .suggestion("Oats + eggs + milk + banana")
                    .build());
            meals.add(MealSuggestion.builder()
                    .mealName("Mid Morning Snack")
                    .calories((int)(calories * 0.15))
                    .suggestion("Peanut butter toast + protein shake")
                    .build());
            meals.add(MealSuggestion.builder()
                    .mealName("Lunch")
                    .calories((int)(calories * 0.30))
                    .suggestion("Rice + chicken/paneer + dal + vegetables")
                    .build());
            meals.add(MealSuggestion.builder()
                    .mealName("Pre Workout Snack")
                    .calories((int)(calories * 0.10))
                    .suggestion("Banana + black coffee or green tea")
                    .build());
            meals.add(MealSuggestion.builder()
                    .mealName("Dinner")
                    .calories((int)(calories * 0.20))
                    .suggestion("Roti + sabzi + curd + boiled eggs")
                    .build());
        }
        return meals;
    }

    private String generateDietNotes(Goal.GoalType goalType) {
        return switch (goalType) {
            case CUT -> "Stay in a 500 calorie deficit. Prioritize protein to preserve muscle. " +
                    "Drink 3-4 litres of water daily. Avoid sugar and processed foods.";
            case BULK -> "Eat in a 300 calorie surplus. Focus on clean bulk — avoid junk food. " +
                    "Protein is key for muscle growth. Don't skip meals.";
            case MAINTAIN -> "Match your TDEE daily. Keep macros consistent. " +
                    "Adjust calories if weight changes over 2 weeks.";
        };
    }
}