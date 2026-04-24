package com.internship.tool.service;

import com.internship.tool.entity.User;
import com.internship.tool.repository.UserRepository;
import com.internship.tool.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // CREATE USER
    public User createUser(User user) {

        validateUser(user);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        return userRepository.save(user);
    }

    // GET ALL USERS
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // GET USER BY ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id));
    }

    // GET USER BY EMAIL
    public User getUserByEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new InvalidInputException("Email cannot be empty");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: " + email));
    }

    // DELETE USER
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    // VALIDATION
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
    }
}