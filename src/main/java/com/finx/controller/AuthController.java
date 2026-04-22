package com.finx.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        @RequestParam(required = false) String expired,
                        Authentication authentication,
                        Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        if (error != null)   model.addAttribute("errorMsg",  "Invalid username or password.");
        if (logout != null)  model.addAttribute("logoutMsg", "You have been logged out.");
        if (expired != null) model.addAttribute("errorMsg",  "Session expired. Please log in again.");
        return "auth/login";
    }
}
