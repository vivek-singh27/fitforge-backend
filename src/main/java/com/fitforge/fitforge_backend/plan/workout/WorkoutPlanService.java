package com.fitforge.fitforge_backend.plan.workout;

import com.fitforge.fitforge_backend.exception.ResourceNotFoundException;
import com.fitforge.fitforge_backend.goal.Goal;
import com.fitforge.fitforge_backend.goal.GoalRepository;
import com.fitforge.fitforge_backend.plan.workout.WorkoutPlan.PlanType;
import com.fitforge.fitforge_backend.plan.workout.dto.WorkoutPlanResponse;
import com.fitforge.fitforge_backend.plan.workout.dto.WorkoutPlanResponse.ExerciseDetail;
import com.fitforge.fitforge_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public WorkoutPlanResponse generateOrGetWorkoutPlan(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Goal goal = goalRepository.findByUserIdAndIsActiveTrue(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Please set a fitness goal first"));

        // Return existing if already generated
        return workoutPlanRepository.findByGoalId(goal.getId())
                .map(this::mapToResponse)
                .orElseGet(() -> generateNewPlan(goal));
    }

    private WorkoutPlanResponse generateNewPlan(Goal goal) {
        // Choose plan type based on goal
        PlanType planType = switch (goal.getGoalType()) {
            case CUT      -> PlanType.PPL;        // high frequency, more volume
            case BULK     -> PlanType.PPL;        // progressive overload focus
            case MAINTAIN -> PlanType.FULL_BODY;  // balanced 3 days
        };

        String planName = switch (goal.getGoalType()) {
            case CUT      -> "Fat Loss PPL Program";
            case BULK     -> "Muscle Building PPL Program";
            case MAINTAIN -> "Maintenance Full Body Program";
        };

        int daysPerWeek = goal.getGoalType() == Goal.GoalType.MAINTAIN ? 3 : 6;

        WorkoutPlan plan = WorkoutPlan.builder()
                .goal(goal)
                .planName(planName)
                .daysPerWeek(daysPerWeek)
                .planType(planType)
                .build();

        // Build exercise list based on goal
        List<WorkoutExercise> exercises = buildExercises(plan, goal.getGoalType());
        plan.setExercises(exercises);

        workoutPlanRepository.save(plan);
        return mapToResponse(plan);
    }

    // Rule-based exercise selection engine
    private List<WorkoutExercise> buildExercises(WorkoutPlan plan, Goal.GoalType goalType) {
        List<WorkoutExercise> exercises = new ArrayList<>();

        if (goalType == Goal.GoalType.MAINTAIN) {
            // Full Body — 3 days/week
            addExercise(exercises, plan, "Day 1 - Full Body", "Squat",          "Legs",      4, "8-10",  90);
            addExercise(exercises, plan, "Day 1 - Full Body", "Bench Press",     "Chest",     4, "8-10",  90);
            addExercise(exercises, plan, "Day 1 - Full Body", "Bent Over Row",   "Back",      3, "10-12", 75);
            addExercise(exercises, plan, "Day 1 - Full Body", "Overhead Press",  "Shoulders", 3, "10-12", 75);
            addExercise(exercises, plan, "Day 1 - Full Body", "Plank",           "Core",      3, "60sec", 45);

            addExercise(exercises, plan, "Day 2 - Full Body", "Deadlift",        "Back",      4, "6-8",   120);
            addExercise(exercises, plan, "Day 2 - Full Body", "Incline Press",   "Chest",     3, "10-12", 75);
            addExercise(exercises, plan, "Day 2 - Full Body", "Pull Up",         "Back",      3, "8-10",  75);
            addExercise(exercises, plan, "Day 2 - Full Body", "Lateral Raise",   "Shoulders", 3, "12-15", 60);
            addExercise(exercises, plan, "Day 2 - Full Body", "Leg Curl",        "Legs",      3, "12-15", 60);

            addExercise(exercises, plan, "Day 3 - Full Body", "Leg Press",       "Legs",      4, "10-12", 90);
            addExercise(exercises, plan, "Day 3 - Full Body", "Dumbbell Fly",    "Chest",     3, "12-15", 60);
            addExercise(exercises, plan, "Day 3 - Full Body", "Cable Row",       "Back",      3, "12-15", 60);
            addExercise(exercises, plan, "Day 3 - Full Body", "Bicep Curl",      "Arms",      3, "12-15", 45);
            addExercise(exercises, plan, "Day 3 - Full Body", "Tricep Pushdown", "Arms",      3, "12-15", 45);

        } else {
            // PPL — 6 days/week (same for CUT and BULK, reps differ)
            String strengthReps = goalType == Goal.GoalType.BULK ? "6-8" : "12-15";
            int strengthRest    = goalType == Goal.GoalType.BULK ? 120 : 60;

            // PUSH DAY (Chest, Shoulders, Triceps)
            addExercise(exercises, plan, "Push Day", "Bench Press",          "Chest",     4, strengthReps, strengthRest);
            addExercise(exercises, plan, "Push Day", "Incline Dumbbell Press","Chest",    3, "10-12",      75);
            addExercise(exercises, plan, "Push Day", "Overhead Press",        "Shoulders",4, strengthReps, strengthRest);
            addExercise(exercises, plan, "Push Day", "Lateral Raise",         "Shoulders",3, "15-20",      45);
            addExercise(exercises, plan, "Push Day", "Tricep Dips",           "Arms",     3, "10-12",      60);
            addExercise(exercises, plan, "Push Day", "Skull Crushers",        "Arms",     3, "10-12",      60);

            // PULL DAY (Back, Biceps)
            addExercise(exercises, plan, "Pull Day", "Deadlift",              "Back",     4, strengthReps, strengthRest);
            addExercise(exercises, plan, "Pull Day", "Pull Up",               "Back",     4, "6-10",       75);
            addExercise(exercises, plan, "Pull Day", "Bent Over Row",         "Back",     4, strengthReps, strengthRest);
            addExercise(exercises, plan, "Pull Day", "Face Pull",             "Shoulders",3, "15-20",      45);
            addExercise(exercises, plan, "Pull Day", "Barbell Curl",          "Arms",     3, "10-12",      60);
            addExercise(exercises, plan, "Pull Day", "Hammer Curl",           "Arms",     3, "12-15",      45);

            // LEG DAY (Quads, Hamstrings, Glutes, Calves)
            addExercise(exercises, plan, "Leg Day", "Squat",                  "Legs",     4, strengthReps, strengthRest);
            addExercise(exercises, plan, "Leg Day", "Romanian Deadlift",      "Legs",     3, "10-12",      90);
            addExercise(exercises, plan, "Leg Day", "Leg Press",              "Legs",     3, "12-15",      75);
            addExercise(exercises, plan, "Leg Day", "Leg Curl",               "Legs",     3, "12-15",      60);
            addExercise(exercises, plan, "Leg Day", "Calf Raise",             "Legs",     4, "15-20",      45);
            addExercise(exercises, plan, "Leg Day", "Plank",                  "Core",     3, "60sec",      30);
        }

        return exercises;
    }

    // Helper to reduce repetition
    private void addExercise(List<WorkoutExercise> list, WorkoutPlan plan,
                             String day, String name, String muscle,
                             int sets, String reps, int rest) {
        list.add(WorkoutExercise.builder()
                .workoutPlan(plan)
                .dayLabel(day)
                .exerciseName(name)
                .muscleGroup(muscle)
                .sets(sets)
                .reps(reps)
                .restSeconds(rest)
                .build());
    }

    private WorkoutPlanResponse mapToResponse(WorkoutPlan plan) {
        // Group exercises by day using streams
        Map<String, List<ExerciseDetail>> weeklyPlan = plan.getExercises()
                .stream()
                .collect(Collectors.groupingBy(
                        WorkoutExercise::getDayLabel,
                        LinkedHashMap::new,   // preserves insertion order
                        Collectors.mapping(ex -> ExerciseDetail.builder()
                                        .exerciseName(ex.getExerciseName())
                                        .muscleGroup(ex.getMuscleGroup())
                                        .sets(ex.getSets())
                                        .reps(ex.getReps())
                                        .restSeconds(ex.getRestSeconds())
                                        .notes(ex.getNotes())
                                        .build(),
                                Collectors.toList())
                ));

        return WorkoutPlanResponse.builder()
                .id(plan.getId())
                .planName(plan.getPlanName())
                .daysPerWeek(plan.getDaysPerWeek())
                .planType(plan.getPlanType().name())
                .weeklyPlan(weeklyPlan)
                .build();
    }
}