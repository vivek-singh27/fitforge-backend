package com.fitforge.fitforge_backend.tracking;

import com.fitforge.fitforge_backend.exception.ResourceNotFoundException;
import com.fitforge.fitforge_backend.tracking.dto.ProgressLogRequest;
import com.fitforge.fitforge_backend.tracking.dto.ProgressLogResponse;
import com.fitforge.fitforge_backend.tracking.dto.ProgressSummaryResponse;
import com.fitforge.fitforge_backend.user.User;
import com.fitforge.fitforge_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressLogService {

    private final ProgressLogRepository progressLogRepository;
    private final UserRepository userRepository;

    public ProgressLogResponse logProgress(String email, ProgressLogRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDate date = request.getLogDate() != null
                ? request.getLogDate() : LocalDate.now();

        // If log exists for today → update it
        ProgressLog log = progressLogRepository
                .findByUserIdAndLogDate(user.getId(), date)
                .orElse(ProgressLog.builder().user(user).logDate(date).build());

        log.setWeightKg(request.getWeightKg());
        log.setBodyFatPct(request.getBodyFatPct());
        log.setNotes(request.getNotes());

        progressLogRepository.save(log);
        return mapToResponse(log, null);
    }

    public ProgressSummaryResponse getSummary(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Last 30 days for chart
        List<ProgressLog> logs = progressLogRepository
                .findRecentLogs(user.getId(), LocalDate.now().minusDays(30));

        if (logs.isEmpty()) {
            return ProgressSummaryResponse.builder()
                    .totalLogsCount(0)
                    .logs(List.of())
                    .build();
        }

        double startWeight   = logs.get(0).getWeightKg();
        double currentWeight = logs.get(logs.size() - 1).getWeightKg();
        double totalChange   = Math.round((currentWeight - startWeight) * 10.0) / 10.0;

        double lowest  = logs.stream().mapToDouble(ProgressLog::getWeightKg).min().orElse(0);
        double highest = logs.stream().mapToDouble(ProgressLog::getWeightKg).max().orElse(0);

        // Build response list with change-from-previous
        List<ProgressLogResponse> responses = buildWithChanges(logs);

        return ProgressSummaryResponse.builder()
                .startWeight(startWeight)
                .currentWeight(currentWeight)
                .totalChange(totalChange)
                .lowestWeight(lowest)
                .highestWeight(highest)
                .totalLogsCount(logs.size())
                .logs(responses)
                .build();
    }

    public List<ProgressLogResponse> getAllLogs(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<ProgressLog> logs = progressLogRepository
                .findByUserIdOrderByLogDateAsc(user.getId());

        return buildWithChanges(logs);
    }

    // Calculates weight change compared to previous entry
    private List<ProgressLogResponse> buildWithChanges(List<ProgressLog> logs) {
        return java.util.stream.IntStream.range(0, logs.size())
                .mapToObj(i -> {
                    ProgressLog current = logs.get(i);
                    Double change = null;
                    if (i > 0) {
                        change = Math.round(
                                (current.getWeightKg() - logs.get(i - 1).getWeightKg()) * 10.0
                        ) / 10.0;
                    }
                    return mapToResponse(current, change);
                })
                .collect(Collectors.toList());
    }

    private ProgressLogResponse mapToResponse(ProgressLog log, Double change) {
        return ProgressLogResponse.builder()
                .id(log.getId())
                .logDate(log.getLogDate())
                .weightKg(log.getWeightKg())
                .bodyFatPct(log.getBodyFatPct())
                .notes(log.getNotes())
                .changeFromPrevious(change)
                .build();
    }
}