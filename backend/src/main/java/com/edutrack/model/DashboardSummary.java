package com.edutrack.model;

public class DashboardSummary {
    private String studentId;
    private String name;
    private String branch;
    private Integer semester;
    private Double averageMarks;
    private Double attendancePercentage;
    private Integer pendingAssignments;
    private Integer completedAssignments;

    public DashboardSummary() {}

    public DashboardSummary(String studentId, String name, String branch, Integer semester,
                            Double averageMarks, Double attendancePercentage,
                            Integer pendingAssignments, Integer completedAssignments) {
        this.studentId = studentId;
        this.name = name;
        this.branch = branch;
        this.semester = semester;
        this.averageMarks = averageMarks;
        this.attendancePercentage = attendancePercentage;
        this.pendingAssignments = pendingAssignments;
        this.completedAssignments = completedAssignments;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public Double getAverageMarks() { return averageMarks; }
    public void setAverageMarks(Double averageMarks) { this.averageMarks = averageMarks; }

    public Double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(Double attendancePercentage) { this.attendancePercentage = attendancePercentage; }

    public Integer getPendingAssignments() { return pendingAssignments; }
    public void setPendingAssignments(Integer pendingAssignments) { this.pendingAssignments = pendingAssignments; }

    public Integer getCompletedAssignments() { return completedAssignments; }
    public void setCompletedAssignments(Integer completedAssignments) { this.completedAssignments = completedAssignments; }
}
