package com.lms.eduspring.controller;

import com.lms.eduspring.dto.UserRegistrationDto;
import com.lms.eduspring.dto.LoginRequestDto;
import com.lms.eduspring.model.User;
import com.lms.eduspring.service.UserService;
import jakarta.validation.Valid; // NEW: Needed for validation
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; // We need this for the Thymeleaf form submission method
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // NEW: Needed for form error handling
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // NEW: Needed for success messages

@Controller // Use @Controller for the class since we have a mix of view and JSON responses
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ✅ 1. Handles Thymeleaf Form Submission (for Tasks 7, 8, 9)
    // NOTE: This method should be moved to a FrontendController for cleaner architecture
    @PostMapping(value = "/register", consumes = "application/x-www-form-urlencoded")
    public String registerForm(
            @Valid @ModelAttribute("registrationForm") UserRegistrationDto dto, // Use @Valid and BindingResult
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            // Task 8: If validation fails, return to the registration page.
            return "register";
        }

        try {
            User user = new User(
                    dto.getUsername(),
                    dto.getPassword(),
                    dto.getFirstName(), // Updated with new DTO fields
                    dto.getLastName(),  // Updated with new DTO fields
                    dto.getEmail(),
                    "STUDENT"
            );
            userService.registerUser(user);

            // Task 9: Use flash attribute for success message
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! You can now log in.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            // Handle backend errors (e.g., username taken) by adding it to the model
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            // Need to return to the form, but redirection loses model data, so we need to handle this in DTO validation.
            // For now, let's keep it simple and redirect back.
            return "redirect:/register";
        }
    }

    // ✅ 2. Handles JSON POSTs from API / Postman / Tests
    // The tests in AuthControllerTest are hitting this endpoint.
    @PostMapping(value = "/register", consumes = "application/json")
    @ResponseBody // Tells Spring to treat the return value as the response body (JSON/String)
    public ResponseEntity<String> register(@Valid @RequestBody UserRegistrationDto registrationDto) { // ADDED @Valid
        try {
            User user = new User(
                    registrationDto.getUsername(),
                    registrationDto.getPassword(),
                    registrationDto.getFirstName(), // Updated with new DTO fields
                    registrationDto.getLastName(), // Updated with new DTO fields
                    registrationDto.getEmail(),
                    "STUDENT"
            );
            userService.registerUser(user);
            // Tests expect HTTP Status 201 Created and the body string.
            return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful! Please log in.");
        } catch (IllegalArgumentException e) {
            // Tests expect HTTP Status 400 Bad Request and the exception message.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Login endpoint
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginDto) {
        boolean success = userService.verifyLogin(loginDto.getUsername(), loginDto.getPassword());
        if (success) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
