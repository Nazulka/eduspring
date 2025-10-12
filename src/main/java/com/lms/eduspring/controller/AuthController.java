package com.lms.eduspring.controller;

import com.lms.eduspring.dto.UserRegistrationDto;
import com.lms.eduspring.dto.LoginRequestDto;
import com.lms.eduspring.model.User;
import com.lms.eduspring.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }


    // ✅ 1. Handles form submission from Thymeleaf (Frontend)
    @PostMapping(value = "/register", consumes = "application/x-www-form-urlencoded")
    public String registerForm(@ModelAttribute("user") UserRegistrationDto dto, Model model) {
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
            model.addAttribute("message", "Registration successful! Please log in.");
            return "redirect:/login"; // redirects to your login page
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register"; // reload registration form with error
        }
    }

    // ✅ 2. Handles JSON POSTs from API / Postman / React
    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> register(@RequestBody UserRegistrationDto registrationDto) {
        try {
            User user = new User(
                    registrationDto.getUsername(),
                    registrationDto.getPassword(),
                    registrationDto.getFirstName(),
                    registrationDto.getLastName(),
                    registrationDto.getEmail(),
                    "STUDENT"
            );
            userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    //  Login endpoint
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginDto) {
        boolean success = userService.verifyLogin(loginDto.getUsername(), loginDto.getPassword());
        if (success) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
