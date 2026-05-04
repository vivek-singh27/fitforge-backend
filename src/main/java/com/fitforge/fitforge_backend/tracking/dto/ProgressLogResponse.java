package com.fitforge.fitforge_backend.tracking.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class ProgressLogResponse {
    private Long id;
    private LocalDate logDate;
    private Double weightKg;
    private Double bodyFatPct;
    private String notes;
    private Double changeFromPrevious; // how much weight changed
}