package com.fitforge.fitforge_backend.tracking;

import com.fitforge.fitforge_backend.exception.ResourceNotFoundException;
import com.fitforge.fitforge_backend.goal.GoalRepository;
import com.fitforge.fitforge_backend.plan.diet.DietPlanRepository;
import com.fitforge.fitforge_backend.tracking.dto.CalorieLogRequest;
import com.fitforge.fitforge_backend.tracking.dto.CalorieSummaryResponse;
import com.fitforge.fitforge_backend.tracking.dto.CalorieSummaryResponse.MealEntry;
import com.fitforge.fitforge_backend.user.User;
import com.fitforge.fitforge_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalorieLogService {

    private final CalorieLogRepository calorieLogRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final DietPlanRepository dietPlanRepository;

    public CalorieSummaryResponse logMeal(String email, CalorieLogRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDate date = request.getLogDate() != null
                ? request.getLogDate() : LocalDate.now();

        CalorieLog log = CalorieLog.builder()
                .user(user)
                .logDate(date)
                .mealName(request.getMealName())
                .calories(request.getCalories())
                .proteinGrams(request.getProteinGrams())
                .carbsGrams(request.getCarbsGrams())
                .fatGrams(request.getFatGrams())
                .build();

        calorieLogRepository.save(log);

        // Return updated summary after logging
        return getTodaySummary(email);
    }

    public CalorieSummaryResponse getTodaySummary(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDate today = LocalDate.now();
        List<CalorieLog> todayLogs = calorieLogRepository
                .findByUserIdAndLogDateOrderByCreatedAtAsc(user.getId(), today);

        // Sum all macros
        int totalCal      = todayLogs.stream().mapToInt(CalorieLog::getCalories).sum();
        double totalProt  = todayLogs.stream()
                .mapToDouble(l -> l.getProteinGrams() != null ? l.getProteinGrams() : 0).sum();
        double totalCarbs = todayLogs.stream()
                .mapToDouble(l -> l.getCarbsGrams()   != null ? l.getCarbsGrams()   : 0).sum();
        double totalFat   = todayLogs.stream()
                .mapToDouble(l -> l.getFatGrams()      != null ? l.getFatGrams()      : 0).sum();

        // Get target calories from diet plan if exists
        Integer targetCalories = goalRepository
                .findByUserIdAndIsActiveTrue(user.getId())
                .flatMap(g -> dietPlanRepository.findByGoalId(g.getId()))
                .map(d -> d.getDailyCalories())
                .orElse(2000); // fallback

        List<MealEntry> meals = todayLogs.stream()
                .map(l -> MealEntry.builder()
                        .id(l.getId())
                        .mealName(l.getMealName())
                        .calories(l.getCalories())
                        .proteinGrams(l.getProteinGrams())
                        .carbsGrams(l.getCarbsGrams())
                        .fatGrams(l.getFatGrams())
                        .build())
                .collect(Collectors.toList());

        return CalorieSummaryResponse.builder()
                .totalCaloriesToday(totalCal)
                .totalProteinToday(Math.round(totalProt * 10.0) / 10.0)
                .totalCarbsToday(Math.round(totalCarbs * 10.0) / 10.0)
                .totalFatToday(Math.round(totalFat * 10.0) / 10.0)
                .targetCalories(targetCalories)
                .remainingCalories(targetCalories - totalCal)
                .meals(meals)
                .build();
    }

    public void deleteMeal(String email, Long mealId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CalorieLog log = calorieLogRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found"));

        // Security check — users can only delete their own logs
        if (!log.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        calorieLogRepository.delete(log);
    }
}