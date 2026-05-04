package com.fitforge.fitforge_backend.tracking.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CalorieLogRequest {

    @NotBlank(message = "Meal name is required")
    private String mealName;

    @NotNull @Min(1) @Max(5000)
    private Integer calories;

    private Double proteinGrams;
    private Double carbsGrams;
    private Double fatGrams;
    private LocalDate logDate;
}