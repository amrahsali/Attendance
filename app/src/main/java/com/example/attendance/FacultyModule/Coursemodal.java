package com.example.attendance.FacultyModule;

public class Coursemodal {

    private String courseName;
    private String codeName;

    private String codeNameLowerCase;

    public Coursemodal() {
        // empty constructor required for firebase.
    }

    public String getCodeNameLowerCase() {
        return codeNameLowerCase;
    }

    public void setCodeNameLowerCase(String codeNameLowerCase) {
        this.codeNameLowerCase = codeNameLowerCase;
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
