package com.fitforge.fitforge_backend.plan.workout;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workout_exercises")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlan workoutPlan;

    @Column(nullable = false)
    private String dayLabel;      // "Push Day", "Pull Day", etc.

    @Column(nullable = false)
    private String exerciseName;  // "Bench Press"

    @Column(nullable = false)
    private String muscleGroup;   // "Chest"

    @Column(nullable = false)
    private Integer sets;

    @Column(nullable = false)
    private String reps;          // "8-12" stored as string for flexibility

    @Column(nullable = false)
    private Integer restSeconds;

    private String notes;
}