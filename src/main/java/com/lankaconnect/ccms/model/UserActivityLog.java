package com.lankaconnect.ccms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity_logs")
public class UserActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String username;
    private String userRole;

    @Column(nullable = false)
    private String action; // LOGIN, LOGOUT, SUBMIT_COMPLAINT, ASSIGN_COMPLAINT, RESOLVE_COMPLAINT, CHAT_START, FEEDBACK_SUBMIT, ROLE_CHANGE

    @Column(length = 2000)
    private String details;

    private String ipAddress;

    private LocalDateTime timestamp = LocalDateTime.now();

    public UserActivityLog() {}

    public UserActivityLog(Long userId, String username, String userRole, String action, String details, String ipAddress) {
        this.userId = userId;
        this.username = username;
        this.userRole = userRole;
        this.action = action;
        this.details = details;
        this.ipAddress = ipAddress;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
