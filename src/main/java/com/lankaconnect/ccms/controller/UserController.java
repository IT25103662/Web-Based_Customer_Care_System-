package com.lankaconnect.ccms.controller;

import com.lankaconnect.ccms.model.User;
import com.lankaconnect.ccms.model.UserActivityLog;
import com.lankaconnect.ccms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        try {
            Long userId = Long.parseLong(payload.get("userId"));
            String fullName = payload.get("fullName");
            String phone = payload.get("phone");
            String email = payload.get("email");

            User updated = userService.updateProfile(userId, fullName, phone, email, request.getRemoteAddr());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        try {
            Long userId = Long.parseLong(payload.get("userId"));
            String oldPassword = payload.get("oldPassword");
            String newPassword = payload.get("newPassword");

            boolean success = userService.resetPassword(userId, oldPassword, newPassword, request.getRemoteAddr());
            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Password changed successfully"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Incorrect current password"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/manage/{id}")
    public ResponseEntity<?> manageUserRoleStatus(@PathVariable Long id, @RequestBody Map<String, Object> payload, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            Long adminId = (session != null && session.getAttribute("USER_ID") != null) ? (Long) session.getAttribute("USER_ID") : 1L;

            String role = (String) payload.get("role");
            String department = (String) payload.get("department");
            Boolean active = (Boolean) payload.get("active");

            User updated = userService.updateUserRoleAndStatus(adminId, id, role, department, active, request.getRemoteAddr());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/activity-logs")
    public ResponseEntity<List<UserActivityLog>> getActivityLogs() {
        return ResponseEntity.ok(userService.getRecentActivityLogs());
    }
}
