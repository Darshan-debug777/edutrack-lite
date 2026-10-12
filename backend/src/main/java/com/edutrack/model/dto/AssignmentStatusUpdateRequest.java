package com.edutrack.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AssignmentStatusUpdateRequest {
    @NotBlank(message = "Status cannot be empty")
    @Pattern(regexp = "^(?i)(PENDING|COMPLETED)$", message = "Status must be either PENDING or COMPLETED")
    private String status;

    public AssignmentStatusUpdateRequest() {}

    public AssignmentStatusUpdateRequest(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
