package com.example.attendance.StudentModule;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class StudentModal implements Parcelable {

    private  String studentName;
    private String studentFaculty;
    private String studentDepartment;
    private String studentNumber;
    private String studentImage;
    private String studentLevel;
    private String userID;

    private ArrayList<String> courses;




    // creating an empty constructor.
    public StudentModal() {

    }

    private String studentID;


    protected StudentModal(Parcel in) {
        studentName = in.readString();
        studentFaculty = in.readString();
        studentDepartment = in.readString();
        studentNumber = in.readString();
        studentID = in.readString();
        studentImage = in.readString();
        studentLevel = in.readString();
        userID = in.readString();
        courses = in.readArrayList(getClass().getClassLoader());
    }

    public static final Creator<StudentModal> CREATOR = new Creator<StudentModal>() {
        @Override
        public StudentModal createFromParcel(Parcel in) {
            return new StudentModal(in);
        }

        @Override
        public StudentModal[] newArray(int size) {
            return new StudentModal[size];
        }
    };


    public ArrayList<String> getCourses(){
        return courses;
    }

    public void setCourses(ArrayList<String> courses){
        this.courses = courses;
    }
    public String getProductImg() {
        return studentImage;
    }

    public void setProductImg(String studentImage){
        this.studentImage = studentImage;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentFaculty() {
        return studentFaculty;
    }

    public void setStudentFaculty(String studentFaculty) {
        this.studentFaculty = studentFaculty;
    }

    public String getStudentDepartment() {
        return studentDepartment;
    }

    public void setStudentDepartment(String studentDepartment) {
        this.studentDepartment = studentDepartment;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setUserID(String userId) {
        this.userID = userId;
    }

    public String getUserID() {
        return userID;
    }


    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getStudentLevel() {
            return studentLevel;
        }

        public void setStudentLevel(String studentLevel) {
            this.studentLevel = studentLevel;
        }

    public StudentModal(String studentID, String studentName, String studentFaculty, String studentDepartment,
                        String studentNumber, String uid, String studentLevel, String studentImage, ArrayList<String> courses) {
        this.studentName = studentName;
        this.studentFaculty = studentFaculty;
        this.studentDepartment = studentDepartment;
        this.studentNumber = studentNumber;
        this.studentID = studentID;
        this.userID = uid;
        this.studentLevel = studentLevel;
        this.studentImage = studentImage;
        this.courses = courses;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(studentName);
        dest.writeString(studentFaculty);
        dest.writeString(studentDepartment);
        dest.writeString(studentNumber);
        dest.writeString(studentID);
        dest.writeString(studentImage);
        dest.writeString(studentLevel);
        dest.writeString(userID);
        dest.writeList(courses);
    }
}
