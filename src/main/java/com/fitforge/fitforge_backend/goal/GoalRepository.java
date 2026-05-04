package com.fitforge.fitforge_backend.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    Optional<Goal> findByUserIdAndIsActiveTrue(Long userId);

    List<Goal> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Deactivates ALL active goals for a user before setting a new one
    @Modifying
    @Transactional
    @Query("UPDATE Goal g SET g.isActive = false WHERE g.user.id = :userId")
    void deactivateAllGoalsForUser(Long userId);
}