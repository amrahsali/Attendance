package com.example.attendance.FacultyModule;

import java.util.List;
import java.util.Map;

public class FacultyModel {
    private String fid;
    private String name;
    private List<String> dept; // Updated to store department names

    public FacultyModel() {
        // Default constructor required for Firebase
    }

    public FacultyModel(String facultyId, String facultyName, List<String> departmentNames) {
        this.fid = facultyId;
        this.name = facultyName;
        this.dept = departmentNames;
    }

    public String getFId() {
        return fid;
    }

    public void setFId(String facultyId) {
        this.fid = facultyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String facultyName) {
        this.name = facultyName;
    }

    public List<String> getDept() {
        return dept;
    }

    public void setDept(List<String> departmentNames) {
        this.dept = departmentNames;
    }
}

