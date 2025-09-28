package com.lms.eduspring.service;

import com.lms.eduspring.model.User;

public interface UserService {

    // Registers a new user with hashed password
    void registerUser(User user);

    // Finds user by username
    User findByUsername(String username);

    // Verifies login credentials (username + raw password)
    boolean verifyLogin(String username, String rawPassword);
}
