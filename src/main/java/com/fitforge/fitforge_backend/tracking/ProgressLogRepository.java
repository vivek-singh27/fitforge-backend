package com.fitforge.fitforge_backend.tracking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProgressLogRepository extends JpaRepository<ProgressLog, Long> {

    List<ProgressLog> findByUserIdOrderByLogDateAsc(Long userId);

    Optional<ProgressLog> findByUserIdAndLogDate(Long userId, LocalDate date);

    // Last 30 days only — for the chart
    @Query("SELECT p FROM ProgressLog p WHERE p.user.id = :userId " +
            "AND p.logDate >= :fromDate ORDER BY p.logDate ASC")
    List<ProgressLog> findRecentLogs(Long userId, LocalDate fromDate);

    // Latest single log
    Optional<ProgressLog> findTopByUserIdOrderByLogDateDesc(Long userId);
}