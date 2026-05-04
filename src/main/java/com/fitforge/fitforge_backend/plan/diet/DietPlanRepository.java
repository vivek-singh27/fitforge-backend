package com.fitforge.fitforge_backend.plan.diet;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {
    Optional<DietPlan> findByGoalId(Long goalId);
}