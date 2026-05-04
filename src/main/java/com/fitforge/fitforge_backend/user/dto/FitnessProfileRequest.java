package com.fitforge.fitforge_backend.user.dto;

import com.fitforge.fitforge_backend.user.FitnessProfile.ActivityLevel;
import com.fitforge.fitforge_backend.user.FitnessProfile.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class FitnessProfileRequest {

    @NotNull @Min(10) @Max(100)
    private Integer age;

    @NotNull
    private Gender gender;

    @NotNull @DecimalMin("100.0") @DecimalMax("250.0")
    private Double heightCm;

    @NotNull @DecimalMin("30.0") @DecimalMax("300.0")
    private Double weightKg;

    @NotNull
    private ActivityLevel activityLevel;
}