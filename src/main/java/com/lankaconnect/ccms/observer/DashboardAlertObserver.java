package com.lankaconnect.ccms.observer;

import org.springframework.stereotype.Component;

/**
 * Concrete Observer that handles manager dashboard alert notifications.
 */
@Component
public class DashboardAlertObserver implements SpikeAlertObserver {
    @Override
    public void onSpikeDetected(String region, long currentCount, int thresholdLimit, int timeWindowHours) {
        System.out.println("[OBSERVER ALERT] Dashboard Notification Triggered for Region: " + region +
                " (" + currentCount + " complaints in past " + timeWindowHours + "h exceeds limit of " + thresholdLimit + ")");
    }
}
