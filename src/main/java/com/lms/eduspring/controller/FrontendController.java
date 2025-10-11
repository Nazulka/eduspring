package com.lms.eduspring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping("/")
    public String homePage() {
        // Spring automatically looks for 'home.html' in the templates folder.
        System.out.println("Serving the Home Page.");
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // looks for login.html templates folder
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // looks for register.html
    }
}
