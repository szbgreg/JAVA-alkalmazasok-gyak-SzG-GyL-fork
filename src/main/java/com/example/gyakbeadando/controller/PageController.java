package com.example.gyakbeadando.controller;

import com.example.gyakbeadando.model.Contact;
import com.example.gyakbeadando.repo.ContactRepository;
import com.example.gyakbeadando.repo.GepRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.gyakbeadando.model.RegisterForm;


import java.util.HashMap;

@Controller
public class PageController {

    private final GepRepository gepRepository;
    private final ContactRepository contactRepository;   // ← kisbetűs mező
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public PageController(GepRepository gepRepository,
                          ContactRepository contactRepository,
                          UserDetailsManager userDetailsManager,
                          UserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.gepRepository = gepRepository;
        this.contactRepository = contactRepository;
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    /*@GetMapping("/login")
    public String login(@RequestHeader(value="HX-Request", required=false) String hx,
                        @RequestParam(value="error", required=false) String error,
                        @RequestParam(value="logout", required=false) String logout,
                        Model model) {
        if (error != null)  model.addAttribute("loginError", true);
        if (logout != null) model.addAttribute("loggedOut", true);
        return (hx != null) ? "fragments/login :: content" : "layout";
    }*/


    @GetMapping("/")
    public String home(@RequestHeader(value = "HX-Request", required = false) String hx) {
        return (hx != null) ? "fragments/home :: content" : "layout";
    }

    @GetMapping("/database")
    public String database(@RequestHeader(value = "HX-Request", required = false) String hx,
                           Model model) {
        model.addAttribute("lista", gepRepository.findAllWithRefs());
        return (hx != null) ? "fragments/database :: content" : "layout";
    }

    // --- CONTACT: csak EZ az egyetlen GET legyen! ---
    @GetMapping("/contact")
    public String contact(@RequestHeader(value="HX-Request", required=false) String hx,
                          Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new Contact());
        }
        if (hx != null) {
            return "fragments/contact :: content";
        }
        model.addAttribute("view", "fragments/contact"); // <-- CSAK a template neve
        return "layout";
    }

    @PostMapping("/contact")
    public String submitContact(@RequestHeader(value="HX-Request", required=false) String hx,
                                @Valid @ModelAttribute("form") Contact form,
                                BindingResult br,
                                Model model,
                                RedirectAttributes ra) {
        if (br.hasErrors()) {
            if (hx != null) {
                return "fragments/contact :: content";
            }
            model.addAttribute("view", "fragments/contact"); // <-- CSAK a template neve
            return "layout";
        }

        contactRepository.save(form);

        if (hx != null) {
            model.addAttribute("ok", true);
            model.addAttribute("form", new Contact());
            return "fragments/contact :: content";
        }
        ra.addFlashAttribute("ok", true);
        return "redirect:/contact";
    }

    @GetMapping("/register")
    public String register(@RequestHeader(value="HX-Request", required=false) String hx,
                           Model model) {
        if (!model.containsAttribute("form")) model.addAttribute("form", new RegisterForm());
        if (hx != null) return "fragments/register :: content";
        model.addAttribute("view", "fragments/register");
        return "layout";

    }

    @PostMapping("/register")
    public String doRegister(@RequestHeader(value="HX-Request", required=false) String hx,
                             @Valid @ModelAttribute("form") RegisterForm form,
                             BindingResult br,
                             Model model,
                             RedirectAttributes ra) {

        // jelszó egyezés
        if (!br.hasErrors() && !form.getPassword().equals(form.getConfirmPassword())) {
            br.rejectValue("confirmPassword", "pw.mismatch", "A két jelszó nem egyezik");
        }

        // névfoglaltság
        if (!br.hasErrors() && userDetailsManager.userExists(form.getUsername())) {
            br.rejectValue("username", "username.exists", "Ez a felhasználónév már foglalt");
        }

        if (br.hasErrors()) {
            if (hx != null) return "fragments/register :: content";
            model.addAttribute("view", "fragments/register");
            return "layout";
        }

        // létrehozás
        UserDetails user = User.withUsername(form.getUsername())
                .password(passwordEncoder.encode(form.getPassword()))
                .roles("USER") // vagy authorities(...), ha authorities táblát használsz
                .build();
        userDetailsManager.createUser(user);

        if (hx != null) {
            model.addAttribute("ok", true);
            model.addAttribute("form", new RegisterForm());
            return "fragments/register :: content";
        }
        ra.addFlashAttribute("ok", true);
        return "redirect:/register";
    }

    // --- a többi oldal maradhat így ---
//    @GetMapping("/messages")
//    public String messages(@RequestHeader(value = "HX-Request", required = false) String hx) {
//        return (hx != null) ? "fragments/messages :: content" : "layout";
//    }

//    @GetMapping("/chart")
//    public String chart(@RequestHeader(value = "HX-Request", required = false) String hx) {
//        return (hx != null) ? "fragments/chart :: content" : "layout";
//    }

//    @GetMapping("/crud")
//    public String crud(@RequestHeader(value = "HX-Request", required = false) String hx) {
//        return (hx != null) ? "fragments/crud :: content" : "layout";
//    }

//    @GetMapping("/restful")
//    public String restful(@RequestHeader(value = "HX-Request", required = false) String hx) {
//        return (hx != null) ? "fragments/restful :: content" : "layout";
//    }

//    @GetMapping("/admin")
//    public String admin(@RequestHeader(value = "HX-Request", required = false) String hx) {
//        return (hx != null) ? "fragments/admin :: content" : "layout";
//    }

    @GetMapping("/login")
    public String login(@RequestHeader(value = "HX-Request", required = false) String hx) {
        return (hx != null) ? "fragments/login :: content" : "layout";
    }
    /*public String register(@RequestHeader(value = "HX-Request", required = false) String hx) {
        return (hx != null) ? "fragments/register :: content" : "layout";
    }*/




}
