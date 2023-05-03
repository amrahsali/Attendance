package com.example.attendance;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class StudentModal implements Parcelable {

    private  String studentName;
    private String studentFaculty;
    private String studentDepartment;
    private String studentNumber;



    private String productId;


    protected StudentModal(Parcel in) {
        studentName = in.readString();
        studentFaculty = in.readString();
        studentDepartment = in.readString();
        studentNumber = in.readString();
        productId = in.readString();
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

    public StudentModal(String courseID, String stdName, String stdDepartment, String stdFaculty, String downloadUri, String uid) {

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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public StudentModal(String studentName, String studentFaculty, String studentDepartment, String studentNumber, String productId) {
        this.studentName = studentName;
        this.studentFaculty = studentFaculty;
        this.studentDepartment = studentDepartment;
        this.studentNumber = studentNumber;
        this.productId = productId;
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
        dest.writeString(productId);
    }
}
