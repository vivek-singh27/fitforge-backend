package com.fitforge.fitforge_backend.user.dto;

import com.fitforge.fitforge_backend.user.FitnessProfile.ActivityLevel;
import com.fitforge.fitforge_backend.user.FitnessProfile.Gender;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FitnessProfileResponse {
    private Long id;
    private Integer age;
    private Gender gender;
    private Double heightCm;
    private Double weightKg;
    private ActivityLevel activityLevel;
    private Double bmi;           // calculated on the fly, not stored
    private String bmiCategory;   // "Normal", "Overweight", etc.
}