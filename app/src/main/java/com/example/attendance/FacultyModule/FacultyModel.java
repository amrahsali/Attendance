package com.example.attendance.FacultyModule;

public class FacultyModel {
    private String facultyId;
    private String facultyName;
    private String departmentName;

    public FacultyModel() {
        // Default constructor required for Firestore
    }

    public FacultyModel(String facultyId, String facultyName, String departmentName) {
        this.facultyId = facultyId;
        this.facultyName = facultyName;
        this.departmentName = departmentName;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
