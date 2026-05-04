package com.fitforge.fitforge_backend.chatbot;

import com.fitforge.fitforge_backend.chatbot.dto.ChatRequest;
import com.fitforge.fitforge_backend.chatbot.dto.ChatResponse;
import com.fitforge.fitforge_backend.exception.ResourceNotFoundException;
import com.fitforge.fitforge_backend.goal.Goal;
import com.fitforge.fitforge_backend.goal.GoalRepository;
import com.fitforge.fitforge_backend.plan.diet.DietPlan;
import com.fitforge.fitforge_backend.plan.diet.DietPlanRepository;
import com.fitforge.fitforge_backend.plan.workout.WorkoutPlan;
import com.fitforge.fitforge_backend.plan.workout.WorkoutPlanRepository;
import com.fitforge.fitforge_backend.tracking.ProgressLogRepository;
import com.fitforge.fitforge_backend.user.FitnessProfile;
import com.fitforge.fitforge_backend.user.FitnessProfileRepository;
import com.fitforge.fitforge_backend.user.User;
import com.fitforge.fitforge_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final GroqService groqService;
    private final UserRepository userRepository;
    private final FitnessProfileRepository profileRepository;
    private final GoalRepository goalRepository;
    private final DietPlanRepository dietPlanRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final ProgressLogRepository progressLogRepository;

    public ChatResponse chat(String email, ChatRequest request) {
        // Fetch user data to build personalized context
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Build system prompt with user's real data
        String systemPrompt = buildSystemPrompt(user);

        // Call Groq AI
        String reply = groqService.chat(systemPrompt, request.getMessage());

        return ChatResponse.builder()
                .reply(reply)
                .timestamp(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("HH:mm")))
                .build();
    }

    // This is the KEY method — injects real user data into AI context
    private String buildSystemPrompt(User user) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are FitBot, an expert AI fitness assistant inside the FitForge app. ");
        prompt.append("You are talking to ").append(user.getUsername()).append(". ");
        prompt.append("Always be encouraging, concise, and personalized. ");
        prompt.append("Never give generic advice — always refer to their specific data below.\n\n");

        // Add fitness profile
        Optional<FitnessProfile> profileOpt =
                profileRepository.findByUserId(user.getId());

        if (profileOpt.isPresent()) {
            FitnessProfile p = profileOpt.get();
            double heightM = p.getHeightCm() / 100.0;
            double bmi     = Math.round((p.getWeightKg() / (heightM * heightM)) * 10.0) / 10.0;

            prompt.append("=== USER PROFILE ===\n");
            prompt.append("Name: ").append(user.getUsername()).append("\n");
            prompt.append("Age: ").append(p.getAge()).append(" years\n");
            prompt.append("Gender: ").append(p.getGender()).append("\n");
            prompt.append("Height: ").append(p.getHeightCm()).append(" cm\n");
            prompt.append("Weight: ").append(p.getWeightKg()).append(" kg\n");
            prompt.append("BMI: ").append(bmi).append("\n");
            prompt.append("Activity Level: ").append(p.getActivityLevel()).append("\n\n");
        } else {
            prompt.append("Note: User has not set up their fitness profile yet.\n\n");
        }

        // Add active goal
        goalRepository.findByUserIdAndIsActiveTrue(user.getId())
                .ifPresent(goal -> {
                    prompt.append("=== FITNESS GOAL ===\n");
                    prompt.append("Goal Type: ").append(goal.getGoalType()).append("\n");
                    prompt.append("Duration: ").append(goal.getDurationWeeks())
                            .append(" weeks\n");
                    prompt.append("Days Remaining: ").append(goal.getDaysRemaining())
                            .append(" days\n");
                    if (goal.getTargetWeightKg() != null) {
                        prompt.append("Target Weight: ")
                                .append(goal.getTargetWeightKg()).append(" kg\n");
                    }
                    prompt.append("\n");

                    // Add diet plan
                    dietPlanRepository.findByGoalId(goal.getId())
                            .ifPresent(diet -> {
                                prompt.append("=== DIET PLAN ===\n");
                                prompt.append("Daily Calories: ")
                                        .append(diet.getDailyCalories()).append(" kcal\n");
                                prompt.append("Protein: ")
                                        .append(diet.getProteinGrams()).append("g\n");
                                prompt.append("Carbs: ")
                                        .append(diet.getCarbsGrams()).append("g\n");
                                prompt.append("Fat: ")
                                        .append(diet.getFatGrams()).append("g\n");
                                prompt.append("Meals per day: ")
                                        .append(diet.getMealFrequency()).append("\n\n");
                            });

                    // Add workout plan
                    workoutPlanRepository.findByGoalId(goal.getId())
                            .ifPresent(workout -> {
                                prompt.append("=== WORKOUT PLAN ===\n");
                                prompt.append("Plan: ")
                                        .append(workout.getPlanName()).append("\n");
                                prompt.append("Days per week: ")
                                        .append(workout.getDaysPerWeek()).append("\n");
                                prompt.append("Type: ")
                                        .append(workout.getPlanType()).append("\n\n");
                            });
                });

        // Add latest progress
        progressLogRepository
                .findTopByUserIdOrderByLogDateDesc(user.getId())
                .ifPresent(log -> {
                    prompt.append("=== LATEST PROGRESS ===\n");
                    prompt.append("Last logged weight: ")
                            .append(log.getWeightKg()).append(" kg\n");
                    prompt.append("Date: ").append(log.getLogDate()).append("\n\n");
                });

        prompt.append("=== INSTRUCTIONS ===\n");
        prompt.append("Answer the user's fitness question using their data above. ");
        prompt.append("Keep responses under 150 words. ");
        prompt.append("Be friendly, motivating, and specific to their numbers. ");
        prompt.append("If they ask something unrelated to fitness, politely redirect them.");

        return prompt.toString();
    }
}