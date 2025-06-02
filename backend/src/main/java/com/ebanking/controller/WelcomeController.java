package com.ebanking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Welcome Controller that handles the welcome pages
 */
@Controller
public class WelcomeController {

    /**
     * Handles the root URL and displays a welcome page
     *
     * @param model the model to pass data to the view
     * @return the view name
     */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Welcome to E-Banking Application!");
        model.addAttribute("description", "User Management & Authentication System is now active with PostgreSQL and Docker.");
        return "welcome";
    }

    /**
     * Handles the /welcome URL and displays a welcome page
     *
     * @param model the model to pass data to the view
     * @return the view name
     */
    @GetMapping("/welcome")
    public String welcome(Model model) {
        model.addAttribute("message", "Welcome to E-Banking Application!");
        model.addAttribute("description", "User Management & Authentication System is now active with PostgreSQL and Docker.");
        return "welcome";
    }
}
