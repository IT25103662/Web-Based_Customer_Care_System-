package com.lankaconnect.ccms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert_thresholds")
public class AlertThreshold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String region; // Western, Central, Southern, Northern, Eastern, North Western, North Central, Uva, Sabaragamuwa

    @Column(nullable = false)
    private int thresholdCount = 3; // Number of complaints in rolling window to trigger alert

    @Column(nullable = false)
    private int timeWindowHours = 1; // Rolling window in hours

    private boolean active = true;

    private LocalDateTime lastTriggeredAt;

    private String lastAlertMessage;

    public AlertThreshold() {}

    public AlertThreshold(String region, int thresholdCount, int timeWindowHours) {
        this.region = region;
        this.thresholdCount = thresholdCount;
        this.timeWindowHours = timeWindowHours;
        this.active = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public int getThresholdCount() { return thresholdCount; }
    public void setThresholdCount(int thresholdCount) { this.thresholdCount = thresholdCount; }

    public int getTimeWindowHours() { return timeWindowHours; }
    public void setTimeWindowHours(int timeWindowHours) { this.timeWindowHours = timeWindowHours; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getLastTriggeredAt() { return lastTriggeredAt; }
    public void setLastTriggeredAt(LocalDateTime lastTriggeredAt) { this.lastTriggeredAt = lastTriggeredAt; }

    public String getLastAlertMessage() { return lastAlertMessage; }
    public void setLastAlertMessage(String lastAlertMessage) { this.lastAlertMessage = lastAlertMessage; }
}
