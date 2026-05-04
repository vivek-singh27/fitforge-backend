package com.fitforge.fitforge_backend.tracking;

import com.fitforge.fitforge_backend.exception.ResourceNotFoundException;
import com.fitforge.fitforge_backend.tracking.dto.WorkoutLogRequest;
import com.fitforge.fitforge_backend.tracking.dto.WorkoutLogResponse;
import com.fitforge.fitforge_backend.user.User;
import com.fitforge.fitforge_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutLogService {

    private final WorkoutLogRepository workoutLogRepository;
    private final UserRepository userRepository;

    public WorkoutLogResponse logWorkout(String email, WorkoutLogRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDate date = request.getWorkoutDate() != null
                ? request.getWorkoutDate() : LocalDate.now();

        // Update if exists for that date
        WorkoutLog log = workoutLogRepository
                .findByUserIdAndWorkoutDate(user.getId(), date)
                .orElse(WorkoutLog.builder().user(user).workoutDate(date).build());

        log.setDayLabel(request.getDayLabel());
        log.setDurationMinutes(request.getDurationMinutes());
        log.setNotes(request.getNotes());
        log.setCompleted(request.getCompleted());

        workoutLogRepository.save(log);
        return mapToResponse(log);
    }

    public List<WorkoutLogResponse> getAllLogs(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return workoutLogRepository
                .findByUserIdOrderByWorkoutDateDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // How many workouts completed in last 7 days
    public int getWeeklyCount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return workoutLogRepository.findByUserIdAndWorkoutDateBetween(
                user.getId(),
                LocalDate.now().minusDays(7),
                LocalDate.now()
        ).size();
    }

    private WorkoutLogResponse mapToResponse(WorkoutLog log) {
        return WorkoutLogResponse.builder()
                .id(log.getId())
                .workoutDate(log.getWorkoutDate())
                .dayLabel(log.getDayLabel())
                .durationMinutes(log.getDurationMinutes())
                .notes(log.getNotes())
                .completed(log.getCompleted())
                .build();
    }
}