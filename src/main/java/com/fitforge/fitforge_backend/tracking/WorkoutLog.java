package com.fitforge.fitforge_backend.tracking;

import com.fitforge.fitforge_backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "workout_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class WorkoutLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate workoutDate;

    @Column(nullable = false)
    private String dayLabel;       // "Push Day", "Pull Day"

    private Integer durationMinutes;

    private String notes;

    // Did they complete it?
    @Builder.Default
    private Boolean completed = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}