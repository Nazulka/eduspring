package com.lms.eduspring.controller;

import com.lms.eduspring.dto.UserRegistrationDto;
import com.lms.eduspring.dto.LoginRequestDto;
import com.lms.eduspring.model.User;
import com.lms.eduspring.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // === GET registration page ===
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registrationForm", new UserRegistrationDto());
        return "register";
    }

    // === POST registration form ===
    @PostMapping(value = "/register", consumes = "application/x-www-form-urlencoded")
    public String registerForm(
            @Valid @ModelAttribute("registrationForm") UserRegistrationDto dto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "register";
        }

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
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! You can now log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }

    // === GET login page ===
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginForm", new LoginRequestDto());
        return "login";
    }

    // === POST login API (JSON) ===
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginDto) {
        boolean success = userService.verifyLogin(loginDto.getUsername(), loginDto.getPassword());
        if (success) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }

    // === POST API registration (JSON) ===
    @PostMapping(value = "/api/auth/register", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> registerApi(@Valid @RequestBody UserRegistrationDto dto) {
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
                    .body("Registration successful! Please log in.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
