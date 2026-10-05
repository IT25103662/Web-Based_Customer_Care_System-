package com.lankaconnect.ccms.service;

import com.lankaconnect.ccms.model.AlertThreshold;
import com.lankaconnect.ccms.repository.AlertThresholdRepository;
import com.lankaconnect.ccms.repository.ComplaintRepository;
import com.lankaconnect.ccms.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReportingService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private AlertThresholdRepository alertThresholdRepository;

    @Autowired
    private com.lankaconnect.ccms.repository.GeneratedReportRepository generatedReportRepository;

    @Autowired
    private com.lankaconnect.ccms.observer.RegionalSpikeAlertPublisher alertPublisher;

    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long totalComplaints = complaintRepository.count();
        long totalTickets = ticketRepository.count();
        long overdueTickets = ticketRepository.countByOverdueTrue();
        int activeChats = chatService.getActiveSessions().size() + chatService.getQueuedSessions().size();

        Map<String, Object> csat = feedbackService.getCsatMetrics();

        stats.put("totalComplaints", totalComplaints);
        stats.put("totalTickets", totalTickets);
        stats.put("overdueTickets", overdueTickets);
        stats.put("activeChats", activeChats);
        stats.put("csatPercentage", csat.get("csatPercentage"));
        stats.put("averageRating", csat.get("averageRating"));
        stats.put("totalFeedback", csat.get("totalFeedback"));

        // Status counts
        List<Object[]> ticketStatusList = ticketRepository.countTicketsByStatus();
        Map<String, Long> ticketStatusMap = new HashMap<>();
        for (Object[] row : ticketStatusList) {
            if (row[0] != null && row[1] != null) {
                ticketStatusMap.put(row[0].toString(), ((Number) row[1]).longValue());
            }
        }
        stats.put("ticketStatusBreakdown", ticketStatusMap);

        // Complaints by Category
        List<Object[]> categoryList = complaintRepository.countComplaintsByCategory();
        Map<String, Long> categoryMap = new HashMap<>();
        for (Object[] row : categoryList) {
            if (row[0] != null && row[1] != null) {
                categoryMap.put(row[0].toString(), ((Number) row[1]).longValue());
            }
        }
        stats.put("categoryBreakdown", categoryMap);

        // Complaints by Region
        List<Object[]> regionList = complaintRepository.countComplaintsByRegion();
        Map<String, Long> regionMap = new HashMap<>();
        for (Object[] row : regionList) {
            if (row[0] != null && row[1] != null) {
                regionMap.put(row[0].toString(), ((Number) row[1]).longValue());
            }
        }
        stats.put("regionBreakdown", regionMap);

        // Priority breakdown
        List<Object[]> priorityList = ticketRepository.countTicketsByPriority();
        Map<String, Long> priorityMap = new HashMap<>();
        for (Object[] row : priorityList) {
            if (row[0] != null && row[1] != null) {
                priorityMap.put(row[0].toString(), ((Number) row[1]).longValue());
            }
        }
        stats.put("priorityBreakdown", priorityMap);

        // Regional Spike Alerts
        stats.put("activeSpikeAlerts", getTriggeredSpikeAlerts());

        return stats;
    }

    public List<Map<String, Object>> getTriggeredSpikeAlerts() {
        List<AlertThreshold> thresholds = alertThresholdRepository.findByActiveTrue();
        List<Map<String, Object>> triggered = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        for (AlertThreshold at : thresholds) {
            LocalDateTime since = now.minusHours(at.getTimeWindowHours());
            long count = complaintRepository.countByRegionSince(at.getRegion(), since);

            boolean isSpike = count >= at.getThresholdCount();
            if (isSpike && alertPublisher != null) {
                // Notify Observers (Observer Pattern)
                alertPublisher.notifyObservers(at.getRegion(), count, at.getThresholdCount(), at.getTimeWindowHours());
            }

            Map<String, Object> alert = new HashMap<>();
            alert.put("region", at.getRegion());
            alert.put("currentCount", count);
            alert.put("thresholdLimit", at.getThresholdCount());
            alert.put("windowHours", at.getTimeWindowHours());
            alert.put("isSpike", isSpike);
            alert.put("status", isSpike ? "CRITICAL_SPIKE_ALERT" : "NORMAL");
            alert.put("lastTriggered", at.getLastTriggeredAt());
            alert.put("message", isSpike
                    ? "Warning: Regional complaint spike detected in " + at.getRegion() + "! (" + count + " complaints in last " + at.getTimeWindowHours() + "h exceeds limit of " + at.getThresholdCount() + ")"
                    : "Normal operational volume in " + at.getRegion());

            triggered.add(alert);
        }

        return triggered;
    }


    public List<AlertThreshold> getAllThresholds() {
        return alertThresholdRepository.findAll();
    }

    public Optional<AlertThreshold> getThresholdById(Long id) {
        return alertThresholdRepository.findById(id);
    }

    public AlertThreshold updateThreshold(Long id, String region, int thresholdCount, int timeWindowHours, Boolean active) {
        AlertThreshold threshold = alertThresholdRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Threshold not found"));
        if (region != null && !region.trim().isEmpty()) {
            threshold.setRegion(region);
        }
        if (thresholdCount > 0) {
            threshold.setThresholdCount(thresholdCount);
        }
        if (timeWindowHours > 0) {
            threshold.setTimeWindowHours(timeWindowHours);
        }
        if (active != null) {
            threshold.setActive(active);
        }
        return alertThresholdRepository.save(threshold);
    }

    public void deleteThreshold(Long id) {
        alertThresholdRepository.deleteById(id);
    }

    public AlertThreshold saveOrUpdateThreshold(String region, int thresholdCount, int timeWindowHours) {
        Optional<AlertThreshold> opt = alertThresholdRepository.findByRegion(region);
        AlertThreshold threshold;
        if (opt.isPresent()) {
            threshold = opt.get();
            threshold.setThresholdCount(thresholdCount);
            threshold.setTimeWindowHours(timeWindowHours);
            threshold.setActive(true);
        } else {
            threshold = new AlertThreshold(region, thresholdCount, timeWindowHours);
        }
        return alertThresholdRepository.save(threshold);
    }

    public List<com.lankaconnect.ccms.model.Complaint> getComplaintsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null) startDate = LocalDateTime.of(2000, 1, 1, 0, 0);
        if (endDate == null) endDate = LocalDateTime.of(2099, 12, 31, 23, 59, 59);
        return complaintRepository.findComplaintsBetweenDates(startDate, endDate);
    }

    public com.lankaconnect.ccms.model.GeneratedReport saveReport(com.lankaconnect.ccms.model.GeneratedReport report) {
        return generatedReportRepository.save(report);
    }

    public List<com.lankaconnect.ccms.model.GeneratedReport> getAllGeneratedReports() {
        return generatedReportRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<com.lankaconnect.ccms.model.GeneratedReport> getReportById(Long id) {
        return generatedReportRepository.findById(id);
    }

    public void deleteReport(Long id) {
        generatedReportRepository.deleteById(id);
    }
}

