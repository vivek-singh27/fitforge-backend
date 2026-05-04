package com.fitforge.fitforge_backend.plan.workout;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    Optional<WorkoutPlan> findByGoalId(Long goalId);
}