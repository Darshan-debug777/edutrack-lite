package com.edutrack.model;

public class Mark {
    private Long id;
    private String studentId;
    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private Double marksObtained;
    private Double maxMarks;

    public Mark() {}

    public Mark(Long id, String studentId, Long subjectId, String subjectCode, String subjectName, Double marksObtained, Double maxMarks) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks;
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

    public Double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(Double marksObtained) { this.marksObtained = marksObtained; }

    public Double getMaxMarks() { return maxMarks; }
    public void setMaxMarks(Double maxMarks) { this.maxMarks = maxMarks; }

    public Double getPercentage() {
        if (maxMarks == null || maxMarks <= 0 || marksObtained == null) return 0.0;
        return Math.round((marksObtained / maxMarks * 100.0) * 100.0) / 100.0;
    }
}
