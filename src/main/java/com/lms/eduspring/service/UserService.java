package com.lms.eduspring.service;

import com.lms.eduspring.model.User;
import java.util.List;

public interface UserService {

    // Registers a new user with hashed password
    void registerUser(User user);

    // Finds user by username
    User findByUsername(String username);

    // Verifies login credentials (username + raw password)
    boolean verifyLogin(String username, String rawPassword);

    // === NEW METHODS for UserController ===
    List<User> getAllUsers();

    User getUserById(Long id);

    User updateUser(Long id, User updatedUser);

    void deleteUser(Long id);
}
