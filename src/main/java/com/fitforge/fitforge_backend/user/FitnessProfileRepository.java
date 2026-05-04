package com.fitforge.fitforge_backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FitnessProfileRepository extends JpaRepository<FitnessProfile, Long> {

    // Spring generates the SQL automatically from the method name
    Optional<FitnessProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}