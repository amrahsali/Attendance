package com.example.attendance.ExamsModule;

public class ExamsModal {

    private String departmentName;

    public ExamsModal() {
        // Default constructor required for Firebase
    }

    public ExamsModal(String departmentName) {
        this.departmentName = departmentName;
    }

    public ExamsModal(String departmentName, String examName) {
        this.departmentName = departmentName;

    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
