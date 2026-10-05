package com.lankaconnect.ccms.controller;

import com.lankaconnect.ccms.model.User;
import com.lankaconnect.ccms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpServletRequest request) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        String clientIp = request.getRemoteAddr();

        Optional<User> userOpt = userService.authenticate(username, password, clientIp);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            HttpSession session = request.getSession(true);
            session.setAttribute("USER", user);
            session.setAttribute("USER_ID", user.getId());
            session.setAttribute("ROLE", user.getRole());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", user);

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Invalid username or password");
            return ResponseEntity.status(401).body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user, HttpServletRequest request) {
        try {
            String clientIp = request.getRemoteAddr();
            User registered = userService.registerUser(user, clientIp);

            HttpSession session = request.getSession(true);
            session.setAttribute("USER", registered);
            session.setAttribute("USER_ID", registered.getId());
            session.setAttribute("ROLE", registered.getRole());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Account registered successfully");
            response.put("user", registered);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("USER");
        if (user != null) {
            // Refresh user details from DB
            User fresh = userService.getUserById(user.getId()).orElse(user);
            session.setAttribute("USER", fresh);
            return ResponseEntity.ok(fresh);
        }
        return ResponseEntity.status(401).body(Map.of("authenticated", false));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("USER");
            userService.logLogout(user, request.getRemoteAddr());
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("success", true, "message", "Logged out successfully"));
    }
}
