package com.finx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @GetMapping
    public String profile(Model model) {
        model.addAttribute("fullName",   "James Wilson");
        model.addAttribute("email",      "james.wilson@example.com");
        model.addAttribute("phone",      "+886 912 345 678");
        model.addAttribute("joinDate",   "2023 年 01 月");
        model.addAttribute("avatarInit", "JW");
        model.addAttribute("activePage", "profile");
        return "profile/index";
    }
}
