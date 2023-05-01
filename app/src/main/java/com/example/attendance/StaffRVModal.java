package com.example.attendance;

import android.os.Parcel;
import android.os.Parcelable;

public class StaffRVModal implements Parcelable {
    // creating variables for our different fields.
    private String productName;
    private String department;
    private String faculty;
    private String productImg;
    private String productId;
    private String userID;
    private String phone;
    private String email;


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    // creating an empty constructor.
    public StaffRVModal() {

    }

    protected StaffRVModal(Parcel in) {
        productName = in.readString();
        productId = in.readString();
        department = in.readString();
        faculty = in.readString();
        productImg = in.readString();
        phone = in.readString();
        email = in.readString();
    }

    public static final Creator<StaffRVModal> CREATOR = new Creator<StaffRVModal>() {
        @Override
        public StaffRVModal createFromParcel(Parcel in) {
            return new StaffRVModal(in);
        }

        @Override
        public StaffRVModal[] newArray(int size) {
            return new StaffRVModal[size];
        }
    };

    // creating getter and setter methods.
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public void setUserID(String userId) {
        this.userID = userId;
    }

    public String getUserID() {
        return userID;
    }

    public void setEmail(String email) {
            this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public StaffRVModal(String productId, String productName, String staffEmail, String staffPhone,
                        String productImg, String userID, String faculty, String department) {
        this.productName = productName;
        this.productId = productId;
        this.department = department;
        this.faculty = faculty;
        this.productImg = productImg;
        this.userID = userID;
        this.phone = staffPhone;
        this.email = staffEmail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productName);
        dest.writeString(productId);
        dest.writeString(department);
        dest.writeString(faculty);
        dest.writeString(productImg);
        dest.writeString(userID);
    }
}