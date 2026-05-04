package com.fitforge.fitforge_backend.tracking;

import com.fitforge.fitforge_backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "calorie_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CalorieLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate logDate;

    @Column(nullable = false)
    private String mealName;       // Breakfast, Lunch etc.

    @Column(nullable = false)
    private Integer calories;

    private Double proteinGrams;
    private Double carbsGrams;
    private Double fatGrams;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}