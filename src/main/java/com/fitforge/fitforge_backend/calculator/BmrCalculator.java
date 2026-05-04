package com.fitforge.fitforge_backend.calculator;

import com.fitforge.fitforge_backend.user.FitnessProfile.Gender;
import org.springframework.stereotype.Component;

@Component
public class BmrCalculator {

    // Mifflin-St Jeor Equation — most accurate for general population
    public double calculate(double weightKg, double heightCm, int age, Gender gender) {
        double bmr = (10 * weightKg) + (6.25 * heightCm) - (5 * age);
        return gender == Gender.MALE ? bmr + 5 : bmr - 161;
    }
}