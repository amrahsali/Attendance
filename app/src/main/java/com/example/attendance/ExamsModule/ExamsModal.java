package com.example.attendance.ExamsModule;

import java.util.ArrayList;

public class ExamsModal {

    private String courseName;

    private ArrayList<String> invigilators;
    private String examId;

    private String time;
    private String endTime;

    public ExamsModal() {
        // Default constructor required for Firebase
    }

    public ExamsModal(String examId, String courseName,ArrayList<String> invigilators, String time, String endTime) {
        this.examId = examId;
        this.courseName = courseName;
        this.invigilators = invigilators;
        this.time = time;
        this.endTime = endTime;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String time){
        this.time = time;
    }

    public ArrayList<String> getInvigilators(){
        return invigilators;
    }

    public void setInvigilators(ArrayList<String> invigilators){
        this.invigilators = invigilators;
    }
}
