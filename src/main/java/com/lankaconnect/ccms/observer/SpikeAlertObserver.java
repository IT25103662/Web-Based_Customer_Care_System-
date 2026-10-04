package com.lankaconnect.ccms.observer;

/**
 * Observer Pattern Interface for Regional Complaint Spike Alerts.
 */
public interface SpikeAlertObserver {
    void onSpikeDetected(String region, long currentCount, int thresholdLimit, int timeWindowHours);
}
