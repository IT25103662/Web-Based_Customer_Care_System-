package com.lankaconnect.ccms.service;

import com.lankaconnect.ccms.model.Feedback;
import com.lankaconnect.ccms.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserService userService;
//c
    public Feedback submitFeedback(Feedback feedback, String clientIp) {
        feedback.setCreatedAt(LocalDateTime.now());
        if (feedback.getRating() <= 2) {
            feedback.setNegativeFollowUpRequired(true);
            feedback.setFollowUpStatus("PENDING");
        } else {
            feedback.setNegativeFollowUpRequired(false);
            feedback.setFollowUpStatus("N/A");
        }

        Feedback saved = feedbackRepository.save(feedback);

        userService.logActivity(feedback.getCustomerId(), feedback.getCustomerName(), "CUSTOMER",
                "FEEDBACK_SUBMIT", "Submitted " + feedback.getRating() + "-Star Feedback for Ticket " + feedback.getTicketCode(), clientIp);

        return saved;
    }

    public Feedback updateFollowUpStatus(Long feedbackId, String newStatus, String notes, Long managerId, String managerName, String clientIp) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found"));

        feedback.setFollowUpStatus(newStatus.toUpperCase());
        if (notes != null && !notes.isEmpty()) {
            feedback.setComments(feedback.getComments() + " [Manager Note by " + managerName + ": " + notes + "]");
        }

        Feedback saved = feedbackRepository.save(feedback);

        userService.logActivity(managerId, managerName, "MANAGER",
                "FEEDBACK_FOLLOWUP", "Updated follow-up status for Feedback #" + feedbackId + " to " + newStatus, clientIp);

        return saved;
    }

    public Map<String, Object> getCsatMetrics() {
        long total = feedbackRepository.countTotalFeedback();
        long positive = feedbackRepository.countPositiveRatings();
        Double avgRating = feedbackRepository.getAverageRating();

        double csatPercentage = (total > 0) ? ((double) positive / total) * 100.0 : 100.0;
        double roundedCsat = Math.round(csatPercentage * 10.0) / 10.0;
        double roundedAvg = (avgRating != null) ? Math.round(avgRating * 10.0) / 10.0 : 5.0;

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalFeedback", total);
        metrics.put("positiveRatings", positive);
        metrics.put("csatPercentage", roundedCsat);
        metrics.put("averageRating", roundedAvg);

        // Rating breakdown
        //vali
        List<Object[]> ratingCounts = feedbackRepository.countFeedbackByRating();
        Map<Integer, Long> breakdown = new HashMap<>();
        for (int i = 1; i <= 5; i++) breakdown.put(i, 0L);
        for (Object[] row : ratingCounts) {
            if (row[0] != null && row[1] != null) {
                breakdown.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
            }
        }
        metrics.put("ratingBreakdown", breakdown);

        return metrics;
    }

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Feedback> getCustomerFeedback(Long customerId) {
        return feedbackRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public List<Feedback> getNegativeFeedbackRequiringFollowUp() {
        return feedbackRepository.findByNegativeFollowUpRequiredTrueOrderByCreatedAtDesc();
    }

    public Optional<Feedback> getFeedbackById(Long id) {
        return feedbackRepository.findById(id);
    }

    public Optional<Feedback> getFeedbackByTicketId(Long ticketId) {
        return feedbackRepository.findByTicketId(ticketId);
    }
}
