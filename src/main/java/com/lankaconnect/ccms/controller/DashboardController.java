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
}
