package com.edutrack.model.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MarkUpdateRequest {
    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Marks obtained is required")
    @DecimalMin(value = "0.0", message = "Marks cannot be negative")
    @DecimalMax(value = "100.0", message = "Marks cannot exceed 100")
    private Double marksObtained;

    @NotNull(message = "Maximum marks is required")
    @DecimalMin(value = "1.0", message = "Maximum marks must be greater than zero")
    @DecimalMax(value = "100.0", message = "Maximum marks cannot exceed 100")
    private Double maxMarks;

    public MarkUpdateRequest() {}

    public MarkUpdateRequest(String studentId, Long subjectId, Double marksObtained, Double maxMarks) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public Double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(Double marksObtained) { this.marksObtained = marksObtained; }

    public Double getMaxMarks() { return maxMarks; }
    public void setMaxMarks(Double maxMarks) { this.maxMarks = maxMarks; }
}
