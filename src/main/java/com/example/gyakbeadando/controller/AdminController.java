package com.example.gyakbeadando.controller;

import com.example.gyakbeadando.model.User;
import com.example.gyakbeadando.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class AdminController {
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/admin")
    public String showEditPage(@RequestHeader(value = "HX-Request", required = false) String hx, Model model) {

        model.addAttribute("users", userRepo.findAll());

        if (hx != null) {
            return "fragments/admin :: content";
        } else {
            model.addAttribute("view", "fragments/admin");
            return "layout";
        }
    }
}
