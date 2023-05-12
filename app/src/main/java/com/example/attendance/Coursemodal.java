package com.example.attendance;

public class Coursemodal {

    private String courseName;
    private String codeName;

    public Coursemodal() {
        // empty constructor required for firebase.
    }

    public Coursemodal(String courseName, String codeName) {
        this.courseName = courseName;
        this.codeName = codeName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }
}
