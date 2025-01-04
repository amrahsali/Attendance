package com.example.attendance.Utility;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.example.attendance.ExamsModule.ExamsModal;
import com.example.attendance.ExamsModule.ExamsStudentRecordModal;
import com.example.attendance.FacultyModule.CourseModal;
import com.example.attendance.FacultyModule.FacultyModel;
import com.example.attendance.StaffModule.StaffRVModal;
import com.example.attendance.StudentModule.StudentModal;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class LocalStorageUtil {

    public static List<StaffRVModal> retrieveStaffDataFromLocalStorage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("system_staff_data", MODE_PRIVATE);
        String staffDataJson = sharedPreferences.getString("staff_data", "");

        if (!staffDataJson.isEmpty()) {
            return new Gson().fromJson(staffDataJson, new TypeToken<List<StaffRVModal>>() {}.getType());
        } else {
            return null;
        }

    }

    public static List<StudentModal> retrieveStudentDataFromLocalStorage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("system_student_data", MODE_PRIVATE);
        String studentDataJson = sharedPreferences.getString("student_data", "");

        if (!studentDataJson.isEmpty()) {
            String retrievedData = sharedPreferences.getString("student_data", "");
            Log.d(TAG, "Retrieved student data from SharedPreferences: " + retrievedData);
            return new Gson().fromJson(studentDataJson, new TypeToken<List<StudentModal>>() {}.getType());
        } else {
            Log.d(TAG, "Retrieved student data from SharedPreferences is empty ");
            return null;
        }
    }

    public static List<FacultyModel> retrieveFacultyDataFromLocalStorage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("system_faculty_data", MODE_PRIVATE);
        String facultyDataJson = sharedPreferences.getString("faculty_data", "");

        if (!facultyDataJson.isEmpty()) {
            return new Gson().fromJson(facultyDataJson, new TypeToken<List<FacultyModel>>() {}.getType());
        } else {
            return new ArrayList<>();
        }
    }

    public static List<CourseModal> retrieveCourseDataFromLocalStorage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("system_course_data", Context.MODE_PRIVATE);
        String courseDataJson = sharedPreferences.getString("course_data", "");

        if (!courseDataJson.isEmpty()) {
            return new Gson().fromJson(courseDataJson, new TypeToken<List<CourseModal>>() {}.getType());
        } else {
            return new ArrayList<>(); // Return an empty list if no data is found
        }
    }

    public static List<ExamsModal> retrieveExamDataFromLocalStorage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("system_exams_data", Context.MODE_PRIVATE);
        String examDataJson = sharedPreferences.getString("exams_data", "");

        if (!examDataJson.isEmpty()) {
            return new Gson().fromJson(examDataJson, new TypeToken<List<ExamsModal>>() {}.getType());
        } else {
            return new ArrayList<>(); // Return an empty list if no data is found
        }
    }

    public static List<ExamsStudentRecordModal> retrieveExamRecordDataFromLocalStorage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("system_exam_record_data", Context.MODE_PRIVATE);
        String examRecordData = sharedPreferences.getString("exam_record_data", "");

        if (!examRecordData.isEmpty()) {
            return new Gson().fromJson(examRecordData, new TypeToken<List<ExamsStudentRecordModal>>() {}.getType());
        } else {
            return new ArrayList<>(); // Return an empty list if no data is found
        }
    }

    public static ArrayList<ExamsStudentRecordModal> retrieveExamsRecordDataFromLocal(Context context, String examsName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("system_exam_records_data", Context.MODE_PRIVATE);
        String key = "exam_records_" + examsName;
        String examRecordsJson = sharedPreferences.getString(key, "");

        if (!examRecordsJson.isEmpty()) {
            return new Gson().fromJson(examRecordsJson, new TypeToken<ArrayList<ExamsStudentRecordModal>>() {}.getType());
        } else {
            return new ArrayList<>(); // Return empty list if no data is found
        }
    }


    public static void updateLastUpdatedTimestamp() {
        DatabaseReference lastUpdatedRef = FirebaseDatabase.getInstance().getReference().child("lastUpdated");
        lastUpdatedRef.setValue(System.currentTimeMillis());
    }

    public static void saveAndApply(String dataJson, String dataJsonTitle, String preferenceTitle, Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(preferenceTitle, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(dataJsonTitle, dataJson);
        editor.putLong("lastUpdated", System.currentTimeMillis());
        editor.apply();
    }

    public static String generateCustomToken(String uid) {
        // Replace this with your own method to generate a secure custom token
        // This example is only for demonstration purposes
        try {
            // Create the token header and payload
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Map<String, Object> payload = new HashMap<>();
            payload.put("uid", uid);

            // Convert header and payload to JSON strings
            String headerJson = new ObjectMapper().writeValueAsString(header);
            String payloadJson = new ObjectMapper().writeValueAsString(payload);

            // Encode the header and payload as Base64
            String encodedHeader = Base64.encodeToString(headerJson.getBytes(), Base64.NO_WRAP);
            String encodedPayload = Base64.encodeToString(payloadJson.getBytes(), Base64.NO_WRAP);

            // Create the token signature
            String secret = "YOUR_SECRET_KEY"; // Replace with your own secret key
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            String signature = Base64.encodeToString(sha256_HMAC.doFinal((encodedHeader + "." + encodedPayload).getBytes()), Base64.NO_WRAP);

            // Concatenate the header, payload, and signature to form the token
            return encodedHeader + "." + encodedPayload + "." + signature;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
