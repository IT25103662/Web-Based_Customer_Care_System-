package com.lankaconnect.ccms.controller;

import com.lankaconnect.ccms.model.AlertThreshold;
import com.lankaconnect.ccms.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private ReportingService reportingService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(reportingService.getDashboardStatistics());
    }

    @GetMapping("/spike-alerts")
    public ResponseEntity<List<Map<String, Object>>> getSpikeAlerts() {
        return ResponseEntity.ok(reportingService.getTriggeredSpikeAlerts());
    }

    @GetMapping("/thresholds")
    public ResponseEntity<List<AlertThreshold>> getThresholds() {
        return ResponseEntity.ok(reportingService.getAllThresholds());
    }

    @GetMapping("/thresholds/{id}")
    public ResponseEntity<?> getThresholdById(@PathVariable Long id) {
        return reportingService.getThresholdById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/thresholds/{id}")
    public ResponseEntity<?> updateThreshold(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            String region = (String) payload.get("region");
            int thresholdCount = payload.get("thresholdCount") != null ? Integer.parseInt(payload.get("thresholdCount").toString()) : 0;
            int timeWindowHours = payload.get("timeWindowHours") != null ? Integer.parseInt(payload.get("timeWindowHours").toString()) : 0;
            Boolean active = payload.get("active") != null ? Boolean.parseBoolean(payload.get("active").toString()) : null;

            AlertThreshold updated = reportingService.updateThreshold(id, region, thresholdCount, timeWindowHours, active);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/thresholds/{id}")
    public ResponseEntity<?> deleteThreshold(@PathVariable Long id) {
        try {
            reportingService.deleteThreshold(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Alert threshold deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/thresholds")
    public ResponseEntity<?> saveThreshold(@RequestBody Map<String, Object> payload) {
        try {
            String region = (String) payload.get("region");
            int thresholdCount = Integer.parseInt(payload.get("thresholdCount").toString());
            int timeWindowHours = Integer.parseInt(payload.get("timeWindowHours").toString());

            AlertThreshold saved = reportingService.saveOrUpdateThreshold(region, thresholdCount, timeWindowHours);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/reports/filter")
    public ResponseEntity<?> getFilteredReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            java.time.LocalDateTime start = null;
            java.time.LocalDateTime end = null;
            if (startDate != null && !startDate.trim().isEmpty()) {
                start = java.time.LocalDate.parse(startDate).atStartOfDay();
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                end = java.time.LocalDate.parse(endDate).atTime(23, 59, 59);
            }
            List<com.lankaconnect.ccms.model.Complaint> list = reportingService.getComplaintsByDateRange(start, end);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reports")
    public ResponseEntity<?> saveReport(@RequestBody com.lankaconnect.ccms.model.GeneratedReport report) {
        try {
            com.lankaconnect.ccms.model.GeneratedReport saved = reportingService.saveReport(report);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/reports")
    public ResponseEntity<List<com.lankaconnect.ccms.model.GeneratedReport>> getAllReports() {
        return ResponseEntity.ok(reportingService.getAllGeneratedReports());
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<?> getReportById(@PathVariable Long id) {
        return reportingService.getReportById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/reports/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
        try {
            reportingService.deleteReport(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Report deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

