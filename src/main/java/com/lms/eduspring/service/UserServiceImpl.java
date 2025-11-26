package com.lms.eduspring.service;

import com.lms.eduspring.model.User;
import com.lms.eduspring.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =============================
    // 🔐 JWT identity
    // =============================
    @Override
    public Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = findByUsername(username);
        return user.getId();
    }

    // =============================
    // 🔑 LOGIN VERIFICATION
    // =============================
    @Override
    public boolean verifyLogin(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return false;
        }

        return passwordEncoder.matches(password, user.getPassword());
    }

    // =============================
    // 👤 REGISTER USER
    // =============================
    @Override
    public void registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    // =============================
    // 🔍 FIND USER BY USERNAME
    // =============================
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found with username: " + username));
    }

    // =============================
    // 🔍 FIND USER BY ID
    // =============================
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found with id: " + id));
    }

    // =============================
    // 🛠 UPDATE USER
    // =============================
    @Override
    public User updateUser(Long id, User updatedUser) {
        User existing = getUserById(id);

        existing.setFirstName(updatedUser.getFirstName());
        existing.setLastName(updatedUser.getLastName());
        existing.setEmail(updatedUser.getEmail());
        existing.setUsername(updatedUser.getUsername());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(existing);
    }

    // === DELETE USER ===
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // =============================
    // 📃 GET ALL USERS
    // =============================
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
