package com.lankaconnect.ccms.observer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Subject for Observer Pattern.
 * Maintains registered SpikeAlertObservers and notifies them when regional thresholds are exceeded.
 */
@Component
public class RegionalSpikeAlertPublisher implements SpikeAlertPublisher {

    private final List<SpikeAlertObserver> observers = new ArrayList<>();

    @Autowired
    public RegionalSpikeAlertPublisher(DashboardAlertObserver dashboardObserver, AuditLogAlertObserver auditObserver) {
        attach(dashboardObserver);
        attach(auditObserver);
    }

    @Override
    public void attach(SpikeAlertObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(SpikeAlertObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String region, long currentCount, int thresholdLimit, int timeWindowHours) {
        for (SpikeAlertObserver observer : observers) {
            observer.onSpikeDetected(region, currentCount, thresholdLimit, timeWindowHours);
        }
    }
}
