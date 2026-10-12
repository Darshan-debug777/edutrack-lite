package com.edutrack.model;

public class Attendance {
    private Long id;
    private String studentId;
    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private Integer classesAttended;
    private Integer totalClasses;

    public Attendance() {}

    public Attendance(Long id, String studentId, Long subjectId, String subjectCode, String subjectName, Integer classesAttended, Integer totalClasses) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.classesAttended = classesAttended;
        this.totalClasses = totalClasses;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public Integer getClassesAttended() { return classesAttended; }
    public void setClassesAttended(Integer classesAttended) { this.classesAttended = classesAttended; }

    public Integer getTotalClasses() { return totalClasses; }
    public void setTotalClasses(Integer totalClasses) { this.totalClasses = totalClasses; }

    public Double getAttendancePercentage() {
        if (totalClasses == null || totalClasses <= 0 || classesAttended == null) {
            return 0.0;
        }
        return Math.round(((double) classesAttended / (double) totalClasses * 100.0) * 10.0) / 10.0;
    }
}
