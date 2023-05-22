package com.example.attendance;

public class FilterModel {
    private String level;
    private String department;
    private String faculty;

    public FilterModel(String level, String faculty, String department) {
        this.level = level;
        this.faculty = faculty;
        this.department = department;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }
}
