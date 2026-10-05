package com.lankaconnect.ccms.service;

import com.lankaconnect.ccms.model.AlertThreshold;
import com.lankaconnect.ccms.repository.AlertThresholdRepository;
import com.lankaconnect.ccms.repository.ComplaintRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(AlertSchedulerService.class);

    @Autowired
    private AlertThresholdRepository thresholdRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    /**
     * Automated Threshold-Based Complaint Alerts:
     * Scheduled job periodically counts complaints raised per region over a rolling time window;
     * if the count exceeds the threshold, automatically triggers alerts for the Operations Manager.
     */
    @Scheduled(fixedRate = 20000) // Runs every 20 seconds
    public void scanRegionalComplaintSpikes() {
        List<AlertThreshold> thresholds = thresholdRepository.findByActiveTrue();
        LocalDateTime now = LocalDateTime.now();

        for (AlertThreshold threshold : thresholds) {
            LocalDateTime windowStart = now.minusHours(threshold.getTimeWindowHours());
            long count = complaintRepository.countByRegionSince(threshold.getRegion(), windowStart);

            if (count >= threshold.getThresholdCount()) {
                String alertMsg = "AUTOMATED SPIKE ALERT: " + threshold.getRegion() +
                        " region has received " + count + " complaints in the last " +
                        threshold.getTimeWindowHours() + " hour(s), exceeding maximum threshold (" +
                        threshold.getThresholdCount() + ")!";

                threshold.setLastTriggeredAt(now);
                threshold.setLastAlertMessage(alertMsg);
                thresholdRepository.save(threshold);

                log.warn("[THRESHOLD ALERT] {}", alertMsg);
            }
        }
    }
}
