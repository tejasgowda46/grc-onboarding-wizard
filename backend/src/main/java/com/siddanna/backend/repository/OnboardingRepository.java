package com.siddanna.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.siddanna.backend.model.onboarding;

public interface OnboardingRepository extends JpaRepository<onboarding, Long> {
}