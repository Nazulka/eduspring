package com.lms.eduspring.controller;

import com.lms.eduspring.security.JwtService;
import com.lms.eduspring.dto.LoginRequestDto;
import com.lms.eduspring.dto.UserRegistrationDto;
import com.lms.eduspring.model.User;
import com.lms.eduspring.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000") // allow React frontend
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // === POST: Register new user ===
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDto dto) {
        try {
            User user = new User(
                    dto.getUsername(),
                    dto.getPassword(),
                    dto.getFirstName(),
                    dto.getLastName(),
                    dto.getEmail(),
                    "STUDENT"
            );
            userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("{\"message\":\"Registration successful!\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // === POST: Login ===
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginDto) {
        boolean success = userService.verifyLogin(loginDto.getUsername(), loginDto.getPassword());
        if (success) {
            String token = jwtService.generateToken(loginDto.getUsername(), Map.of());
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\":\"Invalid username or password\"}");
        }
    }
}
