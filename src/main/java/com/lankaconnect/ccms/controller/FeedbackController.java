package com.lankaconnect.ccms.controller;

import com.lankaconnect.ccms.model.Feedback;
import com.lankaconnect.ccms.service.FeedbackService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<?> submitFeedback(@RequestBody Feedback feedback, HttpServletRequest request) {
        try {
            Feedback saved = feedbackService.submitFeedback(feedback, request.getRemoteAddr());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getCsatMetrics() {
        return ResponseEntity.ok(feedbackService.getCsatMetrics());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Feedback>> getCustomerFeedback(@PathVariable Long customerId) {
        return ResponseEntity.ok(feedbackService.getCustomerFeedback(customerId));
    }

    @GetMapping("/negative-followups")
    public ResponseEntity<List<Feedback>> getNegativeFollowUps() {
        return ResponseEntity.ok(feedbackService.getNegativeFeedbackRequiringFollowUp());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFeedbackById(@PathVariable Long id) {
        return feedbackService.getFeedbackById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/followup")
    public ResponseEntity<?> updateFollowUp(@PathVariable Long id, @RequestBody Map<String, String> payload, HttpServletRequest request) {
        try {
            String status = payload.get("status");
            String notes = payload.get("notes");
            Long managerId = Long.parseLong(payload.get("managerId"));
            String managerName = payload.get("managerName");

            Feedback updated = feedbackService.updateFollowUpStatus(id, status, notes, managerId, managerName, request.getRemoteAddr());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFeedback(@PathVariable Long id, @RequestBody Feedback feedback, HttpServletRequest request) {
        try {
            Feedback updated = feedbackService.updateFeedback(id, feedback, request.getRemoteAddr());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id, HttpServletRequest request) {
        try {
            feedbackService.deleteFeedback(id, request.getRemoteAddr());
            return ResponseEntity.ok(Map.of("success", true, "message", "Feedback deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}


