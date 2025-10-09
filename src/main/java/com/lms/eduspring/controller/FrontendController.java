package com.lms.eduspring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // looks for login.html templates folder
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // looks for register.html
    }
}
