package com.fitforge.fitforge_backend.tracking;

import com.fitforge.fitforge_backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress_logs",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "log_date"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ProgressLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate logDate;

    @Column(nullable = false)
    private Double weightKg;

    private Double bodyFatPct;  // optional

    private String notes;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}