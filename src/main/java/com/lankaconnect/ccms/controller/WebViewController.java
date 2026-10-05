package com.lankaconnect.ccms.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for handling root URL routing, shortcuts, and fallback error redirection.
 * Prevents Spring Boot Whitelabel Error Page when accessing http://localhost:8080/ or unmapped routes.
 */
@Controller
public class WebViewController implements ErrorController {

    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/login.html";
    }

    @RequestMapping("/error")
    public String handleError() {
        return "redirect:/index.html";
    }
}
