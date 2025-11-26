package com.lms.eduspring.service;

import com.lms.eduspring.model.User;

import java.util.List;

public interface UserService {

    // register new user
    void registerUser(User user);

    // find user by username
    User findByUsername(String username);

    // get user by ID
    User getUserById(Long id);

    // update user
    User updateUser(Long id, User updatedUser);

    // delete user
    void deleteUser(Long id);

    // get all users
    List<User> getAllUsers();

    // get current user ID from JWT
    Long getCurrentUserId();

    boolean verifyLogin(String username, String password);
}
