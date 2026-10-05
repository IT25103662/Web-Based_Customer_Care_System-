package com.lankaconnect.ccms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ticketId;
    private String ticketCode;

    private Long complaintId;
    private String complaintCode;

    @Column(nullable = false)
    private Long customerId;
    private String customerName;

    @Column(nullable = false)
    private int rating; // 1 to 5 stars

    private String category; // Timeliness, Officer Behavior, Issue Resolution, System Ease

    @Column(length = 2000)
    private String comments;

    private boolean negativeFollowUpRequired = false; // Flagged true if rating <= 2
    private String followUpStatus; // PENDING, CONTACTED, RESOLVED

    private LocalDateTime createdAt = LocalDateTime.now();

    public Feedback() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public String getTicketCode() { return ticketCode; }
    public void setTicketCode(String ticketCode) { this.ticketCode = ticketCode; }

    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }

    public String getComplaintCode() { return complaintCode; }
    public void setComplaintCode(String complaintCode) { this.complaintCode = complaintCode; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getRating() { return rating; }
    public void setRating(int rating) {
        this.rating = rating;
        this.negativeFollowUpRequired = (rating <= 2);
        if (this.negativeFollowUpRequired) {
            this.followUpStatus = "PENDING";
        }
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public boolean isNegativeFollowUpRequired() { return negativeFollowUpRequired; }
    public void setNegativeFollowUpRequired(boolean negativeFollowUpRequired) { this.negativeFollowUpRequired = negativeFollowUpRequired; }

    public String getFollowUpStatus() { return followUpStatus; }
    public void setFollowUpStatus(String followUpStatus) { this.followUpStatus = followUpStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
