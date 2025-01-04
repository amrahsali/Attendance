package com.example.attendance.FacultyModule;

import java.util.HashMap;
import java.util.Map;

public class CourseModal {

    private String courseName;
    private String codeName;

    private String codeNameLowerCase;

    public CourseModal() {
        // empty constructor required for firebase.
    }

    public String getCodeNameLowerCase() {
        return codeNameLowerCase;
    }

    public void setCodeNameLowerCase(String codeNameLowerCase) {
        this.codeNameLowerCase = codeNameLowerCase;
    }



    public CourseModal(String courseName, String codeName) {
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

    // Method to convert the object to a Map
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("courseName", courseName);
        map.put("codeName", codeName);
        return map;
    }
}
