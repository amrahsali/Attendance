package com.example.attendance.ExamsModule;

public class ExamsModal {

    private String departmentName;
    private String examId;

    public ExamsModal() {
        // Default constructor required for Firebase
    }

    public ExamsModal(String examId, String departmentName) {
        this.examId = examId;
        this.departmentName = departmentName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }
}
