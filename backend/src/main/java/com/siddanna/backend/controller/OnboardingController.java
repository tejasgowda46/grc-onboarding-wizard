package com.siddanna.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.siddanna.backend.model.onboarding;
import com.siddanna.backend.repository.OnboardingRepository;

@RestController
@RequestMapping("/onboarding")
@CrossOrigin(origins = "http://localhost:5173") // ✅ FIX CORS
public class OnboardingController {

    private final OnboardingRepository repo;

    public OnboardingController(OnboardingRepository repo) {
        this.repo = repo;
    }

    // ✅ POST
    @PostMapping
    public onboarding save(@RequestBody onboarding data) {
        return repo.save(data);
    }

    // ✅ GET (THIS WAS MISSING → 405 ERROR)
    @GetMapping
    public List<onboarding> getAll() {
        return repo.findAll();
    }
}