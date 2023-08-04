package com.example.attendance.ExamsModule;

import java.util.ArrayList;

public class ExamsStudentRecordModal {

    private String studentName;

    private String studentMatricNo;


    public ExamsStudentRecordModal() {
        // Default constructor required for Firebase
    }

    public ExamsStudentRecordModal(String studentName, String studentMatricNo) {
        this.studentName = studentName;
        this.studentMatricNo = studentMatricNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentMatricNo() {
        return studentMatricNo;
    }

    public void setStudentMatricNo(String studentMatricNo) {
        this.studentMatricNo = studentMatricNo;
    }
}
