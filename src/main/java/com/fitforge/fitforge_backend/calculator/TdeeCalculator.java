package com.fitforge.fitforge_backend.calculator;

import com.fitforge.fitforge_backend.user.FitnessProfile.ActivityLevel;
import org.springframework.stereotype.Component;

@Component
public class TdeeCalculator {

    public double calculate(double bmr, ActivityLevel activityLevel) {
        double multiplier = switch (activityLevel) {
            case SEDENTARY         -> 1.2;
            case LIGHTLY_ACTIVE    -> 1.375;
            case MODERATELY_ACTIVE -> 1.55;
            case VERY_ACTIVE       -> 1.725;
            case EXTRA_ACTIVE      -> 1.9;
        };
        return bmr * multiplier;
    }
}