package com.fitforge.fitforge_backend.goal;

import com.fitforge.fitforge_backend.exception.ResourceNotFoundException;
import com.fitforge.fitforge_backend.goal.dto.GoalRequest;
import com.fitforge.fitforge_backend.goal.dto.GoalResponse;
import com.fitforge.fitforge_backend.user.User;
import com.fitforge.fitforge_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalResponse setGoal(String email, GoalRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Deactivate previous goal before creating new one
        goalRepository.deactivateAllGoalsForUser(user.getId());

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusWeeks(request.getDurationWeeks());

        Goal goal = Goal.builder()
                .user(user)
                .goalType(request.getGoalType())
                .targetWeightKg(request.getTargetWeightKg())
                .durationWeeks(request.getDurationWeeks())
                .startDate(startDate)
                .endDate(endDate)
                .isActive(true)
                .build();

        goalRepository.save(goal);
        return mapToResponse(goal);
    }

    public GoalResponse getActiveGoal(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Goal goal = goalRepository.findByUserIdAndIsActiveTrue(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active goal found. Please set a goal first."));

        return mapToResponse(goal);
    }

    public List<GoalResponse> getAllGoals(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return goalRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GoalResponse mapToResponse(Goal goal) {
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), goal.getEndDate());

        return GoalResponse.builder()
                .id(goal.getId())
                .goalType(goal.getGoalType())
                .targetWeightKg(goal.getTargetWeightKg())
                .durationWeeks(goal.getDurationWeeks())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .isActive(goal.getIsActive())
                .daysRemaining((int) Math.max(daysRemaining, 0))
                .build();
    }
}