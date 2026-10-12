package com.edutrack.model;

public class Subject {
    private Long id;
    private String code;
    private String name;
    private Integer credits;

    public Subject() {}

    public Subject(Long id, String code, String name, Integer credits) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.credits = credits;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }
}
