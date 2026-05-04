package com.fitforge.fitforge_backend.tracking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface CalorieLogRepository extends JpaRepository<CalorieLog, Long> {

    List<CalorieLog> findByUserIdAndLogDateOrderByCreatedAtAsc(Long userId, LocalDate date);

    // Sum today's calories
    @Query("SELECT COALESCE(SUM(c.calories), 0) FROM CalorieLog c " +
            "WHERE c.user.id = :userId AND c.logDate = :date")
    Integer sumCaloriesByUserIdAndDate(Long userId, LocalDate date);

    // Last 7 days daily totals
    @Query("SELECT c.logDate, SUM(c.calories) FROM CalorieLog c " +
            "WHERE c.user.id = :userId AND c.logDate >= :fromDate " +
            "GROUP BY c.logDate ORDER BY c.logDate ASC")
    List<Object[]> getDailyTotals(Long userId, LocalDate fromDate);
}