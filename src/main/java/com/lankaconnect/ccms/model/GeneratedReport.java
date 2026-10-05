package com.lankaconnect.ccms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "generated_reports")
public class GeneratedReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reportName;

    private String startDate;
    private String endDate;

    private String generatedBy;
    private Long managerId;

    private Integer recordCount;
    private String fileFormat;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String reportDataJson;

    private LocalDateTime createdAt = LocalDateTime.now();

    public GeneratedReport() {}

    public GeneratedReport(String reportName, String startDate, String endDate, String generatedBy, Long managerId, Integer recordCount, String fileFormat, String reportDataJson) {
        this.reportName = reportName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedBy = generatedBy;
        this.managerId = managerId;
        this.recordCount = recordCount;
        this.fileFormat = fileFormat;
        this.reportDataJson = reportDataJson;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReportName() { return reportName; }
    public void setReportName(String reportName) { this.reportName = reportName; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }

    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }

    public Integer getRecordCount() { return recordCount; }
    public void setRecordCount(Integer recordCount) { this.recordCount = recordCount; }

    public String getFileFormat() { return fileFormat; }
    public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }

    public String getReportDataJson() { return reportDataJson; }
    public void setReportDataJson(String reportDataJson) { this.reportDataJson = reportDataJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
