package com.fitforge.fitforge_backend.tracking;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {

    List<WorkoutLog> findByUserIdOrderByWorkoutDateDesc(Long userId);

    Optional<WorkoutLog> findByUserIdAndWorkoutDate(Long userId, LocalDate date);

    // Count completed workouts in last 7 days
    List<WorkoutLog> findByUserIdAndWorkoutDateBetween(
            Long userId, LocalDate from, LocalDate to);
}