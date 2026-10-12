package com.edutrack.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Assignment {
    private Long id;
    private String title;
    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private String description;
    private LocalDate dueDate;
    private String status; // "PENDING", "COMPLETED", "OVERDUE"
    private String createdBy;
    private LocalDateTime createdAt;

    public Assignment() {}

    public Assignment(Long id, String title, Long subjectId, String subjectCode, String subjectName,
                      String description, LocalDate dueDate, String status, String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getStatus() {
        // Dynamic overdue detection: if pending and due date is past today, return OVERDUE
        if ("PENDING".equalsIgnoreCase(status) && dueDate != null && dueDate.isBefore(LocalDate.now())) {
            return "OVERDUE";
        }
        return status != null ? status : "PENDING";
    }

    public void setStatus(String status) { this.status = status; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
