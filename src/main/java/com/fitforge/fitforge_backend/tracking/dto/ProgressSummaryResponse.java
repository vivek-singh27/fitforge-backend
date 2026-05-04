package com.fitforge.fitforge_backend.tracking.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ProgressSummaryResponse {
    private Double startWeight;
    private Double currentWeight;
    private Double totalChange;
    private Double lowestWeight;
    private Double highestWeight;
    private Integer totalLogsCount;
    private List<ProgressLogResponse> logs; // for chart
}