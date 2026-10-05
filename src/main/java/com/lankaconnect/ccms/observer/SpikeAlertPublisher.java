package com.lankaconnect.ccms.observer;

/**
 * Subject Interface for Observer Pattern.
 */
public interface SpikeAlertPublisher {
    void attach(SpikeAlertObserver observer);
    void detach(SpikeAlertObserver observer);
    void notifyObservers(String region, long currentCount, int thresholdLimit, int timeWindowHours);
}
