package com.fitforge.fitforge_backend.user;

import com.fitforge.fitforge_backend.exception.ResourceNotFoundException;
import com.fitforge.fitforge_backend.user.dto.FitnessProfileRequest;
import com.fitforge.fitforge_backend.user.dto.FitnessProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FitnessProfileRepository profileRepository;

    public FitnessProfileResponse saveOrUpdateProfile(String email,
                                                      FitnessProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // If profile exists → update it. If not → create new one.
        FitnessProfile profile = profileRepository.findByUserId(user.getId())
                .orElse(FitnessProfile.builder().user(user).build());

        profile.setAge(request.getAge());
        profile.setGender(request.getGender());
        profile.setHeightCm(request.getHeightCm());
        profile.setWeightKg(request.getWeightKg());
        profile.setActivityLevel(request.getActivityLevel());

        profileRepository.save(profile);
        return mapToResponse(profile);
    }

    public FitnessProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        FitnessProfile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profile not found. Please complete your profile first."));

        return mapToResponse(profile);
    }

    // Converts entity → response DTO + calculates BMI
    private FitnessProfileResponse mapToResponse(FitnessProfile profile) {
        double heightM = profile.getHeightCm() / 100.0;
        double bmi = profile.getWeightKg() / (heightM * heightM);
        bmi = Math.round(bmi * 10.0) / 10.0; // round to 1 decimal

        return FitnessProfileResponse.builder()
                .id(profile.getId())
                .age(profile.getAge())
                .gender(profile.getGender())
                .heightCm(profile.getHeightCm())
                .weightKg(profile.getWeightKg())
                .activityLevel(profile.getActivityLevel())
                .bmi(bmi)
                .bmiCategory(getBmiCategory(bmi))
                .build();
    }

    private String getBmiCategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25.0) return "Normal";
        if (bmi < 30.0) return "Overweight";
        return "Obese";
    }
}