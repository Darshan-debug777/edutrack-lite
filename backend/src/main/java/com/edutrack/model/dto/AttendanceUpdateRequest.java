package com.edutrack.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AttendanceUpdateRequest {
    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Classes attended is required")
    @Min(value = 0, message = "Classes attended cannot be negative")
    private Integer classesAttended;

    @NotNull(message = "Total classes is required")
    @Min(value = 0, message = "Total classes cannot be negative")
    private Integer totalClasses;

    public AttendanceUpdateRequest() {}

    public AttendanceUpdateRequest(String studentId, Long subjectId, Integer classesAttended, Integer totalClasses) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.classesAttended = classesAttended;
        this.totalClasses = totalClasses;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public Integer getClassesAttended() { return classesAttended; }
    public void setClassesAttended(Integer classesAttended) { this.classesAttended = classesAttended; }

    public Integer getTotalClasses() { return totalClasses; }
    public void setTotalClasses(Integer totalClasses) { this.totalClasses = totalClasses; }
}
