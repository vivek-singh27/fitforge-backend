package com.fitforge.fitforge_backend.tracking.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ProgressLogRequest {

    @NotNull
    @DecimalMin("20.0") @DecimalMax("500.0")
    private Double weightKg;

    @DecimalMin("1.0") @DecimalMax("70.0")
    private Double bodyFatPct;  // optional

    private String notes;

    // If null, backend defaults to today
    private LocalDate logDate;
}