package com.example.attendance.StaffModule;


public class StudentExamPojo {
    String userId;
    String studentName;
    String matNumber;

    public String getUserId() {
        return userId;
    }

    public String getMatNumber() {
        return matNumber;
    }

    public void setMatNumber(String matNumber) {
        this.matNumber = matNumber;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }


}
