package com.fitforge.fitforge_backend.plan.diet;

import com.fitforge.fitforge_backend.goal.Goal;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "diet_plans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DietPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each goal gets exactly one diet plan
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false, unique = true)
    private Goal goal;

    @Column(nullable = false)
    private Integer dailyCalories;

    @Column(nullable = false)
    private Integer proteinGrams;

    @Column(nullable = false)
    private Integer carbsGrams;

    @Column(nullable = false)
    private Integer fatGrams;

    @Column(nullable = false)
    private Integer mealFrequency;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}