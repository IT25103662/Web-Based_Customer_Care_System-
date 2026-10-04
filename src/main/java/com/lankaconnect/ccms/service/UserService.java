package com.lankaconnect.ccms.service;

import com.lankaconnect.ccms.model.User;
import com.lankaconnect.ccms.model.UserActivityLog;
import com.lankaconnect.ccms.repository.UserActivityLogRepository;
import com.lankaconnect.ccms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserActivityLogRepository activityLogRepository;

    public User registerUser(User user, String clientIp) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("CUSTOMER");
        }

        if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {
            if (user.getSubscriptionStartDate() == null) {
                user.setSubscriptionStartDate(LocalDate.now());
            }
            user.setLoyaltyTier(calculateLoyaltyTier(user.getSubscriptionStartDate()));
        } else {
            user.setLoyaltyTier(null); // Loyalty tier is strictly for customers only
        }

        user.setActive(true);
        user.setVerified(true);
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        logActivity(savedUser.getId(), savedUser.getUsername(), savedUser.getRole(),
                "REGISTER", "New user registered with role: " + savedUser.getRole(), clientIp);

        return savedUser;
    }

    public Optional<User> authenticate(String username, String password, String clientIp) {
        if (username == null || password == null) return Optional.empty();
        String trimmedUser = username.trim();
        String trimmedPass = password.trim();

        Optional<User> userOpt = userRepository.findByUsernameIgnoreCase(trimmedUser);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(trimmedPass) && user.isActive()) {
                if ("CUSTOMER".equalsIgnoreCase(user.getRole()) && user.getSubscriptionStartDate() != null) {
                    user.setLoyaltyTier(calculateLoyaltyTier(user.getSubscriptionStartDate()));
                    userRepository.save(user);
                }

                logActivity(user.getId(), user.getUsername(), user.getRole(),
                        "LOGIN", "User logged in successfully", clientIp);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public void logLogout(User user, String clientIp) {
        if (user != null) {
            logActivity(user.getId(), user.getUsername(), user.getRole(),
                    "LOGOUT", "User logged out", clientIp);
        }
    }

    public String calculateLoyaltyTier(LocalDate subscriptionStartDate) {
        com.lankaconnect.ccms.strategy.LoyaltyTierStrategy strategy = com.lankaconnect.ccms.strategy.LoyaltyStrategyContext.determineStrategy(subscriptionStartDate);
        return strategy.getTierName();
    }


    public User updateProfile(Long userId, String fullName, String phone, String email, String clientIp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setEmail(email);
        User updated = userRepository.save(user);

        logActivity(user.getId(), user.getUsername(), user.getRole(),
                "UPDATE_PROFILE", "User updated personal profile details", clientIp);
        return updated;
    }

    public boolean resetPassword(Long userId, String oldPassword, String newPassword, String clientIp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!user.getPassword().equals(oldPassword)) {
            return false;
        }
        user.setPassword(newPassword);
        userRepository.save(user);

        logActivity(user.getId(), user.getUsername(), user.getRole(),
                "RESET_PASSWORD", "User changed password", clientIp);
        return true;
    }

    public User updateUserRoleAndStatus(Long adminUserId, Long targetUserId, String newRole, String department, Boolean active, String clientIp) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found"));

        if (newRole != null && !newRole.isEmpty()) {
            targetUser.setRole(newRole.toUpperCase());
            if ("CUSTOMER".equalsIgnoreCase(newRole)) {
                if (targetUser.getSubscriptionStartDate() == null) {
                    targetUser.setSubscriptionStartDate(LocalDate.now());
                }
                targetUser.setLoyaltyTier(calculateLoyaltyTier(targetUser.getSubscriptionStartDate()));
            } else {
                targetUser.setLoyaltyTier(null); // strictly for customers only
            }
        }
        if (department != null) {
            targetUser.setDepartment(department);
        }
        if (active != null) {
            targetUser.setActive(active);
        }

        User saved = userRepository.save(targetUser);

        logActivity(adminUserId, "admin", "ADMIN",
                "USER_MANAGEMENT", "Updated user " + targetUser.getUsername() + " (Role: " + targetUser.getRole() + ", Active: " + targetUser.isActive() + ")", clientIp);

        return saved;
    }

    public User createUser(User user, String clientIp) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("CUSTOMER");
        } else {
            user.setRole(user.getRole().toUpperCase());
        }
        if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {
            if (user.getSubscriptionStartDate() == null) {
                user.setSubscriptionStartDate(LocalDate.now());
            }
            user.setLoyaltyTier(calculateLoyaltyTier(user.getSubscriptionStartDate()));
        }
        user.setActive(true);
        user.setVerified(true);
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        logActivity(saved.getId(), saved.getUsername(), saved.getRole(),
                "CREATE_USER", "Created user " + saved.getUsername() + " with role " + saved.getRole(), clientIp);
        return saved;
    }

    public void deleteUser(Long id, String clientIp) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepository.deleteById(id);
        logActivity(id, user.getUsername(), user.getRole(),
                "DELETE_USER", "Deleted user " + user.getUsername(), clientIp);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role.toUpperCase());
    }

    public void logActivity(Long userId, String username, String userRole, String action, String details, String ipAddress) {
        UserActivityLog log = new UserActivityLog(userId, username, userRole, action, details, ipAddress != null ? ipAddress : "127.0.0.1");
        activityLogRepository.save(log);
    }

    public List<UserActivityLog> getRecentActivityLogs() {
        return activityLogRepository.findTop100ByOrderByTimestampDesc();
    }
}

