package com.lankaconnect.ccms.observer;

import com.lankaconnect.ccms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer that logs spike alert events into System Audit Log.
 */
@Component
public class AuditLogAlertObserver implements SpikeAlertObserver {

    @Autowired
    private UserService userService;

    @Override
    public void onSpikeDetected(String region, long currentCount, int thresholdLimit, int timeWindowHours) {
        if (userService != null) {
            userService.logActivity(
                    1L, "SystemAlertEngine", "SYSTEM",
                    "REGIONAL_SPIKE_ALERT",
                    "Automated Spike Alert triggered for " + region + ": " + currentCount + " complaints in past " + timeWindowHours + "h (Limit: " + thresholdLimit + ")",
                    "127.0.0.1"
            );
        }
    }
}
