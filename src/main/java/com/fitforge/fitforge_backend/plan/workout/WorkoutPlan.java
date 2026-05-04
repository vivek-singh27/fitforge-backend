package com.fitforge.fitforge_backend.plan.workout;

import com.fitforge.fitforge_backend.goal.Goal;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "workout_plans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class WorkoutPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false, unique = true)
    private Goal goal;

    @Column(nullable = false)
    private String planName;

    @Column(nullable = false)
    private Integer daysPerWeek;

    @Enumerated(EnumType.STRING)
    private PlanType planType;

    // One workout plan has many exercises across different days
    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkoutExercise> exercises;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public enum PlanType {
        PPL,         // Push Pull Legs
        FULL_BODY,
        UPPER_LOWER,
        HIIT
    }
}