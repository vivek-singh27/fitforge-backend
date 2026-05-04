package com.fitforge.fitforge_backend.goal;

import com.fitforge.fitforge_backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalType goalType;

    private Double targetWeightKg;

    @Column(nullable = false)
    private Integer durationWeeks;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public enum GoalType {
        CUT, BULK, MAINTAIN
    }

    // Returns how many days are left in the goal
    // Used by ChatbotService to tell AI the remaining days
    public long getDaysRemaining() {
        if (this.endDate == null) return 0;
        return Math.max(ChronoUnit.DAYS.between(LocalDate.now(), this.endDate), 0);
    }
}