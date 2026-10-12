package com.edutrack.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class AssignmentRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    private String description;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    public AssignmentRequest() {}

    public AssignmentRequest(String title, Long subjectId, String description, LocalDate dueDate) {
        this.title = title;
        this.subjectId = subjectId;
        this.description = description;
        this.dueDate = dueDate;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
