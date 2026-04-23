package com.internship.tool.service;

import com.internship.tool.entity.User;
import com.internship.tool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Create user
    public User createUser(User user) {
        return userRepository.save(user);
    }

    //  Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //  Get user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    //  Get user by email (Step 6 method)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Delete user
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}