package com.example.attendance;

import android.os.Parcel;
import android.os.Parcelable;

public class StaffRVModal implements Parcelable {
    // creating variables for our different fields.
    private String productName;
    private String productDescription;
    private String department;
    private String faculty;
    private String productImg;
    private String productId;
    private String userID;

    public StaffRVModal(String courseID, String name, String email, String phone, String downloadUri, String uid) {

    }

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
        productDescription = in.readString();
        department = in.readString();
        faculty = in.readString();
        productImg = in.readString();
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

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductPrice() {
        return department;
    }

    public void setProductPrice(String productPrice) {
        this.department = productPrice;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setUserID(String userId) {
        this.userID = userId;
    }

    public String getUserID() {
        return userID;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public StaffRVModal(String productId, String productName, String productDescription, String productPrice, String productfaculty, String productImg, String userID) {
        this.productName = productName;
        this.productId = productId;
        this.productDescription = productDescription;
        this.department = productPrice;
        this.faculty = productfaculty;
        this.productImg = productImg;
        this.userID = userID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productName);
        dest.writeString(productId);
        dest.writeString(productDescription);
        dest.writeString(department);
        dest.writeString(faculty);
        dest.writeString(productImg);
        dest.writeString(userID);
    }
}