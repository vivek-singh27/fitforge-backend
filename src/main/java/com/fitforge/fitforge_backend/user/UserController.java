package com.fitforge.fitforge_backend.user;

import com.fitforge.fitforge_backend.user.dto.FitnessProfileRequest;
import com.fitforge.fitforge_backend.user.dto.FitnessProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // @AuthenticationPrincipal injects the logged-in user automatically
    // Spring reads it from the JWT token via our JwtAuthFilter
    @PostMapping("/profile")
    public ResponseEntity<FitnessProfileResponse> saveProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody FitnessProfileRequest request) {

        return ResponseEntity.ok(
                userService.saveOrUpdateProfile(userDetails.getUsername(), request));
    }

    @GetMapping("/profile")
    public ResponseEntity<FitnessProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                userService.getProfile(userDetails.getUsername()));
    }
}