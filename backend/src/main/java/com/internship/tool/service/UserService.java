package com.internship.tool.service;

import com.internship.tool.entity.User;
import com.internship.tool.entity.Role;
import com.internship.tool.repository.UserRepository;
import com.internship.tool.exception.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =========================
    // CREATE USER
    // =========================
    @CacheEvict(value = {"users", "user", "userEmail", "usersPage"}, allEntries = true)
    public User createUser(User user) {

        validateUser(user);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // ✅ Set default role
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        // ✅ Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // =========================
    // GET ALL USERS
    // =========================
    @Cacheable(value = "users")
    public List<User> getAllUsers() {
        System.out.println("Fetching users from DB...");
        return userRepository.findAll();
    }

    // =========================
    // GET ALL USERS (PAGINATED)
    // =========================
    @Cacheable(value = "usersPage",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<User> getAllUsers(Pageable pageable) {
        System.out.println("Fetching paginated users from DB...");
        return userRepository.findAll(pageable);
    }

    // =========================
    // GET USER BY ID
    // =========================
    @Cacheable(value = "user", key = "#id")
    public User getUserById(Long id) {
        System.out.println("Fetching user by ID from DB...");
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id));
    }

    // =========================
    // GET USER BY EMAIL
    // =========================
    @Cacheable(value = "userEmail", key = "#email")
    public User getUserByEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new InvalidInputException("Email cannot be empty");
        }

        System.out.println("Fetching user by email from DB...");
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: " + email));
    }

    // =========================
    // UPDATE USER
    // =========================
    @CacheEvict(value = {"users", "user", "userEmail", "usersPage"}, allEntries = true)
    public User updateUser(Long id, User user) {

        User existingUser = getUserById(id);

        // ✅ Update only if values are provided
        if (user.getName() != null && !user.getName().isBlank()) {
            existingUser.setName(user.getName());
        }

        if (user.getEmail() != null && !user.getEmail().isBlank()) {

            // Prevent duplicate email
            if (!existingUser.getEmail().equals(user.getEmail()) &&
                    userRepository.existsByEmail(user.getEmail())) {
                throw new DuplicateResourceException("Email already exists");
            }

            existingUser.setEmail(user.getEmail());
        }

        // ✅ Update password only if provided
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // ✅ Update role (optional — usually ADMIN only)
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }

        return userRepository.save(existingUser);
    }

    // =========================
    // DELETE USER
    // =========================
    @CacheEvict(value = {"users", "user", "userEmail", "usersPage"}, allEntries = true)
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    // =========================
    // VALIDATION
    // =========================
    private void validateUser(User user) {

        if (user == null) {
            throw new InvalidInputException("User cannot be null");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            throw new InvalidInputException("Name is required");
        }

        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new InvalidInputException("Invalid email format");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new InvalidInputException("Password is required");
        }
    }
}